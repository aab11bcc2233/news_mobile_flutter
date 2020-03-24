package com.htphtp.tools.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList

class PagingViewModel<ItemData>(repository: PagingRepository<ItemData>) :
        ViewModel() {
    private val listing: Listing<ItemData> = repository.getListing()

    val pagedList: LiveData<PagedList<ItemData>>
    val refreshState: LiveData<NetworkState>
    val networkState: LiveData<NetworkState>

    init {

        pagedList = listing.pagedList
        refreshState = listing.refreshState
        networkState = listing.networkState
    }


    fun refresh() {
        listing.refresh()
    }

    fun retry() {
        listing.retry()
    }

    abstract class Factory<ItemData, out DataSource : SimplePagingDataSource<ItemData>>(private var pageSize: Int) : ViewModelProvider.Factory {

        abstract fun dataSource(): DataSource

        fun pagedConfig() = PagedList.Config.Builder()
                .setInitialLoadSizeHint(pageSize)
                .setPageSize(pageSize)
                .setPrefetchDistance(pageSize / 3)
                .build()

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val repository = PagingRepository<ItemData>(
                    pagedConfig(),
                    object : SimplePagingDataSource.Factory<ItemData>() {
                        override fun onCreate(): SimplePagingDataSource<ItemData> {
                            return dataSource()
                        }

                    }
            )

            return PagingViewModel<ItemData>(repository) as T
        }
    }
}