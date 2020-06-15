package com.example.wyrmprint.ui.favorite

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wyrmprint.data.model.toThumbnailItemView
import com.example.wyrmprint.databinding.FragFavoriteLayoutBinding
import com.example.wyrmprint.injection.injector
import com.example.wyrmprint.injection.viewModel
import com.example.wyrmprint.ui.browse.viewholder.RetryItemView
import com.example.wyrmprint.ui.browse.viewholder.ThumbnailItemView
import com.example.wyrmprint.ui.viewmodels.FavoriteViewModel
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.ui.items.ProgressItem
import java.util.*

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragFavoriteLayoutBinding
    private val viewModel: FavoriteViewModel by viewModel {
        injector.favoriteViewModel
    }

    // Default span count for different orientations.
    private val SPAN_COUNT_PORTRAIT = 2
    private val SPAN_COUNT_LANDSCAPE = 4

    private var favoriteItemAdapter : ItemAdapter<ThumbnailItemView>? = null
    private var fastAdapter : FastAdapter<ThumbnailItemView>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragFavoriteLayoutBinding.inflate(inflater, container, false)
        setupFavoritesBrowser(binding.favoritesRecycler)
        initObservers(viewModel)
        return binding.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        fastAdapter?.let {
            binding.favoritesRecycler.layoutManager = getGridLayoutManager(it)
        }
    }

    /**
     * Return the appropriate [GridLayoutManager] for the fast adapter passed in the function.
     *
     * @param fastAdapter the [FastAdapter] that will help configure the GridLayoutManager.
     *
     * @return the [GridLayoutManager] object configured for the [fastAdapter]
     */
    private fun getGridLayoutManager(fastAdapter: FastAdapter<ThumbnailItemView>): GridLayoutManager {
        val currentSpanCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> SPAN_COUNT_LANDSCAPE
            else -> SPAN_COUNT_PORTRAIT
        }

        return GridLayoutManager(requireContext(), currentSpanCount).apply {
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
    }

    /**
     * Setup the favorites recycler view.
     *
     * @param favoritesRecycler the [RecyclerView] that's been inflated.
     */
    private fun setupFavoritesBrowser(favoritesRecycler : RecyclerView){
        favoriteItemAdapter = ItemAdapter()
        fastAdapter = FastAdapter.with(favoriteItemAdapter!!)

        favoritesRecycler.adapter = fastAdapter
        favoritesRecycler.layoutManager = getGridLayoutManager(fastAdapter!!)
        addClickListeners(fastAdapter!!)
    }

    /**
     * Setup any click listeners for the [FastAdapter]
     *
     * @param fastAdapter the item adapter to setup the click listeners for.
     */
    private fun addClickListeners(fastAdapter: FastAdapter<ThumbnailItemView>){
        fastAdapter.onClickListener = {_, _, item, _ ->
            val thumbnailData = (item as ThumbnailItemView).thumbnailData
            thumbnailData?.apply {
                val toReaderAction = FavoriteFragmentDirections.actionFavoriteFragmentToComicPagerActivity(
                    comicUrl,
                    comicId
                )
                findNavController().navigate(toReaderAction)
            }
            true
        }
    }


    /**
     * Initialize any Observers for the live data in the [FavoriteViewModel]
     */
    private fun initObservers(viewModel: FavoriteViewModel) {
        viewModel.favoriteList.observe(viewLifecycleOwner, Observer { favoriteList ->
            if(favoriteList.isEmpty()){
                binding.emptyFavoritesContainer.visibility = View.VISIBLE
                binding.favoritesContainer.visibility = View.GONE
            } else {
                binding.emptyFavoritesContainer.visibility = View.GONE
                binding.favoritesContainer.visibility = View.VISIBLE
                favoriteItemAdapter?.add(favoriteList.toThumbnailItemView())
            }
        })
    }
}