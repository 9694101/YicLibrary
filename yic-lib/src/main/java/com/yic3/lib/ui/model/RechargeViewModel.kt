package com.yic3.lib.ui.model

import androidx.lifecycle.MutableLiveData
import com.yic3.lib.entity.RechargePriceEntity
import com.yic3.lib.net.NetworkApi
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request

open class RechargeViewModel: BaseViewModel() {

    val priceListResult = MutableLiveData<List<RechargePriceEntity>>()

    fun getPriceList() {
        request({ NetworkApi.service.getVipRechargeList()}, {
            priceListResult.postValue(it)
        }, isShowDialog = true)
    }

}