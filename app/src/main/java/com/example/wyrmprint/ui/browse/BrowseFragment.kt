package com.example.wyrmprint.ui.browse

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.example.wyrmprint.R
import com.example.wyrmprint.data.model.ThumbnailData
import com.example.wyrmprint.databinding.FragBrowseLayoutBinding
import com.example.wyrmprint.injection.injector
import com.example.wyrmprint.injection.viewModel
import com.example.wyrmprint.ui.browse.viewholder.ThumbnailItemView
import com.example.wyrmprint.ui.viewmodel.BrowserViewModel
import com.example.wyrmprint.util.toThumbnailItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mikepenz.fastadapter.GenericFastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.paged.ExperimentalPagedSupport
import com.mikepenz.fastadapter.paged.GenericPagedModelAdapter
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import com.mikepenz.fastadapter.ui.items.ProgressItem
import kotlinx.android.synthetic.main.activity_main.*

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

    companion object {
        // Diff config for the comic thumbnail paged model adapter.
        val comicThumbnailDiff = AsyncDifferConfig.Builder<ThumbnailData>(object :
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

        requireActivity().findViewById<BottomNavigationView>(R.id.main_navbar_bottom)
            .setOnNavigationItemSelectedListener {
                if (it.itemId == R.id.item_setting) {
                    browserViewModel.refreshAsync()
                }
                true
            }

        // Initialize the recycler view.
        initBrowserRecyclerView(binding.browserRecycler)
        return binding.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        footerItemAdapter.clear()
        reconfigRecycler(binding.browserRecycler, fastAdapter)
    }

    /**
     * Initialize any objects/views before use.
     */
    private fun initSetup() {
        browsePageItemAdapter =
            GenericPagedModelAdapter(comicThumbnailDiff, { ProgressItem() }) { comicThumbnail ->
                // Wrap into ThumbnailItemView
                comicThumbnail.toThumbnailItemView()
            }
        footerItemAdapter = GenericItemAdapter()
        binding.browserViewModel = browserViewModel
        initObservables(browserViewModel)
    }

    /**
     * Setup the browser recycler view.
     *
     * @param browser the RecyclerView to setup.
     */
    private fun initBrowserRecyclerView(browser: RecyclerView) {
        // Create a generic fast adapter that will take in a paged model adapter and a generic item adapter.
        fastAdapter = GenericFastAdapter.with<IItem<*>, IAdapter<IItem<*>>>(
            listOf(
                browsePageItemAdapter,
                footerItemAdapter
            )
        ).apply {
            registerTypeInstance(ThumbnailItemView(null))
            onClickListener = { view, adapter, item, position ->
                (item as ThumbnailItemView).thumbnailData?.apply {
                    val action = BrowseFragmentDirections.actionBrowseFragmentToComicPagerActivity(
                        comicUrl,
                        id
                    )
                    findNavController().navigate(action)
                }
                false
            }
        }

        // Init grid layout manager & set span size lookup for ProgressItem.
        val gridLayoutManager = getGridLayoutManager(fastAdapter)

        browser.apply {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            itemAnimator = null
            // Ui,
            layoutManager = gridLayoutManager
            // Config adapter
            adapter = fastAdapter.apply {
                registerTypeInstance(ProgressItem())
            }
            addOnScrollListener(object : EndlessRecyclerOnScrollListener(footerItemAdapter) {
                override fun onLoadMore(currentPage: Int) {
                    footerItemAdapter.clear()
                    footerItemAdapter.add(ProgressItem())
                }
            })
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
                    requireActivity().browser_progressBar.visibility = View.GONE
                    browsePageItemAdapter.submitList(thumbnailPagedList, Runnable {
                        footerItemAdapter.clear()
                        val layoutManager =
                            binding.browserRecycler.layoutManager as GridLayoutManager
                        val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                        if (position != RecyclerView.NO_POSITION) {
                            binding.browserRecycler.scrollToPosition(position)
                        }
                    })
                })
        }
    }

    /**
     * Return a configured GridLayoutManager for the [GenericFastAdapter].
     *
     * @param fastAdapter the fast adapter to help configure the GridLayoutManager.
     */
    private fun getGridLayoutManager(fastAdapter: GenericFastAdapter): GridLayoutManager {
        val currentSpanCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> SPAN_COUNT_LANDSCAPE
            else -> SPAN_COUNT_PORTRAIT
        }
        return GridLayoutManager(requireContext(), currentSpanCount).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (fastAdapter.getItemViewType(position) == ProgressItem().type)
                        return 2
                    return 1
                }
            }
        }
    }

    /**
     * Reconfigure the RecyclerView on orientation change.
     *
     * @see onConfigurationChanged
     */
    private fun reconfigRecycler(
        recycler: RecyclerView,
        fastAdapter: GenericFastAdapter
    ) {
        recycler.layoutManager = getGridLayoutManager(fastAdapter)
    }
}