package com.htphtp.tools

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import androidx.annotation.IdRes


/**
 * Created by HeTianpeng on 16/9/7.
 */
object StatusBarUtils {

    fun setStatusBarTransparentAndFullScreen(activity: Activity, isLightBarText: Boolean = false) {
        val window = activity.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setSystemUiVisibility(window)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && isLightBarText) {
                    window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }

                window.statusBarColor = Color.TRANSPARENT
            }
        }
    }

    fun setStatusBarLight(activity: Activity, statusBarColor: Int = Color.TRANSPARENT) {
        val window = activity.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 亮色状态栏模式 或者在style属性中加上 <item name="android:windowLightStatusBar">true</item>
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            setStatusBarColor(activity, statusBarColor)
        }
    }

    fun setStatusBarColor(activity: Activity, statusBarColor: Int = Color.TRANSPARENT) {
        val window = activity.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = statusBarColor
//            if (statusBarColor == -1) {
//                activity.window.decorView.viewTreeObserver
//                        .addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
//                            override fun onPreDraw(): Boolean {
//                                activity.window.decorView.viewTreeObserver.removeOnPreDrawListener(this)
//                                var view = activity.window.decorView.findViewById<FrameLayout>(android.R.id.content)
//                                var contentView = view.getChildAt(0)
//
//                                if (contentView != null && contentView.background != null) {
//                                        if (contentView.background is ColorDrawable) {
//                                            window.statusBarColor = (contentView.background.mutate() as ColorDrawable).color
//                                        } else {
//                                            Thread({
//                                                var bitmap = drawableToBitmap(contentView.background)
//                                                if (bitmap != null) {
//
//                                                    if (activity.isDestroyed) {
//                                                        return@Thread
//                                                    }
//
//                                                    activity.runOnUiThread {
//                                                        Palette.from(bitmap).generate {
//                                                            var color = it.getDominantColor(Color.TRANSPARENT)
//                                                            window.statusBarColor = color
//                                                        }
//                                                    }
//                                                }
//                                            }).start()
//
//                                        }
//                                } else {
//                                   window.statusBarColor = Color.TRANSPARENT
//                                }
//
//                                return true
//                            }
//                        })
//
//            } else {
//                window.statusBarColor = statusBarColor
//            }
        }
    }


    private fun setSystemUiVisibility(window: Window) {
        val decorView = window.decorView
        val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        decorView.systemUiVisibility = option
    }

    fun addStatusBarHeightToPaddingByViewID(activity: Activity, @IdRes viewID: Int) {
        addStatusBarHeightToView(activity, viewID, setPadding(activity))
    }

    fun addStatusBarHeightToPaddingByView(context: Activity, view: View) {
        addStatusBarHeightToView(context, view, setPadding(context))
    }

    private fun setPadding(context: Activity): (View) -> Unit {
        return { view ->
            val params = view.layoutParams

            val statusBarHeight = getStatusBarHeight(
                    context.applicationContext)
            params.height = statusBarHeight + view.height
            val l = view.paddingLeft
            val r = view.paddingRight
            val b = view.paddingBottom
            view.setPadding(l, statusBarHeight, r, b)
        }
    }

    fun addStatusBarHeightToMarginByViewID(activity: Activity, @IdRes viewID: Int) {
        addStatusBarHeightToView(activity, viewID, setMargin(activity))
    }

    fun addStatusBarHeightToMarginByView(context: Activity, view: View) {
        addStatusBarHeightToView(context, view, setMargin(context))
    }

    private fun setMargin(context: Activity): (View) -> Unit {
        return { view ->
            val params: ViewGroup.MarginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams

            val statusBarHeight = getStatusBarHeight(
                    context.applicationContext)

            params.topMargin += statusBarHeight

            view.layoutParams = params
        }
    }

    /**
     * 给View加上状态栏的高度
     */
    private fun addStatusBarHeightToView(context: Context, obj: Any, doSomething: (view: View) -> Unit) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return
        }

        try {
            val view: View?
            if (obj is Int) {
                view = (context as Activity).window.decorView.findViewById(obj)
            } else if (obj is View) {
                view = obj
            } else {
                return
            }

            view?.viewTreeObserver?.addOnPreDrawListener(
                    object : ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            view.viewTreeObserver.removeOnPreDrawListener(this)
                            doSomething(view)
                            return true
                        }
                    })
        } catch (e: Exception) {

        }

    }

    fun getStatusBarHeight(context: Context?): Int {
        var result = 0
        if (context != null) {
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen",
                    "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
        }
        return result
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null

        if (drawable is BitmapDrawable) {
            val bitmapDrawable = drawable as BitmapDrawable
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap()
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888)
        }

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)
        return bitmap
    }

    fun getVirtualBarHeigh(context: Context): Int {
        var vh = 0
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val dm = DisplayMetrics()
        try {
            @SuppressWarnings("rawtypes")
            val c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            val method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
            method.invoke(display, dm)
            vh = dm.heightPixels - windowManager.defaultDisplay.height
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return vh
    }

    fun getVirtualBarHeigh(activity: Activity): Int {
        var titleHeight = 0
        val frame = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(frame)
        val statusHeight = frame.top;
        titleHeight = activity.window.findViewById<View>(Window.ID_ANDROID_CONTENT).top - statusHeight
        return titleHeight
    }

}
