package com.yic3.lib.ui.model

import androidx.lifecycle.MutableLiveData
import com.yic3.lib.entity.LogoutEntity
import com.yic3.lib.net.NetworkApi
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState

open class LoginViewModel: BaseViewModel() {

    val sendResult = MutableLiveData<ResultState<Any>>()

    fun sendSms(phone: String) {
        request({
            NetworkApi.service.sendSms(phone)
        }, sendResult)
    }

    val loginResult = MutableLiveData<ResultState<LogoutEntity>>()

    fun loginWithPhone(phone: String, code: String) {
        val param = mutableMapOf<String, Any>()
        param["loginType"] = "phone"
        val phoneParam = mutableMapOf<String, Any>()
        phoneParam["phone"] = phone
        phoneParam["code"] = code
        param["phone"] = phoneParam

        request({
            NetworkApi.service.login(param)
        }, loginResult)
    }

    fun loginWithWechat(code: String) {
        val param = mutableMapOf<String, Any>()
        param["loginType"] = "weixin"
        val phoneParam = mutableMapOf<String, Any>()
        phoneParam["code"] = code
        param["weixin"] = phoneParam

        request({
            NetworkApi.service.login(param)
        }, loginResult)
    }

}