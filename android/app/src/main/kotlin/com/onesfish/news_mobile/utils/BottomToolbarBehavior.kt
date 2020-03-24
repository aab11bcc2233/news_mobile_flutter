package com.onesfish.news_mobile.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.google.android.material.card.MaterialCardView
import com.htphtp.tools.getDimensionPixelSize
import com.onesfish.news_mobile.R


/**
 * Create by htp on 2019/9/11
 */
class BottomToolbarBehavior : VerticalScrollingBehavior<MaterialCardView> {
    private val INTERPOLATOR = LinearOutSlowInInterpolator()
    private var hidden = false
    //    private var animateToolbar: ViewPropertyAnimatorCompat? = null
//    private var animateFab: ViewPropertyAnimatorCompat? = null
    private var fabParent: FrameLayout? = null

    private var offsetHideToolbar = -1f
    private var offsetHideFab = -1f

    private var animatorShow: AnimatorSet? = null
    private var animatorHide: AnimatorSet? = null


    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor() : super()


    override fun onNestedVerticalOverScroll(coordinatorLayout: CoordinatorLayout, child: MaterialCardView, direction: Int, currentOverScroll: Int, totalOverScroll: Int) {

    }

    override fun onDirectionNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: MaterialCardView, target: View, dx: Int, dy: Int, consumed: IntArray, scrollDirection: Int) {
        handleDirection(child, scrollDirection)
    }

    override fun onNestedDirectionFling(coordinatorLayout: CoordinatorLayout, child: MaterialCardView, target: View, velocityX: Float, velocityY: Float, scrollDirection: Int): Boolean {
        handleDirection(child, scrollDirection)
        return true
    }

    private fun handleDirection(child: MaterialCardView, scrollDirection: Int) {
        if (offsetHideToolbar == -1f) {
            val context = child.context
            offsetHideToolbar = (context.getDimensionPixelSize(R.dimen.bottom_toolbar_height) + context.getDimensionPixelSize(R.dimen.bottom_toolbar_margin)).toFloat()
            offsetHideFab = (context.getDimensionPixelSize(R.dimen.news_details_fab_bottom) + context.getDimensionPixelSize(R.dimen.fab_size)).toFloat()

        }

        if (fabParent == null) {
            fabParent = (child.parent as? View)?.findViewById<FrameLayout>(R.id.fabParent)
        }

        if (scrollDirection == VerticalScrollingBehavior.ScrollDirection.SCROLL_DIRECTION_DOWN && hidden) {
            hidden = false
//            animateOffset(child, 0f)
            runAnimatorShow(child)
        } else if (scrollDirection == VerticalScrollingBehavior.ScrollDirection.SCROLL_DIRECTION_UP && !hidden) {
            hidden = true
//            animateOffset(child, offsetHideToolbar)
            runAnimatorHide(child)
        }
    }

    private fun runAnimatorShow(child: MaterialCardView) {
        if (animatorShow == null) {
            animatorShow = AnimatorSet()
            animatorShow!!.play(
                    ObjectAnimator.ofFloat(fabParent!!, "translationY", 0f)
            )
                    .with(ObjectAnimator.ofFloat(child!!, "translationY", 0f))
            animatorShow!!.interpolator = INTERPOLATOR

        }

        animatorHide?.cancel()
        animatorShow?.cancel()

        animatorShow?.start()
    }

    private fun runAnimatorHide(child: MaterialCardView) {
        if (animatorHide == null) {
            animatorHide = AnimatorSet()
            animatorHide!!.play(ObjectAnimator.ofFloat(child!!, "translationY", offsetHideToolbar))
                    .with(ObjectAnimator.ofFloat(fabParent!!, "translationY", offsetHideFab))
            animatorHide!!.interpolator = INTERPOLATOR

        }

        animatorShow?.cancel()
        animatorHide?.cancel()

        animatorHide?.start()
    }

//    private fun animateOffset(child: MaterialCardView, offset: Float) {
//        if (animateToolbar == null) {
//            animateToolbar = ViewCompat.animate(child)
//            animateFab = ViewCompat.animate(fabParent!!)
//        } else {
//            animateToolbar!!.cancel()
//            animateFab!!.cancel()
//        }
//
//
//        if (offset > 0f) { // hide
//            animateToolbar!!.translationY(offset)
//                    .setInterpolator(INTERPOLATOR)
//                    .setListener(object : ViewPropertyAnimatorListenerAdapter() {
//                        override fun onAnimationEnd(view: View?) {
//                            super.onAnimationEnd(view)
//                            animateFab!!.translationY(offsetHideFab)
//                                    .setInterpolator(INTERPOLATOR)
//                                    .start()
//
//                        }
//                    })
//                    .start()
//        } else {
//            animateFab!!.translationY(0f)
//                    .setInterpolator(INTERPOLATOR)
//                    .setListener(object : ViewPropertyAnimatorListenerAdapter() {
//                        override fun onAnimationEnd(view: View?) {
//                            super.onAnimationEnd(view)
//                            animateToolbar!!.translationY(0f)
//                                    .setInterpolator(INTERPOLATOR)
//                                    .start()
//                        }
//                    })
//                    .start()
//        }
//
//    }
}
