package com.htphtp.tools.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

open class SimplePagingDataSource<ItemData>() : PagingDataSource<ItemData>() {

    val initialLoad: LiveData<NetworkState> = MutableLiveData()
    val networkState: LiveData<NetworkState> = MutableLiveData()

    private var mRetry: (() -> Unit)? = null

    fun retry() {
        val previousRetry = mRetry
        mRetry = null

        previousRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }

    protected fun setRetry(fn: (() -> Unit)?) {
        mRetry = fn
    }

    override fun loadInitial(params: PageKeyedDataSource.LoadInitialParams<Int>, callback: PageKeyedDataSource.LoadInitialCallback<Int, ItemData>) {

    }

    override fun loadAfter(params: PageKeyedDataSource.LoadParams<Int>, callback: PageKeyedDataSource.LoadCallback<Int, ItemData>) {

    }


    protected fun postError(networkState: MutableLiveData<NetworkState>, message: String?) {
        networkState.postValue(NetworkState.error(message))
    }

    companion object {
        private val retryExecutor: ExecutorService by lazy { Executors.newFixedThreadPool(3) }
    }

    abstract class Factory<ItemData>() : PagingDataSource.Factory<ItemData>() {
        val sourceLiveData: LiveData<SimplePagingDataSource<ItemData>> = MutableLiveData()

        final override fun create(): DataSource<Int, ItemData> {
            val dataSource = onCreate()
            (sourceLiveData as MutableLiveData).postValue(dataSource)
            return dataSource
        }

        abstract fun onCreate(): SimplePagingDataSource<ItemData>
    }

//    class SimpleFactory<RequestParameter, ItemData>(private val requestParameter: RequestParameter) : Factory<RequestParameter, ItemData>(requestParameter) {
//        override fun onCreate(): SimplePagingDataSource<RequestParameter, ItemData> {
//            return SimplePagingDataSource(requestParameter)
//        }
//    }
}