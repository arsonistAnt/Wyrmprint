package com.example.wyrmprint.ui.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wyrmprint.databinding.FragBrowseLayoutBinding
import com.example.wyrmprint.ui.browse.adapters.ThumbnailItem
import com.example.wyrmprint.ui.viewmodel.BrowserViewModel
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter

/**
 * Fragment that hosts the UI for viewing the thumbnail items of the Dragalia Life API.
 */
class BrowseFragment : Fragment() {
    private lateinit var binding: FragBrowseLayoutBinding
    private lateinit var thumbnailItemAdapter: ItemAdapter<ThumbnailItem>
    private lateinit var browserViewModel: BrowserViewModel

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
        thumbnailItemAdapter = ItemAdapter()
        browserViewModel = ViewModelProviders.of(this)[BrowserViewModel::class.java]
        binding.browserViewModel = browserViewModel
        initBrowserViewModelObservables(browserViewModel)
    }

    /**
     * Setup the browser recycler view.
     *
     * @param browser the RecyclerView to setup.
     */
    private fun initBrowserRecyclerView(browser: RecyclerView) {
        browser.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = FastAdapter.with(thumbnailItemAdapter)
            binding.browserViewModel?.fetchThumbnailPage(0)
        }
    }

    /**
     * Setup the live data to be observed.
     *
     * @param browserViewModel the BrowserViewModel who's observables that will be subscribed to.
     */
    private fun initBrowserViewModelObservables(browserViewModel: BrowserViewModel) {
        browserViewModel.thumbnailPage.observe(viewLifecycleOwner, Observer { thumbnailItems ->
            thumbnailItemAdapter.add(thumbnailItems)
        })
    }
}