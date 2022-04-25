package com.app.synchealth.utils

import android.content.res.Resources
import android.util.TypedValue
import kotlin.math.roundToInt


/**
 * Helper class to perform unit conversions.
 */
object DynamicUnitUtils {
    /**
     * Converts DP into pixels.
     *
     * @param dp The value in DP to be converted into pixels.
     *
     * @return The converted value in pixels.
     */
    fun convertDpToPixels(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp, Resources.getSystem().displayMetrics
        ).roundToInt()
    }

    /**
     * Converts pixels into DP.
     *
     * @param pixels The value in pixels to be converted into DP.
     *
     * @return The converted value in DP.
     */
    fun convertPixelsToDp(pixels: Int): Int {
        return (pixels / Resources.getSystem().displayMetrics.density).roundToInt()
    }

    /**
     * Converts SP into pixels.
     *
     * @param sp The value in SP to be converted into pixels.
     *
     * @return The converted value in pixels.
     */
    private fun convertSpToPixels(sp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp, Resources.getSystem().displayMetrics
        ).roundToInt()
    }

    /**
     * Converts pixels into SP.
     *
     * @param pixels The value in pixels to be converted into SP.
     *
     * @return The converted value in SP.
     */
    fun convertPixelsToSp(pixels: Int): Int {
        return (pixels / Resources.getSystem().displayMetrics.density).roundToInt()
    }

    /**
     * Converts DP into SP.
     *
     * @param dp The value in DP to be converted into SP.
     *
     * @return The converted value in SP.
     */
    fun convertDpToSp(dp: Float): Int {
        return (convertDpToPixels(dp) / convertSpToPixels(
            dp
        ).toFloat()).roundToInt()
    }
}