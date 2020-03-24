package com.htphtp.tools.view

import android.graphics.Bitmap
import android.view.View
import android.widget.TextView

/**
 * Created by htp on 2018/4/10.
 */

fun View.measureBy_WRAP_CONTENT(): IntArray {
    val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((1 shl 30) - 1, View.MeasureSpec.AT_MOST)
    val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((1 shl 30) - 1, View.MeasureSpec.AT_MOST)
    this.measure(widthMeasureSpec, heightMeasureSpec)
    return intArrayOf(this.measuredWidth, this.measuredHeight)
}


fun makeMeasureSpecByExactly(size: Int): Int = View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY)

fun View.toBitmapByMeasureBefore(widthSize: Int, heightSize: Int): Bitmap {
    isDrawingCacheEnabled = true

    val widthMeasureSpec = makeMeasureSpecByExactly(widthSize)
    val heightMeasureSpec = makeMeasureSpecByExactly(heightSize)

    measure(widthMeasureSpec, heightMeasureSpec)
    layout(0, 0, measuredWidth, measuredHeight)

    buildDrawingCache(true)
    val b = Bitmap.createBitmap(drawingCache)
    isDrawingCacheEnabled = false // clear drawing cache
    return b
}

var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

var View.isInVisible: Boolean
    get() = visibility == View.INVISIBLE
    set(value) {
        visibility = if (value) View.INVISIBLE else View.VISIBLE
    }

fun View.gone() {
    this.visibility = View.GONE
}

fun View.inVisible() {
    this.visibility = View.INVISIBLE
}

fun View.show() {
    this.visibility = View.VISIBLE
}


fun View.show(content: String = "") {
    this.visibility = View.VISIBLE
    if (this is TextView) {
        this.text = content
    }
}

fun View.show(id: Int = -1) {
    this.visibility = View.VISIBLE
    if (this is TextView && id != -1) {
        this.setText(id)
    }
}

// 先不做防抖动
fun View.click(call: (view: View) -> Unit) {
    this.setOnClickListener(object : OnMultiClickListener() {
        override fun onMultiClick(v: View) {
            call(v)
        }

    })
}

val TextView.content: String
    get() {
        return this.text.toString().trim()
    }