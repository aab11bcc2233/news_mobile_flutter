package com.htphtp.tools.paging

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.htphtp.tools.R
import com.htphtp.tools.view.isVisible
import com.htphtp.tools.view.setProgressBarColor


/**
 * Created by htp on 2018/7/4.
 */
class NetworkViewHolder(itemView: View, private var retryCallback: () -> Unit) : RecyclerView.ViewHolder(itemView) {

    private val msgView: TextView
    private val progressBar: ProgressBar

    init {
        msgView = itemView.findViewById(R.id.msgView)
        progressBar = itemView.findViewById(R.id.progressBar)

        progressBar.setProgressBarColor(Color.parseColor("#dddddd"))
    }

    fun bindTo(item: NetworkState, position: Int) {
        with(itemView) {
            progressBar.isVisible = item == NetworkState.LOADING
            msgView.isVisible = item.msg != null
            msgView.text = item.msg
            setOnClickListener {
                if (item.status == NetworkState.Status.FAILED) {
                    retryCallback()
                }
            }
        }
    }

    companion object {
         val VIEW_TYPE = com.htphtp.tools.R.layout.item_network_state

        fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(VIEW_TYPE, parent, false)

            return NetworkViewHolder(view, retryCallback)
        }

    }
}