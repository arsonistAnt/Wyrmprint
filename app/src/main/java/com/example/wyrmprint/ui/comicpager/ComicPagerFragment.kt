package com.example.wyrmprint.ui.comicpager

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import coil.api.load
import coil.request.CachePolicy
import com.example.wyrmprint.databinding.FragComicPagerLayoutBinding
import com.example.wyrmprint.injection.injector
import com.example.wyrmprint.injection.viewModel
import com.example.wyrmprint.ui.base.MainActivity
import com.example.wyrmprint.ui.viewmodel.ComicPagerViewModel
import com.example.wyrmprint.util.ComicImageScaleUtil
import com.example.wyrmprint.util.setScaleConfig

class ComicPagerFragment : Fragment() {
    private val safeArgs: ComicPagerFragmentArgs by navArgs()
    lateinit var binding: FragComicPagerLayoutBinding
    private val viewModel: ComicPagerViewModel by viewModel { injector.comicPagerViewModel }

    // Comic strip image size
    private val imgWidth = 650
    private val imgHeight = 3000

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragComicPagerLayoutBinding.inflate(inflater, container, false)
        (requireActivity() as MainActivity).mainBinding.mainNavbarBottom.visibility = View.GONE
        initObservers(viewModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onOrientationChange(resources.configuration.orientation)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.onOrientationChange(newConfig.orientation)
    }

    private fun initObservers(viewModel: ComicPagerViewModel) {
        viewModel.orientation.observe(viewLifecycleOwner, Observer {
            val comicStripView = binding.comicStrip
            comicStripView.load(safeArgs.comicUrl) {
                diskCachePolicy(CachePolicy.DISABLED)
                memoryCachePolicy(CachePolicy.DISABLED)
                size(imgWidth, imgHeight)
                listener { _, _ ->
                    val scaleUtil =
                        ComicImageScaleUtil(comicStripView, comicStripView.drawable.toBitmap())
                    comicStripView.setScaleConfig(scaleUtil)
                    comicStripView.setScale(scaleUtil.minScaleFactor, 0F, 0F, false)
                }
            }
        })
    }
}