package com.htphtp.tools.paging

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class PagingAdapter<ItemData, VH : RecyclerView.ViewHolder>(diffCallback: DiffUtil.ItemCallback<ItemData>, private val retryCallback: () -> Unit = {}) : PagedListAdapter<ItemData, RecyclerView.ViewHolder>(diffCallback) {

    private var networkState: NetworkState? = null

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NetworkViewHolder.VIEW_TYPE -> {
                NetworkViewHolder.create(parent, retryCallback)
            }
            viewTypeByLayoutId() -> {
                createViewHolder(parent)
            }
            else -> {
                throw IllegalArgumentException("unknown view type $viewType")
            }
        }
    }

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NetworkViewHolder) {
            networkState?.let { holder.bindTo(it, position) }
        } else {
            onViewHolder(holder as VH, position)
        }

    }

    final override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            NetworkViewHolder.VIEW_TYPE
        } else {
            viewTypeByLayoutId()
        }
    }

    abstract fun viewTypeByLayoutId(): Int
    abstract fun createViewHolder(parent: ViewGroup): VH
    abstract fun onViewHolder(holder: VH, position: Int)

    final override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun hasExtraRow(): Boolean = networkState != null && networkState != NetworkState.LOADED

    fun setNetworkState(networkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()

        this.networkState = networkState
        val hasExtraRow = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(itemCount)
            } else {
                notifyItemInserted(itemCount)
            }
        } else if (hasExtraRow && previousState != networkState) {
            notifyItemChanged(itemCount - 1)
        }
    }


}