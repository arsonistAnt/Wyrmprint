package com.example.wyrmprint.ui.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wyrmprint.data.model.ComicThumbnailData
import com.example.wyrmprint.databinding.FragBrowseLayoutBinding
import com.example.wyrmprint.injection.injector
import com.example.wyrmprint.injection.viewModel
import com.example.wyrmprint.ui.browse.viewholder.ThumbnailItemView
import com.example.wyrmprint.ui.viewmodel.BrowserViewModel
import com.example.wyrmprint.util.toThumbnailItemView
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
    private lateinit var browsePageAdapter: GenericPagedModelAdapter<ComicThumbnailData>
    private lateinit var progressFooterAdapter: GenericItemAdapter
    private val browserViewModel: BrowserViewModel by viewModel { injector.browserViewModel }

    companion object {
        // Diff config for the comic thumbnail paged model adapter.
        val comicThumbnailDiff = AsyncDifferConfig.Builder<ComicThumbnailData>(object :
            DiffUtil.ItemCallback<ComicThumbnailData>() {
            override fun areItemsTheSame(
                oldItem: ComicThumbnailData,
                newItem: ComicThumbnailData
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ComicThumbnailData,
                newItem: ComicThumbnailData
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

    /**
     * Initialize any objects/views before use.
     */
    private fun initSetup() {
        browsePageAdapter = GenericPagedModelAdapter(comicThumbnailDiff) { comicThumbnail ->
            // Wrap into ThumbnailItemView
            comicThumbnail.toThumbnailItemView()
        }
        progressFooterAdapter = GenericItemAdapter()
        binding.browserViewModel = browserViewModel
        initBrowserViewModelObservables(browserViewModel)
        setDataSourceCallbacks()
    }

    /**
     * Setup the browser recycler view.
     *
     * @param browser the RecyclerView to setup.
     */
    private fun initBrowserRecyclerView(browser: RecyclerView) {
        // Create a generic fast adapter that will take in a paged model adapter and a generic item adapter.
        val fastAdapter = GenericFastAdapter.with<IItem<*>, IAdapter<IItem<*>>>(
            listOf(
                browsePageAdapter,
                progressFooterAdapter
            )
        ).apply {
            registerTypeInstance(ThumbnailItemView(null))
        }

        // Init grid layout manager & set span size lookup for ProgressItem.
        val gridLayoutManager = GridLayoutManager(requireContext(), 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (fastAdapter.getItemViewType(position) == ProgressItem().type)
                        return 2
                    return 1
                }
            }
        }
        browser.apply {
            // Ui,
            layoutManager = gridLayoutManager
            // Config adapter
            adapter = fastAdapter
            addOnScrollListener(object : EndlessRecyclerOnScrollListener(progressFooterAdapter) {
                override fun onLoadMore(currentPage: Int) {
                    progressFooterAdapter.clear()
                    progressFooterAdapter.add(ProgressItem())
                }
            })
        }
    }

    /**
     * Setup the live data to be observed.
     *
     * @param browserViewModel the BrowserViewModel who's observables that will be subscribed to.
     */
    private fun initBrowserViewModelObservables(browserViewModel: BrowserViewModel) {
        browserViewModel.thumbnailDataItemPageList.observe(
            viewLifecycleOwner,
            Observer { thumbnailPagedList ->
                browsePageAdapter.submitList(thumbnailPagedList)
            })
    }

    /**
     * Set data source callback functions.
     */
    private fun setDataSourceCallbacks() {
        browserViewModel.setOnInitialLoadedThumbnail {
            requireActivity().browser_progressBar.visibility = View.GONE
        }
        browserViewModel.setOnLoadedMoreThumbnail {
            progressFooterAdapter.clear()
        }
    }
}