package com.htphtp.tools.view

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Build
import android.widget.ProgressBar

fun ProgressBar.setProgressBarColor(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        this.indeterminateTintList = ColorStateList.valueOf(color)
    } else {
        val drawable = this.indeterminateDrawable.mutate()
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        this.indeterminateDrawable = drawable
    }
}