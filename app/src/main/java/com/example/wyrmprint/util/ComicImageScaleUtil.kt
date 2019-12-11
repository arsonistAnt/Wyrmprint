package com.example.wyrmprint.util


import android.graphics.Matrix
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.github.chrisbanes.photoview.PhotoView


/**
 * A util class to help configure the minimum, medium, and maximum scale factors
 * for a given comic strip bitmap & [PhotoView] that hosts the image.
 *
 * Minimum -> Calculated to have a scale factor take up 80% of the width of the [PhotoView]
 * Medium -> Calculated to have a scale factor take up the whole width of the [PhotoView]
 * Maximum -> Will simply be 1.5F * Medium scale factor.
 */
class ComicImageScaleUtil {
    companion object {
        fun calculateAllZoomFactors(view: PhotoView, loadedImage: Drawable): ZoomFactors {
            val imageBitmap = loadedImage.toBitmap()
            val imageMatrix = view.imageMatrix
            val matrixScaleVal = FloatArray(9)

            imageMatrix.getValues(matrixScaleVal)
            // The total width size of the image within this PhotoView.
            val imageScaledWidth = (matrixScaleVal[Matrix.MSCALE_X] * imageBitmap.width)

            // Calculate appropriate scale factor based on PhotoView width.
            val minScaleFactor = (view.width * .8F) / imageScaledWidth
            val mediumScaleFactor = view.width.toFloat() / imageScaledWidth
            val maxScaleFactor = mediumScaleFactor * 1.5F

            return ZoomFactors(minScaleFactor, mediumScaleFactor, maxScaleFactor)
        }

        /**
         * Set the [Matrix.MSCALE_X] to the [ZoomFactors.minimum] value.
         */
        fun setSupportMatrixScaleX(view: PhotoView, newZoomFactor: ZoomFactors) {
            // Get the support matrix.
            val tempMatrix = Matrix()
            view.getSuppMatrix(tempMatrix)

            // Get the scale values.
            val tempValues = FloatArray(9)
            tempMatrix.getValues(tempValues)
            val yScale = tempValues[Matrix.MSCALE_Y]
            tempMatrix.postScale(newZoomFactor.minimum, yScale)
        }
    }
}

/**
 * Data class to hold the minimum, medium, and maximum scale factors for the [PhotoView]
 */
data class ZoomFactors(val minimum: Float, val medium: Float, val maximum: Float)

/**
 * Set the minimum, medium, and maximum zoom scales for the
 */
fun PhotoView.setScaleConfig(scaleConfig: ZoomFactors) {
    this.setScaleLevels(
        scaleConfig.minimum,
        scaleConfig.medium,
        scaleConfig.maximum
    )
}