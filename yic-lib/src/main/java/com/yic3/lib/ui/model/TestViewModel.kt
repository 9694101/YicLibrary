package com.yic3.lib.ui.model

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.PhoneUtils
import com.github.gzuliyujiang.oaid.DeviceIdentifier
import com.google.gson.reflect.TypeToken
import com.yic3.lib.entity.AppInitConfig
import com.yic3.lib.entity.HostEntity
import com.yic3.lib.net.NetworkApi
import com.yic3.lib.util.UserInfoManager
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState

class TestViewModel: BaseViewModel() {

    val hostResult = MutableLiveData<List<HostEntity>?>()
    val initConfigResult = MutableLiveData<ResultState<AppInitConfig>>()

    fun getHostList() {
        val initConfig = UserInfoManager.getInitConfig(AppInitConfig.HOST_URL)
        val hostJson = GsonUtils.toJson(initConfig)
        val type = object : TypeToken<List<HostEntity>>(){}.type
        val list: List<HostEntity>? = GsonUtils.fromJson(hostJson, type)
        hostResult.postValue(list)
    }

    @SuppressLint("MissingPermission")
    fun getInitConfig(application: Application) {
        val oaId = DeviceIdentifier.getOAID(application)
        val imei = if (PermissionUtils.isGranted(Manifest.permission.READ_PHONE_STATE)) {
            PhoneUtils.getIMEI()
        } else {
            ""
        }
        request({ NetworkApi.service.appConfig(oaId, imei)}, initConfigResult, true)
    }

}