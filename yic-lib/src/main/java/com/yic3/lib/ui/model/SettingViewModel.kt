package com.yic3.lib.ui.model

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.PhoneUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.Utils
import com.github.gzuliyujiang.oaid.DeviceIdentifier
import com.yic3.lib.entity.AppInitConfig
import com.yic3.lib.entity.LogoutEntity
import com.yic3.lib.net.NetworkApi
import com.yic3.lib.util.UserInfoManager
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState

class SettingViewModel: BaseViewModel() {

    val newTokenResult = MutableLiveData<ResultState<AppInitConfig>>()
    private val closeResult = MutableLiveData<ResultState<LogoutEntity>>()

    val logoutResult = MutableLiveData<ResultState<LogoutEntity>>()

    init {
        closeResult.observeForever {
            if (it is ResultState.Success) {
                UserInfoManager.saveToken(it.data.token)
                getInitConfig(Utils.getApp())
            }
        }
    }

    fun close() {
        request({
            NetworkApi.service.logout()
        }, closeResult, false)
    }

    fun logout() {
        request({
            NetworkApi.service.logout()
        }, logoutResult)
    }

    @SuppressLint("MissingPermission")
    fun getInitConfig(application: Application) {
        val oaId = DeviceIdentifier.getOAID(application)
        val imei = if (PermissionUtils.isGranted(Manifest.permission.READ_PHONE_STATE)) {
            PhoneUtils.getIMEI()
        } else {
            ""
        }
        request({NetworkApi.service.appConfig(oaId, imei)}, newTokenResult, true)
    }

    companion object {

        private const val KEY_NAME = "setting"
        private const val KEY_PERSONALIZED = "personalized"

        fun isPersonalizedOpen(): Boolean {
            return SPUtils.getInstance(KEY_NAME).getBoolean(KEY_PERSONALIZED, true)
        }

        fun savePersonalizedState(isOpen: Boolean) {
            SPUtils.getInstance(KEY_NAME).put(KEY_PERSONALIZED, isOpen)
        }
    }

}