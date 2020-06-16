package com.example.wyrmprint.ui.favorite

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wyrmprint.R
import com.example.wyrmprint.data.model.FavoriteUtil.Companion.createEmptyThumbnailFavorite
import com.example.wyrmprint.data.model.ThumbnailData
import com.example.wyrmprint.data.model.toFavoriteThumbnail
import com.example.wyrmprint.data.model.toThumbnailItemView
import com.example.wyrmprint.databinding.FragFavoriteLayoutBinding
import com.example.wyrmprint.injection.injector
import com.example.wyrmprint.injection.viewModel
import com.example.wyrmprint.ui.browse.viewholder.RetryItemView
import com.example.wyrmprint.ui.browse.viewholder.ThumbnailItemView
import com.example.wyrmprint.ui.viewmodels.FavoriteViewModel
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.ISelectionListener
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.helpers.ActionModeHelper
import com.mikepenz.fastadapter.helpers.UndoHelper
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.fastadapter.ui.items.ProgressItem
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.*

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragFavoriteLayoutBinding
    private val viewModel: FavoriteViewModel by viewModel {
        injector.favoriteViewModel
    }

    // Default span count for different orientations.
    private val SPAN_COUNT_PORTRAIT = 2
    private val SPAN_COUNT_LANDSCAPE = 4

    // Favorite Recycler View components
    private var favoriteItemAdapter: ItemAdapter<ThumbnailItemView>? = null
    private var fastAdapter: FastAdapter<ThumbnailItemView>? = null
    private lateinit var selectExtension: SelectExtension<ThumbnailItemView>
    private lateinit var actionModeHelper: ActionModeHelper<ThumbnailItemView>
    private lateinit var undoHelper: UndoHelper<*>

    companion object {
        // Diff config for the comic thumbnail paged model adapter.
        val favoriteDiffUtil = AsyncDifferConfig.Builder(object :
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
        binding = FragFavoriteLayoutBinding.inflate(inflater, container, false)
        setupFavoritesBrowser(binding.favoritesRecycler)
        initObservers(viewModel)
        return binding.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Reconfigure layout manager for the recycler view on orientation change.
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
    private fun setupFavoritesBrowser(favoritesRecycler: RecyclerView) {
        favoriteItemAdapter = ItemAdapter()
        fastAdapter = FastAdapter.with(favoriteItemAdapter!!)
        // Initialize selection and action mode helper for multi-selection setups.
        selectExtension = fastAdapter!!.getSelectExtension()
        actionModeHelper =
            ActionModeHelper(fastAdapter!!, R.menu.multi_selection_menu, ActionBarCallBack())
        favoritesRecycler.adapter = fastAdapter
        favoritesRecycler.layoutManager = getGridLayoutManager(fastAdapter!!)
        // Setup click listeners and multi-selection.
        addClickListeners(fastAdapter!!)
        setupMultiSelectionHandler(fastAdapter!!, selectExtension, actionModeHelper)
    }

    /**
     * Setup any click listeners for the [FastAdapter]
     *
     * @param fastAdapter the item adapter to setup the click listeners for.
     */
    private fun addClickListeners(fastAdapter: FastAdapter<ThumbnailItemView>) {
        fastAdapter.onClickListener = { _, _, item, _ ->
            var shouldConsume = false
            // If action mode is active then don't use this click listener.
            if (!actionModeHelper.isActive) {
                val thumbnailData = item.thumbnailData
                thumbnailData?.apply {
                    val toReaderAction =
                        FavoriteFragmentDirections.actionFavoriteFragmentToComicPagerActivity(
                            comicUrl,
                            comicId
                        )
                    findNavController().navigate(toReaderAction)
                }
                shouldConsume = true
            }
            shouldConsume
        }
    }


    /**
     * Initialize any Observers for the live data in the [FavoriteViewModel]
     */
    private fun initObservers(viewModel: FavoriteViewModel) {
        viewModel.favoriteList.observe(viewLifecycleOwner, Observer { favoriteList ->
            if (favoriteList.isEmpty()) {
                // TODO: Fade in animation for this container.
                binding.emptyFavoritesContainer.visibility = View.VISIBLE
                binding.favoritesRecycler.visibility = View.GONE
            } else {
                binding.emptyFavoritesContainer.visibility = View.GONE
                binding.favoritesRecycler.visibility = View.VISIBLE
                favoriteItemAdapter?.set(favoriteList.toThumbnailItemView())
            }
        })
    }

    /**
     * Setup the multi selection for the favorites recycler view. The contextual action bar
     * will be handled by [ActionModeHelper]'s onClick and onLongClick listeners.
     *
     * @param fastAdapter the [ItemAdapter] for the favorite recycler view.
     * @param selectionExtension the [SelectExtension] obtained from the [FastAdapter]
     * @param actionModeHelper the [ActionModeHelper] that will handle the CAB menu.
     */
    private fun setupMultiSelectionHandler(
        fastAdapter: FastAdapter<ThumbnailItemView>,
        selectionExtension: SelectExtension<ThumbnailItemView>,
        actionModeHelper: ActionModeHelper<ThumbnailItemView>
    ) {
        undoHelper = UndoHelper(fastAdapter, object : UndoHelper.UndoListener<ThumbnailItemView> {
            override fun commitRemove(
                positions: Set<Int>,
                removed: ArrayList<FastAdapter.RelativeInfo<ThumbnailItemView>>
            ) {
                Timber.i("Removed: %s", removed.size)
            }
        })
        selectionExtension.apply {
            isSelectable = true
            multiSelect = true
            selectOnLongClick = true
            selectionListener = object : ISelectionListener<ThumbnailItemView> {
                override fun onSelectionChanged(item: ThumbnailItemView, selected: Boolean) {
                    Timber.i("Favoriteselected: %s", selectExtension.selectedItems.size)
                }
            }
        }
        // Configure the adapter to re-route long click and click listeners to the ActionModeHelper
        fastAdapter.apply {
            onPreClickListener =
                { _: View?, _: IAdapter<ThumbnailItemView>, item: ThumbnailItemView, pos: Int ->
                    val res = actionModeHelper.onClick(item)
                    res ?: false
                }
            onPreLongClickListener =
                { _: View?, _: IAdapter<ThumbnailItemView>, item: ThumbnailItemView, pos: Int ->
                    val actionMode =
                        actionModeHelper.onLongClick(requireActivity() as AppCompatActivity, pos)
                    // If action mode exists then consume if not pass down the tree.
                    actionMode != null
                }
        }
    }

    /**
     * CAB call backs for this fragment's multi selection.
     */
    internal inner class ActionBarCallBack : ActionMode.Callback {
        private lateinit var currentBottomNavBehavior: HideBottomViewOnScrollBehavior<View>
        private lateinit var bottomNavParams: CoordinatorLayout.LayoutParams
        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            //as we no longer have a selection so the actionMode can be finished
            //undoHelper.remove(selectExtension.selections)
            val favoriteList = selectExtension.selectedItems.map {
                it.thumbnailData?.toFavoriteThumbnail() ?: createEmptyThumbnailFavorite()
            }.toList()
            viewModel.removeFavorites(favoriteList)
            mode.finish()
            //we consume the event
            return true
        }

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Initialize current bottom navigation scroll behavior.
            bottomNavParams =
                requireActivity().main_navbar_bottom.layoutParams as CoordinatorLayout.LayoutParams
            currentBottomNavBehavior = (bottomNavParams.behavior as HideBottomViewOnScrollBehavior)
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            showActivityBottomNavigation()
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            hideActivityBottomNavigation()
            return false
        }

        /**
         * Show the bottom navigation view and set its behavior to [HideBottomViewOnScrollBehavior] again.
         */
        private fun showActivityBottomNavigation() {
            bottomNavParams.behavior = currentBottomNavBehavior
            currentBottomNavBehavior.slideUp(requireActivity().main_navbar_bottom)
        }

        /**
         * Hide the bottom navigation view from the activity permanently. This is to
         * prevent the bottom navigation from reappearing on scrolling.
         */
        private fun hideActivityBottomNavigation() {
            currentBottomNavBehavior.slideDown(requireActivity().main_navbar_bottom)
            bottomNavParams.behavior = null
        }
    }
}