package com.htphtp.tools

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

/**
 * Created by htp on 2018/8/2.
 */

inline fun Context.getCompoundDrawable(@DrawableRes resId: Int): Drawable {
    var drawable = ContextCompat.getDrawable(this, resId)
    drawable!!.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    return drawable
}