package com.htphtp.tools.view

import android.graphics.Color
import android.view.View
import androidx.annotation.IntRange
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * Create by htp on 2019/3/9
 */

/**
 * @param selectionClick 实现此接口去写单选、多选逻辑。 已经有(SingleSelectionClick, MultiSelectionClick)的实现。
 * @param onItemClickListener 会在 selectionClick 之后调用。
 */
abstract class SelectionAdapter<ItemData : Selection> : RecyclerView.Adapter<SelectionAdapter.ViewHolder> {

    open var datas: List<ItemData> = emptyList()
        set(value) {
            field = value
            removeSelected()
        }

    var selectionClick: SelectionClick<ItemData> = SingleSelectionClick(false)
    var onItemClickListener: (data: ItemData, position: Int) -> Unit = { _, _ -> }
    var isEnableSelection = true
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    constructor()

    constructor(
            datas: List<ItemData>,
            selectionClick: SelectionClick<ItemData>,
            onItemClickListener: (data: ItemData, position: Int) -> Unit) {
        this.datas = datas
        this.selectionClick = selectionClick
        this.onItemClickListener = onItemClickListener
    }

    fun removeSelected() {
        if (selectionClick is SingleSelectionClick) {
            (selectionClick as SingleSelectionClick).reSetSelected()

        }

        if (selectionClick is MultiSelectionClick) {
            (selectionClick as MultiSelectionClick).reSetSelected()
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = datas[position]
        onBindHolder(data, holder, position)
        if (isEnableSelection) {
            selectionClick.onViewHolder(this, data, holder, position)
            holder.itemView.setOnClickListener {
                selectionClick.onItemClickListener(this, data, position)
                onItemClickListener(data, position)
            }
        }
    }

    abstract fun onBindHolder(
            data: ItemData,
            holder: RecyclerView.ViewHolder,
            position: Int
    )

    override fun getItemCount(): Int {
        return datas.size
    }

    open fun getSelected(): List<ItemData> {
        if (selectionClick is SingleSelectionClick) {
            val single = selectionClick as SingleSelectionClick
            if (single.selected == null) {
                return emptyList()
            }
            return listOf(single.selected!!)
        }

        if (selectionClick is MultiSelectionClick) {
            val multi = selectionClick as MultiSelectionClick
            return multi.selected
        }

        return datas.filter { it.isSelected }
    }

    open class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}

interface Selection {
    val text: String
    var isSelected: Boolean

    class Type private constructor(val max: Int = Int.MAX_VALUE) {
        companion object {
            @JvmStatic
            val SINGLE = Type(1)

            @JvmStatic
            fun MULTI(@IntRange(from = 2) max: Int = Int.MAX_VALUE): Type {
                if (max <= 1) {
                    return SINGLE
                }
                return Type(max)
            }
        }
    }
}

/**
 * 实现此接口去写单选、多选逻辑
 */
interface SelectionClick<ItemData : Selection> {
    fun onViewHolder(
            adapter: SelectionAdapter<ItemData>,
            data: ItemData,
            holder: RecyclerView.ViewHolder,
            position: Int
    )

    fun onItemClickListener(
            adapter: SelectionAdapter<ItemData>,
            clickItemData: ItemData,
            position: Int
    )
}

class SingleSelectionClick<ItemData : Selection>(val isCancel: Boolean) : SelectionClick<ItemData> {

    override fun onViewHolder(
            adapter: SelectionAdapter<ItemData>,
            data: ItemData,
            holder: RecyclerView.ViewHolder,
            position: Int
    ) {
        if (data.isSelected) {
            selected = data
            sPosition = position
        }
    }

    var selected: ItemData? = null
        private set
    private var sPosition: Int = -1


    fun reSetSelected() {
        selected = null
    }

    override fun onItemClickListener(
            adapter: SelectionAdapter<ItemData>,
            clickItemData: ItemData,
            position: Int
    ) {
        if (selected == null) {
            clickItemData.isSelected = true
            selected = clickItemData
            sPosition = position
        } else {
            if (selected == clickItemData) {
                if (!isCancel) {
                    return
                }

                clickItemData.isSelected = false
                sPosition = -1
                selected = null
            } else {
                selected?.isSelected = false

                if (sPosition != -1) {
                    adapter.notifyItemChanged(sPosition)
                }

                clickItemData.isSelected = true
                selected = clickItemData
                sPosition = position
            }
        }

        adapter.notifyItemChanged(position)
    }
}

abstract class MultiSelectionClick<ItemData : Selection>(val max: Int? = Int.MAX_VALUE) : SelectionClick<ItemData> {

    var isFilledUpMax = false
        private set
    val normalColor = Color.parseColor("#333333")
    val disableColor = Color.parseColor("#cccccc")

    val selected: ArrayList<ItemData> by lazy { ArrayList<ItemData>() }

    fun reSetSelected() {
        selected.clear()
        isFilledUpMax = false
    }

    abstract fun onFilledUpMax()

    override fun onItemClickListener(
            adapter: SelectionAdapter<ItemData>,
            clickItemData: ItemData,
            position: Int
    ) {
        val b = !clickItemData.isSelected

//        if (max != null) {
        if (b) {
            if (isFilledUpMax) {
                onFilledUpMax()
                return
            }

            selected.add(clickItemData)
            clickItemData.isSelected = b

            if (selected.size == max) {
                isFilledUpMax = true
                adapter.notifyDataSetChanged()
            } else {
                adapter.notifyItemChanged(position)
            }

        } else {
            val isPreFilledUp = isFilledUpMax

            selected.remove(clickItemData)
            clickItemData.isSelected = b
            isFilledUpMax = false

            if (isPreFilledUp) {
                adapter.notifyDataSetChanged()
            } else {
                adapter.notifyItemChanged(position)
            }
        }

//        } else {
//            clickItemData.isSelected = b
//            adapter.notifyItemChanged(position)
//        }

    }
}