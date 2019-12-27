package com.example.wyrmprint.ui.browse.comicpager

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.DisplayCutout
import android.view.View
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
import androidx.core.graphics.drawable.toBitmap
import com.example.wyrmprint.util.ComicImageScaleUtil
import com.example.wyrmprint.util.ZoomFactors
import com.example.wyrmprint.util.setScaleConfig
import com.github.chrisbanes.photoview.PhotoView

/**
 * A custom [PhotoView] class to keep the zoom factor & zoom state stable during
 * layout changes (e.g. system ui visibility, display cutouts). Also automatically
 * resize width of the image to fit 80% of the view that is hosting it.
 */
class ComicStripView : PhotoView, View.OnLayoutChangeListener {
    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet) : super(context, attr)
    constructor(context: Context, attr: AttributeSet, intDefStyle: Int) : super(
        context,
        attr,
        intDefStyle
    )

    // Auto scale the drawable by default when it's set.
    var autoScaleWidth = true

    // Determines whether the device has a notch.
    var displayObject: DisplayCutout? = null

    private var sysUiChanged = false
    // The current support matrix for this view, used to restore zoom scale & zoom position x y.
    private var currSuppMatrix = Matrix()
    // The current max, min, and med zoom scales for this view.
    private var currZoomFactors: ZoomFactors? = null


    init {
        // Remove the attacher as its already listening by default implementation of the super class.
        removeOnLayoutChangeListener(attacher)
        addOnLayoutChangeListener(this)
    }

    override fun onLayoutChange(
        p0: View?,
        p1: Int,
        p2: Int,
        p3: Int,
        p4: Int,
        p5: Int,
        p6: Int,
        p7: Int,
        p8: Int
    ) {
        if (!sysUiChanged) {
            attacher.onLayoutChange(p0, p1, p2, p3, p4, p5, p6, p7, p8)
        }
        if (sysUiChanged) {
            sysUiChanged = false
            fitImageDrawableWidth(drawable)
            setSuppMatrix(currSuppMatrix)
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (autoScaleWidth)
            fitImageDrawableWidth(drawable)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Get the DisplayCutout object to determine if a device has a notch.
        displayObject = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            (context as Activity).window.decorView.rootWindowInsets.displayCutout
        } else {
            null
        }
    }

    override fun onWindowSystemUiVisibilityChanged(visible: Int) {
        super.onWindowSystemUiVisibilityChanged(visible)
        // Handle sys ui visibility changes on devices that have notches.
        if (isPieVersion() && !isLandscape()) {
            val displayCutoutMode =
                (context as Activity).window.attributes.layoutInDisplayCutoutMode
            displayObject?.let {
                if (displayCutoutMode == LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT) {
                    sysUiChanged = true
                    getSuppMatrix(currSuppMatrix)
                }
            }
        }
    }

    /**
     * Public call to auto size width the image within [ComicStripView].
     */
    fun autoSizeImage() {
        fitImageDrawableWidth(drawable)
    }

    /**
     * Scale the image drawable width the size of the view width.
     *
     * @param drawable the drawable in [ComicStripView]
     */
    private fun fitImageDrawableWidth(drawable: Drawable?) {
        drawable?.let {
            if ((drawable.toBitmap().width > 0)
                && this@ComicStripView.width > 0
            ) {
                currZoomFactors =
                    ComicImageScaleUtil.calculateAllZoomFactors(this@ComicStripView, drawable)
                setScaleConfig(currZoomFactors!!)
                scale = currZoomFactors!!.minimum
            }
        }
    }

    private fun isPieVersion(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    private fun isLandscape(): Boolean =
        resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}