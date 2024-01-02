package com.yic3.lib.guide

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.PhoneUtils
import com.blankj.utilcode.util.SPUtils
import com.github.gzuliyujiang.oaid.DeviceIdentifier
import com.yic3.lib.entity.AppInitConfig
import com.yic3.lib.net.NetworkApi
import com.yic3.lib.util.UserInfoManager
import kotlinx.coroutines.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request

class SplashViewModel : BaseViewModel() {

    companion object {
        private const val CONFIG_NAME = "Splash_Config"
        private const val KEY_AGREEMENT = "isAgreement"
        private const val KEY_GUIDE = "isGuide"

        fun getSpUtils(): SPUtils {
            return SPUtils.getInstance(CONFIG_NAME)
        }

        fun saveHasGuide() {
            getSpUtils().put(KEY_GUIDE, true)
        }

        fun clearGuide() {
            getSpUtils().put(KEY_GUIDE, false)
        }

        fun isAgreePrivacy(): Boolean {
            return getSpUtils().getBoolean(KEY_AGREEMENT)
        }
    }

    fun agreePrivacy() {
        getSpUtils().put(KEY_AGREEMENT, true)
    }

    fun isGuide(): Boolean {
        // return false
        return !UserInfoManager.isOpenGuide() || getSpUtils().getBoolean(KEY_GUIDE)
    }

    val initConfigResult = MutableLiveData<AppInitConfig>()

    @SuppressLint("MissingPermission")
    fun getInitConfig(application: Application) {
        val oaId = DeviceIdentifier.getOAID(application)
        val imei = if (PermissionUtils.isGranted(Manifest.permission.READ_PHONE_STATE)) {
            PhoneUtils.getIMEI()
        } else {
            ""
        }
        request({
            NetworkApi.service.appConfig(oaId, imei)
        }, {
            saveInitConfig(it)
            refreshUserInfo()
            initConfigResult.postValue(it)
        }, {
            if (it.errCode == 1004 && !UserInfoManager.token.isNullOrEmpty()) {
                UserInfoManager.clearToken()
                getInitConfig(application)
            }
        }, isShowDialog = false)
    }

    private fun saveInitConfig(initConfig: AppInitConfig) {
        UserInfoManager.saveInitConfig(initConfig)
    }

    private fun refreshUserInfo() {
        request({
            NetworkApi.service.getUserInfo()
        }, {
            UserInfoManager.saveUserInfo(it)
        })
    }

}