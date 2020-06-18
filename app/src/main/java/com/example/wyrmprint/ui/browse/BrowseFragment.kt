package com.example.wyrmprint.ui.browse

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wyrmprint.data.model.NetworkStatus
import com.example.wyrmprint.data.model.ThumbnailData
import com.example.wyrmprint.data.model.toThumbnailItemView
import com.example.wyrmprint.databinding.FragBrowseLayoutBinding
import com.example.wyrmprint.injection.injector
import com.example.wyrmprint.injection.viewModel
import com.example.wyrmprint.ui.browse.viewholder.RetryItemView
import com.example.wyrmprint.ui.browse.viewholder.ThumbnailItemView
import com.example.wyrmprint.ui.viewmodels.BrowserViewModel
import com.mikepenz.fastadapter.GenericFastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.paged.ExperimentalPagedSupport
import com.mikepenz.fastadapter.paged.GenericPagedModelAdapter
import com.mikepenz.fastadapter.ui.items.ProgressItem

/**
 * Fragment that hosts the UI for viewing the thumbnail items of the Dragalia Life API.
 */
@ExperimentalPagedSupport
class BrowseFragment : Fragment() {
    private lateinit var binding: FragBrowseLayoutBinding
    private lateinit var browsePageItemAdapter: GenericPagedModelAdapter<ThumbnailData>
    private lateinit var footerItemAdapter: GenericItemAdapter
    private lateinit var fastAdapter: GenericFastAdapter
    private val browserViewModel: BrowserViewModel by viewModel { injector.browserViewModel }

    // Default span count for different orientations.
    private val SPAN_COUNT_PORTRAIT = 2
    private val SPAN_COUNT_LANDSCAPE = 4

    // Keep track if user has swiped to refresh.
    private var userSwiped = false

    companion object {
        // Diff config for the comic thumbnail paged model adapter.
        val comicThumbnailDiff = AsyncDifferConfig.Builder(object :
            DiffUtil.ItemCallback<ThumbnailData>() {
            override fun areItemsTheSame(
                oldItem: ThumbnailData,
                newItem: ThumbnailData
            ): Boolean {
                return oldItem.comicId == newItem.comicId
            }

            override fun areContentsTheSame(
                oldItem: ThumbnailData,
                newItem: ThumbnailData
            ): Boolean {
                return oldItem == newItem
            }
        }).build()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Init any objects or inflate views for usage.
        binding = FragBrowseLayoutBinding.inflate(layoutInflater, container, false)
        initSetup()
        // Initialize the recycler view.
        initBrowserRecyclerView(binding.browserRecycler)
        return binding.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        footerItemAdapter.clear()
        reconfigureRecycler(binding.browserRecycler, fastAdapter)
    }

    /**
     * Initialize any objects/views before use.
     */
    private fun initSetup() {
        browsePageItemAdapter = GenericPagedModelAdapter(comicThumbnailDiff) { thumbnailItem ->
            // Wrap into ThumbnailItemView
            thumbnailItem.toThumbnailItemView()
        }
        footerItemAdapter = GenericItemAdapter()
        binding.browserViewModel = browserViewModel
        binding.browseSwipeRefresh.setOnRefreshListener {
            browserViewModel.invalidateThumbnailData()
            userSwiped = true
        }

        initObservables(browserViewModel)
    }

    private fun createFastAdapter() = GenericFastAdapter.with<IItem<*>, IAdapter<IItem<*>>>(
        listOf(
            browsePageItemAdapter,
            footerItemAdapter
        )
    ).apply {
        registerTypeInstance(ThumbnailItemView(null))
        onClickListener = { _, _, item, _ ->
            (item as ThumbnailItemView).thumbnailData?.apply {
                val action = BrowseFragmentDirections.actionBrowseFragmentToComicPagerActivity(
                    comicUrl,
                    comicId
                )
                findNavController().navigate(action)
            }
            true
        }
        onLongClickListener = { _, _, item, _ ->
            (item as ThumbnailItemView).thumbnailData?.apply {
                browserViewModel.addToFavorites(this)
                Toast.makeText(
                    requireContext(),
                    "#$comicNumber added to favorites!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            true
        }
    }

    /**
     * Setup the browser recycler view.
     *
     * @param browser the RecyclerView to setup.
     */
    private fun initBrowserRecyclerView(browser: RecyclerView) {
        // Create a generic fast adapter that will take in a paged model adapter and a generic item adapter.
        fastAdapter = createFastAdapter()

        // Init grid layout manager & set span size lookup for ProgressItem.
        val gridLayoutManager = getGridLayoutManager(fastAdapter)

        browser.apply {
            // Ui,
            layoutManager = gridLayoutManager
            // Config adapter
            adapter = fastAdapter
        }
    }

    /**
     * Setup the live data to be observed.
     *
     * @param browserViewModel the BrowserViewModel who's observables that will be subscribed to.
     */
    private fun initObservables(browserViewModel: BrowserViewModel) {
        browserViewModel.apply {
            thumbnailDataItemPageList.observe(
                viewLifecycleOwner,
                Observer { thumbnailPagedList ->
                    updateThumbnailPagedList(thumbnailPagedList)
                })

            thumbnailNetworkStatus.observe(
                viewLifecycleOwner,
                Observer {
                    handleThumbnailRequest(it)
                }
            )
        }
    }

    /**
     * Respond to the thumbnail request's [NetworkStatus] state with the appropriate UI or debug logging.
     *
     * @param networkState the network state.
     */
    private fun handleThumbnailRequest(networkState: NetworkStatus) {
        when {
            networkState.hasError() -> showRetryButton(networkState)
            networkState.inProgress -> showLoadingProgressUI()
            networkState.success -> onRequestSuccess()
        }
    }

    /**
     * Return the appropriate [GridLayoutManager] for the fast adapter passed in the function.
     *
     * @param fastAdapter the [GenericFastAdapter] that will help configure the GridLayoutManager.
     *
     * @return the [GridLayoutManager] object configured for the [fastAdapter]
     */
    private fun getGridLayoutManager(fastAdapter: GenericFastAdapter): GridLayoutManager {
        val currentSpanCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> SPAN_COUNT_LANDSCAPE
            else -> SPAN_COUNT_PORTRAIT
        }
        return constructGridLayoutManager(fastAdapter, currentSpanCount)
    }

    /**
     * Constructs the grid layout manager.
     *
     * @param currentSpanCount the current span count, usually dependent on orientation type and item ViewHolder type.
     */
    private fun constructGridLayoutManager(
        fastAdapter: GenericFastAdapter,
        currentSpanCount: Int
    ): GridLayoutManager =
        GridLayoutManager(requireContext(), currentSpanCount).apply {
            // Set the span size for the footer items.
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int) =
                    when (fastAdapter.getItemViewType(position)) {
                        ProgressItem().type -> currentSpanCount
                        RetryItemView {}.type -> currentSpanCount
                        else -> 1
                    }
            }
        }

    /**
     * Reconfigure the RecyclerView on orientation change.
     *
     * @see onConfigurationChanged
     */
    private fun reconfigureRecycler(
        recycler: RecyclerView,
        fastAdapter: GenericFastAdapter
    ) {
        recycler.layoutManager = getGridLayoutManager(fastAdapter)
    }

    /**
     * Initialize or update the thumbnail paged list to the UI.
     *
     * @param thumbnailPagedList the [PagedList] to submit to the [GenericItemAdapter] for the thumbnail browser.
     */
    private fun updateThumbnailPagedList(thumbnailPagedList: PagedList<ThumbnailData>) {
        if (binding.browseSwipeRefresh.isRefreshing) {
            // Re-create the adapter to properly refresh data.
            browsePageItemAdapter.submitList(thumbnailPagedList, Runnable {
                footerItemAdapter.clear()
                fastAdapter = createFastAdapter()
                binding.browserRecycler.apply {
                    adapter = fastAdapter
                    layoutManager = getGridLayoutManager(fastAdapter)
                }
            })
        } else
            browsePageItemAdapter.submitList(thumbnailPagedList)
    }

    /**
     * Two progress bar needs to be kept in check:
     *      1. Swipe to refresh.
     *      2. New data is being loaded into the pagination.
     *
     * This function helps decide which progress bar to show.
     */
    private fun showLoadingProgressUI() {
        if (!userSwiped) {
            footerItemAdapter.add(ProgressItem())
        }
    }

    /**
     * Cleanup UI or post UI notifications after successful network request.
     */
    private fun onRequestSuccess() {
        hideLoadingProgress()
    }

    /**
     * Show retry UI when error occurs in the network.
     *
     * @param networkState the current state of the network request.
     */
    private fun showRetryButton(networkState: NetworkStatus) {
        hideLoadingProgress()
        val errorMessage = networkState.err?.message ?: "An unexpected error has occurred."
        // Add retry button.
        footerItemAdapter.add(RetryItemView {
            browserViewModel.invalidateThumbnailData()
            footerItemAdapter.clear()
        })

        // Output error message.
        Toast.makeText(
            requireContext(),
            errorMessage,
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Hide all loading progress bars.
     */
    private fun hideLoadingProgress() {
        binding.browseSwipeRefresh.isRefreshing = false
        userSwiped = false
        footerItemAdapter.clear()
    }
}