package com.htphtp.tools.paging

import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList

class PagingRepository<ItemData>(
        private val pagedConfig: PagedList.Config,
        private val factory: SimplePagingDataSource.Factory<ItemData>
) {

    final fun getListing(): Listing<ItemData> {

        val pagedList = LivePagedListBuilder(
            factory,
            pagedConfig
        ).build()

        return Listing(
            pagedList = pagedList,
            networkState = Transformations.switchMap(factory.sourceLiveData) { it.networkState },
            refreshState = Transformations.switchMap(factory.sourceLiveData) { it.initialLoad },
            refresh = { factory.sourceLiveData.value?.invalidate() },
            retry = { factory.sourceLiveData.value?.retry() }
        )
    }

}