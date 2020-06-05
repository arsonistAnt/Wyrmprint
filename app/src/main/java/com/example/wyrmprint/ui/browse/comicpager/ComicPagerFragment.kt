package com.example.wyrmprint.ui.browse.comicpager

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import coil.api.load
import coil.decode.DataSource
import coil.request.Request
import com.example.wyrmprint.databinding.FragComicStripReaderBinding
import com.example.wyrmprint.injection.injector
import com.example.wyrmprint.injection.viewModel
import com.example.wyrmprint.ui.base.MainReaderActivity
import com.example.wyrmprint.ui.base.MainReaderActivityArgs
import com.example.wyrmprint.ui.viewmodels.ComicPagerViewModel
import com.github.chrisbanes.photoview.PhotoView
import me.zhanghai.android.systemuihelper.SystemUiHelper

class ComicPagerFragment : Fragment() {
    lateinit var binding: FragComicStripReaderBinding
    private var safeArgs: MainReaderActivityArgs? = null
    private val viewModel: ComicPagerViewModel by viewModel { injector.comicPagerViewModel }
    private var systemUiHelper: SystemUiHelper? = null

    // Comic strip image size
    private val imgWidth = 650
    private val imgHeight = 3000

    @Suppress("DEPRECATION")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        safeArgs = (activity as MainReaderActivity).safeArgs
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragComicStripReaderBinding.inflate(inflater, container, false)
        binding.comicStrip.autoScaleWidth = false
        initObservers(viewModel)
        createSystemUiHelper()
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.showSystemUi(false)
        binding.comicStrip.setOnPhotoTapListener { _, _, _ ->
            toggleSystemUi()
        }
        viewModel.onOrientationChange(resources.configuration.orientation)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.onOrientationChange(newConfig.orientation)
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
                systemUiHelper?.show()
            } else {
                systemUiHelper?.hide()
            }
        })

        viewModel.comicDetailsState.observe(viewLifecycleOwner, Observer { comicNetworkState ->
            when {
                comicNetworkState.success -> {
                    comicNetworkState.data?.let {
                        configureImageViewScale(it.comicUrl, binding.comicStrip)
                    }
                }
                comicNetworkState.inProgress -> {
                    binding.comicStripLoader.visibility = View.VISIBLE
                }
                else -> {
                    Toast.makeText(requireContext(), "An error has occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
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
            } else
                viewModel.showSystemUi(true)
        }
    }

    /**
     * Initialize the [SystemUiHelper] util object.
     */
    private fun createSystemUiHelper() {
        val level = SystemUiHelper.LEVEL_IMMERSIVE
        val flags = SystemUiHelper.FLAG_IMMERSIVE_STICKY or
                SystemUiHelper.FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES
        systemUiHelper = SystemUiHelper(requireActivity(), level, flags)
    }
}