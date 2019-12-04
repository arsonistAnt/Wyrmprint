package com.example.wyrmprint.util

import android.graphics.Bitmap
import android.graphics.Matrix
import com.github.chrisbanes.photoview.PhotoView


/**
 * A util class to help configure the minimum, medium, and maximum scale factors
 * for a given comic strip bitmap & [PhotoView] that hosts the image.
 *
 * Minimum -> Calculated to have a scale factor take up 80% of the width of the [PhotoView]
 * Medium -> Calculated to have a scale factor take up the whole width of the [PhotoView]
 * Maximum -> Will simply be 1.5F * Medium scale factor.
 */
class ComicImageScaleUtil(
    view: PhotoView,
    loadedImage: Bitmap
) {
    var minScaleFactor: Float = 0F
    var mediumScaleFactor: Float = 0F
    var maxScaleFactor: Float = 0F

    init {
        calculateAllZoomFactors(view, loadedImage)
    }

    private fun calculateAllZoomFactors(view: PhotoView, loadedImage: Bitmap) {
        val comicImageMatrix = view.imageMatrix
        val imageMatrixValues = FloatArray(9)
        comicImageMatrix.getValues(imageMatrixValues)

        // Calculate appropriate scale factor based on PhotoView width.
        val currentImageWidth = imageMatrixValues[Matrix.MSCALE_X] * loadedImage.width
        minScaleFactor = (view.width * .8F) / currentImageWidth
        mediumScaleFactor = view.width / currentImageWidth
        maxScaleFactor = mediumScaleFactor * 1.5F
    }
}


fun PhotoView.setScaleConfig(scaleConfig: ComicImageScaleUtil) {
    this.setScaleLevels(
        scaleConfig.minScaleFactor,
        scaleConfig.mediumScaleFactor,
        scaleConfig.maxScaleFactor
    )
}