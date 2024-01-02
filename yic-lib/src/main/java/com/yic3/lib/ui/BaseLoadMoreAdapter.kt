package com.yic3.lib.ui

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder

abstract class BaseLoadMoreAdapter<T>(layoutId: Int): BaseQuickAdapter<T, BaseViewHolder>(layoutId), LoadMoreModule {

    override fun addData(newData: Collection<T>) {
        super.addData(newData)
        if (newData.isEmpty()) {
            loadMoreModule.loadMoreEnd()
        } else {
            loadMoreModule.loadMoreComplete()
        }
    }

}