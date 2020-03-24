package com.htphtp.tools.view

import android.view.View

abstract class OnMultiClickListener : View.OnClickListener {
    private val MIN_CLICK_DELAY_TIME: Long = 1000L
    private var lastClickTime: Long = 0L

    override fun onClick(v: View) {
        val curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            lastClickTime = curClickTime
            onMultiClick(v)
        }
    }

    abstract fun onMultiClick(v: View)
}