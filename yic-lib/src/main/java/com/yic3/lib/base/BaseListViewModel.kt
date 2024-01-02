package com.yic3.lib.base

import androidx.lifecycle.MutableLiveData
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

abstract class BaseListViewModel<T>: BaseViewModel() {

    private var page: Int = 1

    val dataListResult = MutableLiveData<Map<Int, List<T>>>()

    fun postValue(dataList: List<T>?) {
        dataListResult.postValue(mapOf(Pair(page, dataList ?: mutableListOf())))
    }

    fun refresh(params: Map<String, Any>? = null, isShowLoading: Boolean = false) {
        page = 1
        getDataList(page, params, isShowLoading)
    }

    fun loadMore(params: Map<String, Any>? = null) {
        page++
        getDataList(page, params)
    }

    abstract fun getDataList(page: Int, params: Map<String, Any>?, isShowLoading: Boolean = false)

}