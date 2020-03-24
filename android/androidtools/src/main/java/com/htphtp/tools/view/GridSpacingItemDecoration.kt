package com.newboom.mxassistant.ui.view

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by htp on 2018/7/26.
 */
class GridSpacingItemDecoration
/**
 * @param spanCount gridLayoutManager 列数
 * @param dividerWidth 分割块宽高,单位:dp
 */
    (context: Context, private val spanCount: Int, dividerWidth: Int) : RecyclerView.ItemDecoration() {
    private val dividerWidth: Int
    private val dividerWidthTop: Int
    private val dividerWidthBot: Int

    private val dividerPaint: Paint

    init {

        this.dividerPaint = Paint()
        this.dividerPaint.color = Color.BLUE

        this.dividerWidth = dividerWidth
        this.dividerWidthTop = this.dividerWidth / 2
        this.dividerWidthBot = this.dividerWidth - dividerWidthTop
    }

    override fun getItemOffsets(outRect: Rect, child: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, child, parent, state)

        val pos = parent.getChildAdapterPosition(child)
        val column = pos % spanCount// 计算这个child 处于第几列


        outRect.top = dividerWidthTop
        outRect.bottom = dividerWidthBot

        outRect.left = column * dividerWidth / spanCount
        outRect.right = dividerWidth - (column + 1) * dividerWidth / spanCount

//        Log.e("getItemOffsets", "pos=" + pos + ", column=" + column + " , left=" + outRect.left + ", right="
//                + outRect.right + ", dividerWidth=" + dividerWidth)
    }

}
