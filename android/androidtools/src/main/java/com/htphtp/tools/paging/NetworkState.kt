package com.htphtp.tools.paging

data class NetworkState private constructor(
        val status: Status,
        val msg: String? = null) {
    companion object {
        val LOADED = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.RUNNING)

        fun noMoreData(msg: String?) = NetworkState(Status.NO_MORE_DATA, msg)
        fun error(msg: String?) = NetworkState(Status.FAILED, msg)
    }

    enum class Status {
        RUNNING,
        SUCCESS,
        FAILED,
        NO_MORE_DATA
    }
}