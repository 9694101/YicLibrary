package com.yic3.lib.util

import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import com.yic3.lib.base.AccountConfig
import com.yic3.lib.entity.AppInitConfig
import com.yic3.lib.entity.UserInfoEntity
import org.greenrobot.eventbus.EventBus

object UserInfoManager {

    private const val CONFIG_NAME = "userInfoConfig"
    private const val KEY_TOKEN = "token"
    private const val KEY_USER = "user"
    private const val KEY_USER_ID = "userId"
    private const val KEY_CONFIG = "initConfig"


    private fun getSaveSP(): SPUtils {
        return SPUtils.getInstance(CONFIG_NAME)
    }

    var token: String? = null
    get() {
        if (field.isNullOrEmpty()) {
            field = getSaveSP().getString(KEY_TOKEN)
        }
        return field
    }

    var userId: String? = null
    get() {
        if (field.isNullOrEmpty()) {
            field = getSaveSP().getString(KEY_USER_ID)
        }
        return field
    }

    fun saveToken(token: String) {
        if (token.isNotEmpty()) {
            UserInfoManager.token = token
            getSaveSP().put(KEY_TOKEN, token)
        }
    }

    fun clearToken() {
        token = ""
        getSaveSP().put(KEY_TOKEN, "")
    }

    fun isLoginBeforeRecharge(): Boolean {
        return !isLogin() && getInitConfig(AppInitConfig.LOGIN_BEFORE_RECHARGE) == true
    }

    private fun saveUserId(userId: String) {
        UserInfoManager.userId = userId
        getSaveSP().put(KEY_USER_ID, userId)
    }

    fun saveUserInfo(userInfo: UserInfoEntity?) {
        UserInfoManager.userInfo = userInfo
        getSaveSP().put(KEY_USER, GsonUtils.toJson(userInfo))
        EventBus.getDefault().post(UserInfoUpdatedEvent())
    }

    var userInfo: UserInfoEntity? = null
    get() {
        if (field == null) {
            val userJson = getSaveSP().getString(KEY_USER)
            Log.e("zz", "userJson: $userJson")
            field = GsonUtils.fromJson(userJson, UserInfoEntity::class.java)
        }
        return field
    }

    var initConfig: AppInitConfig? = null
    get() {
        if (field == null) {
            field = GsonUtils.fromJson(getSaveSP().getString(KEY_CONFIG), AppInitConfig::class.java)
        }
        return field
    }

    fun getInitConfig(key: String): Any? {
        return initConfig?.config?.get(key)
    }

    fun getWxAppId(): String {
        return getInitConfig(AppInitConfig.WECHAT_APP_ID)?.toString() ?: AccountConfig.WECHAT_APP_ID
    }

    fun getWxServiceInfo(): Map<String, Any>? {
        return getInitConfig(AppInitConfig.OPEN_SERVICE_CONFIG) as Map<String, Any>?
    }

    fun isOpenGuide(): Boolean {
        return getInitConfig(AppInitConfig.NEW_GUIDE_ENABLE) as Boolean? == true
    }

    fun isOpenGuidePay(): Boolean {
        return getInitConfig(AppInitConfig.GUIDE_PAY_ENABLE) as Boolean? == true
    }

    fun isOpenPay(): Boolean {
        return getInitConfig(AppInitConfig.PAY_ENABLE) as Boolean? == true
    }

    fun isOpenInviteReward(): Boolean {
        return getInitConfig(AppInitConfig.USER_SHARE_INVITATION) as Boolean? == true
    }

    fun getPayChannelList(): List<String> {
        return listOf("weixin", "alipay")
        // return initConfig?.config?.get(AppInitConfig.PAY_CHANNEL_LIST) as List<String>? ?: emptyList()
    }

    fun isLogin(): Boolean {
        return userInfo?.guest == false
    }

    fun isAuth(): Boolean {
        return userInfo?.isAuth == 1
    }

    fun isVip(): Boolean {
        userInfo?.let {
            return it.role > 1 && it.vipExpireTime > (System.currentTimeMillis() / 1000)
        }
        return false
//        return true
    }

    fun getVipRole(role: Int? = userInfo?.role): VipRole {
        return when (role) {
            1 -> VipRole.非会员
            2 -> VipRole.月度会员
            3 -> VipRole.年度会员
            4 -> VipRole.终身会员
            else -> VipRole.非会员
        }
    }

    fun saveInitConfig(initConfig: AppInitConfig) {
        UserInfoManager.initConfig = initConfig
        saveToken(initConfig.token)
        saveUserId(initConfig.userId)
        getSaveSP().put(KEY_CONFIG, GsonUtils.toJson(initConfig))
    }

    fun isCreatorPay(): Boolean {
        return getInitConfig(AppInitConfig.GUIDE_PAY_ENABLE_CREATE) == "create"
    }

    fun clearInitConfig() {
        initConfig = null
        getSaveSP().put(KEY_CONFIG, "")
    }

}

class UserInfoUpdatedEvent

enum class VipRole(val role: Int) {
    非会员(1), 月度会员(2), 年度会员(3), 终身会员(4)
}