package com.htphtp.tools

import android.content.Context
import android.content.res.Resources
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.fragment.app.Fragment

/**
 * Created by htp on 2018/4/10.
 */


val screenWidth: Int = Resources.getSystem().displayMetrics.widthPixels
val screenHeight: Int = Resources.getSystem().displayMetrics.heightPixels

val Number.dp: Int get() = (toInt() * Resources.getSystem().displayMetrics.density).toInt()


fun Fragment.getDimensionPixelSize(@DimenRes id: Int): Int = resources.getDimensionPixelSize(id)

fun Context.getDimensionPixelSize(@DimenRes id: Int): Int = resources.getDimensionPixelSize(id)

fun Fragment.getColorByRes(@ColorRes colorResId: Int): Int = context!!.getColorByRes(colorResId)

fun Context.getColorByRes(@ColorRes colorResId: Int): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    this.resources.getColor(colorResId, null)
} else {
    this.resources.getColor(colorResId)
}


fun Fragment.px2dp(pxValue: Float): Int = context!!.px2dp(pxValue)

fun Context.px2dp(pxValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

fun Fragment.dp2px(dipValue: Float): Int = context!!.dp2px(dipValue)

fun Context.dp2px(dipValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (dipValue * scale + 0.5f).toInt()
}
