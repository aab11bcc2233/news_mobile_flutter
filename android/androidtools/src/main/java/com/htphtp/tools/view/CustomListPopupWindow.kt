package com.htphtp.tools.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.ListAdapter
import androidx.appcompat.widget.ListPopupWindow

/**
 * Created by htp on 2018/12/14
 *
 */
class CustomListPopupWindow(context: Context) : ListPopupWindow(context) {
    private val context: Context = context
    private var clickListener: AdapterView.OnItemClickListener? = null
    private var selectedListener: AdapterView.OnItemSelectedListener? = null


    constructor(context: Context, anchorView: View) : this(context) {
        setAnchorView(anchorView)
    }

    init {
        isModal = true

        super.setOnItemClickListener { parent, view, position, id ->
            clickListener?.onItemClick(parent, view, position, id)
            selectedListener?.onItemSelected(parent, view, position, id)
            dismiss()
        }
    }

    override fun setAdapter(adapter: ListAdapter?) {
        super.setAdapter(adapter)
        adapter?.let {
            setContentWidth(measureContentWidth(context, adapter))
        }

    }

    override fun setOnItemSelectedListener(selectedListener: AdapterView.OnItemSelectedListener?) {
        this.selectedListener = selectedListener
    }

    override fun setOnItemClickListener(clickListener: AdapterView.OnItemClickListener?) {
        this.clickListener = clickListener
    }

    companion object {
        fun measureContentWidth(context: Context, listAdapter: ListAdapter): Int {
            var mMeasureParent: ViewGroup? = null
            var maxWidth = 0
            var itemView: View? = null
            var itemType = 0

            val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            val count = listAdapter.count
            for (i in 0 until count) {
                val positionType = listAdapter.getItemViewType(i)
                if (positionType != itemType) {
                    itemType = positionType
                    itemView = null
                }

                if (mMeasureParent == null) {
                    mMeasureParent = FrameLayout(context)
                }

                itemView = listAdapter.getView(i, itemView, mMeasureParent)
                itemView!!.measure(widthMeasureSpec, heightMeasureSpec)

                val itemWidth = itemView.measuredWidth

                if (itemWidth > maxWidth) {
                    maxWidth = itemWidth
                }
            }

            return maxWidth
        }
    }
}