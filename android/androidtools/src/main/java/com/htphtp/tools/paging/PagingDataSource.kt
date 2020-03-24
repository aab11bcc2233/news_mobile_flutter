package com.htphtp.tools.paging

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource

abstract class PagingDataSource<ItemData> : PageKeyedDataSource<Int, ItemData>() {

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ItemData>) {

    }


    abstract class Factory<Data> : DataSource.Factory<Int, Data>()
}