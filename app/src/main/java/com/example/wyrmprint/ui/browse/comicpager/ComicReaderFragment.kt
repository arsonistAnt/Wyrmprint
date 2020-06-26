package com.example.wyrmprint.ui.browse.comicpager

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import coil.api.load
import coil.decode.DataSource
import coil.request.Request
import com.example.wyrmprint.R
import com.example.wyrmprint.data.local.ComicStrip
import com.example.wyrmprint.data.model.NetworkState
import com.example.wyrmprint.databinding.FragComicStripReaderBinding
import com.example.wyrmprint.injection.injector
import com.example.wyrmprint.injection.viewModel
import com.example.wyrmprint.ui.base.MainReaderActivity
import com.example.wyrmprint.ui.base.MainReaderActivityArgs
import com.example.wyrmprint.ui.base.UIVisibilityAction
import com.example.wyrmprint.ui.viewmodels.ComicPagerViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import com.mikepenz.iconics.utils.sizeDp
import kotlinx.android.synthetic.main.reader_bottom_sheet_layout.view.*

class ComicReaderFragment : Fragment() {
    lateinit var binding: FragComicStripReaderBinding
    private var safeArgs: MainReaderActivityArgs? = null
    private val viewModel: ComicPagerViewModel by viewModel { injector.comicPagerViewModel }
    private var retryButton: Button? = null

    private var bottomSheetLayout: View? = null
    private var bottomBehavior: BottomSheetBehavior<View>? = null
    private var currComicStrip: ComicStrip? = null

    // Comic strip image size
    private val imgWidth = 650
    private val imgHeight = 3000

    @Suppress("DEPRECATION")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        safeArgs = (activity as MainReaderActivity).safeArgs
        //addComicButtonListeners()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragComicStripReaderBinding.inflate(inflater, container, false)
        binding.comicStrip.autoScaleWidth = false
        initObservers(viewModel)
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        constructRetryButton()
        // Setup prev and next comic button listeners.
        constructBottomSheet()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.showSystemUi(false)
        binding.comicStripLayout.setOnClickListener { toggleSystemUi() }
        binding.comicStrip.setOnPhotoTapListener { _, _, _ -> toggleSystemUi() }
        viewModel.onOrientationChange(resources.configuration.orientation)

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.onOrientationChange(newConfig.orientation)
    }

    /**
     * Create the [retryButton] Button.
     */
    private fun constructRetryButton() {
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER
        retryButton = Button(requireContext()).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            text = context.getString(R.string.retry_loading_msg)
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            setLayoutParams(layoutParams)
            setOnClickListener {
                val comicId = safeArgs?.comicId ?: -1
                viewModel.requestComicDetails(comicId)
                binding.comicStripLayout.removeView(this)
            }
        }
    }

    /**
     * Add the previous and next buttons listener on the bottom sheet header in the [MainReaderActivity].
     */
    private fun addComicButtonListeners(bottomSheetLayout: View) {
        // Setup Comic navigation buttons.
        val prevBtn = bottomSheetLayout.prev_comic_btn
        val nextBtn = bottomSheetLayout.next_comic_btn
        prevBtn.setOnClickListener {
            viewModel.requestComicDetails(viewModel.prevComicId)
        }
        nextBtn.setOnClickListener {
            viewModel.requestComicDetails(viewModel.nextComicId)
        }

        val favoritesItem = bottomSheetLayout.favorites_menu_item
        favoritesItem.setOnClickListener {
            currComicStrip?.let {
                viewModel.toggleFavorites(it)
            }
        }

        val shareItem = bottomSheetLayout.share_menu_item
        shareItem.setOnClickListener {
            val shareComicUrlIntent = Intent()
            shareComicUrlIntent.apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "${currComicStrip?.comicUrl}")
            }
            startActivity(Intent.createChooser(shareComicUrlIntent, "Choose"))
        }

        val downloadItem = bottomSheetLayout.download_menu_item
        downloadItem.setOnClickListener {
            // TODO: Different methods of  downloading https://stackoverflow.com/questions/15549421/how-to-download-and-save-an-image-in-android
            // TODO: Basic method of download manager, let android handle it! https://developer.android.com/reference/android/app/DownloadManager.html
            checkForDownloadPermissions {
                // TODO 2. Directory Chooser
                // TODO 3. Initiate Download of comic
            }
        }

        // Set click listener for expand button to expand and collapse reader sheet.
        val dragUpBtn = bottomSheetLayout.drag_up_btn
        dragUpBtn.setOnClickListener {
            bottomBehavior?.apply {
                state = when (state) {
                    BottomSheetBehavior.STATE_COLLAPSED -> BottomSheetBehavior.STATE_EXPANDED
                    BottomSheetBehavior.STATE_EXPANDED -> BottomSheetBehavior.STATE_COLLAPSED
                    else -> BottomSheetBehavior.STATE_SETTLING
                }
            }
        }
    }

    /**
     * Construct bottom sheet for the [MainReaderActivity]
     */
    private fun constructBottomSheet() {
        bottomSheetLayout = binding.root.rootView.bottom_sheet_test
        bottomBehavior = BottomSheetBehavior.from(bottomSheetLayout!!)
        // Get header of the bottom sheet and calculate its height.
        val header = bottomSheetLayout!!.findViewById<View>(R.id.header_container_bottom_sheet)
        // Measure the height of the header to give to the peekHeight.
        header.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        binding.comicStripLayout.setOnSystemUiVisibilityChangeListener { sysFlags ->
            when (sysFlags) {
                MainReaderActivity.SystemUIState.UIVisible.ordinal -> {
                    bottomBehavior?.isHideable = false
                    bottomBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                else -> {
                    bottomBehavior?.isHideable = true
                    bottomBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(bottomSheetLayout!!) { v, insets ->
            adjustBottomSheetMargin(v, insets)
            bottomBehavior?.peekHeight = header.measuredHeight + insets.systemWindowInsetBottom
            v.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
        bottomBehavior?.apply {
            isFitToContents = true
            isGestureInsetBottomIgnored = true
            peekHeight = header.measuredHeight
            skipCollapsed = false
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        addComicButtonListeners(bottomSheetLayout!!)
    }

    /**
     * Observe the live data values in the [ComicPagerViewModel]
     *
     * @param viewModel the [ComicPagerViewModel] object to set observers for.
     */
    private fun initObservers(viewModel: ComicPagerViewModel) {
        viewModel.orientation.observe(viewLifecycleOwner, Observer {
            safeArgs?.run { viewModel.requestComicDetails(comicId) }
        })
        viewModel.systemUiVisible.observe(viewLifecycleOwner, Observer { show ->
            if (show) {
                (requireActivity() as UIVisibilityAction).show()

            } else {
                (requireActivity() as UIVisibilityAction).hide()
            }
        })
        viewModel.isSavedToFavorites.observe(viewLifecycleOwner, Observer {
            toggleFavorites(it)
        })
        viewModel.comicDetailsState.observe(
            viewLifecycleOwner,
            Observer {
                handleNetworkStateUI(it)
            })
    }

    /**
     * Toggles the [bottomSheetLayout]'s favorite icon and message if the comic strip has been saved to the favorites.
     *
     * @param isFavorited a boolean specifying whether or not to show the outlined heart or solid filled heart.
     */
    private fun toggleFavorites(isFavorited: Boolean) {
        val favoritesText = bottomSheetLayout?.favorites_desc_text
        val favoriteImageBtn = bottomSheetLayout?.favoritesBtn
        if (isFavorited) {
            val removeFavoriteText = getString(R.string.remove_from_fav_desc)
            favoritesText?.text = removeFavoriteText
            val heartDrawable =
                IconicsDrawable(requireContext(), CommunityMaterial.Icon2.cmd_heart).apply {
                    sizeDp = 24
                }
            favoriteImageBtn?.setImageDrawable(heartDrawable)
        } else {
            val addFavoritesText = getString(R.string.save_to_fav_desc)
            val heartOutlineDrawable =
                IconicsDrawable(requireContext(), CommunityMaterial.Icon2.cmd_heart_outline)
                    .apply {
                        sizeDp = 24
                    }
            favoritesText?.text = addFavoritesText
            favoriteImageBtn?.setImageDrawable(heartOutlineDrawable)
        }
    }

    /**
     * Handle the requesting network state for the [ComicStrip] and show user appropriate UI.
     *
     * @param comicNetworkState [NetworkState] object to handle.
     */
    private fun handleNetworkStateUI(comicNetworkState: NetworkState<ComicStrip>) {
        when {
            comicNetworkState.success -> {
                comicNetworkState.data?.let {
                    configureImageViewScale(it.comicUrl, binding.comicStrip)
                    updateComicInfo(it)
                    viewModel.prevComicId = it.prevStrip.id
                    viewModel.nextComicId = it.nextStrip.id
                }
            }
            comicNetworkState.inProgress -> binding.comicStripLoader.visibility =
                View.VISIBLE
            else -> handleNetworkError(comicNetworkState)
        }
    }

    /**
     * Handle any network errors and show UI to user.
     *
     * @param comicNetworkState [NetworkState] object.
     */
    private fun handleNetworkError(comicNetworkState: NetworkState<ComicStrip>) {
        val errMsg =
            comicNetworkState.err?.message ?: "An unexpected error has occurred."
        Toast.makeText(requireContext(), errMsg, Toast.LENGTH_SHORT)
            .show()
        binding.comicStripLoader.visibility = View.GONE
        binding.comicStripLayout.removeView(retryButton)
        binding.comicStripLayout.addView(retryButton)
    }

    /**
     * Configure the comic strip bitmap scale in the [PhotoView] based on current dimensions of the view.
     */
    private fun configureImageViewScale(
        comicUrl: String,
        comicStripView: PhotoView
    ) {
        safeArgs.apply {
            comicStripView.load(comicUrl) {
                size(imgWidth, imgHeight)
                listener(object : Request.Listener {
                    override fun onSuccess(data: Any, source: DataSource) {
                        binding.comicStrip.post {
                            binding.comicStrip.autoSizeImage()
                            binding.comicStripLoader.visibility = View.GONE
                            binding.comicStrip.visibility = View.VISIBLE
                        }
                    }
                })
            }
        }
    }

    /**
     * Toggle between showing the system UI.
     */
    private fun toggleSystemUi() {
        viewModel.systemUiVisible.value?.let { show ->
            if (show) {
                viewModel.showSystemUi(false)

            } else {
                viewModel.showSystemUi(true)
            }
        }
    }

    /**
     * Update the comic title and comic number on the bottom sheet bar and the app bar title.
     */
    private fun updateComicInfo(comicInfo: ComicStrip) {
        requireActivity().findViewById<TextView>(R.id.comic_title_text)
            .text = comicInfo.title
        (requireActivity() as AppCompatActivity).supportActionBar
            ?.title = "Dragalia Life | #${comicInfo.episodeNumber}"
        currComicStrip = comicInfo
    }

    /**
     * Adjust bottom sheet margin on configuration change with
     * the systems current insets.
     *
     * @param bottomSheet the [bottomSheetLayout].
     * @param insets the [WindowInsetsCompat] to adjust the layout params to.
     */
    private fun adjustBottomSheetMargin(bottomSheet: View, insets: WindowInsetsCompat) {
        val marginParams = bottomSheet.layoutParams as ViewGroup.MarginLayoutParams
        marginParams.leftMargin = insets.systemWindowInsetLeft
    }


    /**
     *  Check for download permissions before performing the action.
     *
     *  @param action the action to perform after permissions have been checked.
     */
    private fun checkForDownloadPermissions(action: () -> Unit) = runWithPermissions(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.INTERNET
    ) { action() }
}