package com.yic3.lib.util

import android.content.Context
import com.blankj.utilcode.util.ThreadUtils

object PushUtil {

    @JvmStatic
    fun init(context: Context) {
//        JCollectionAuth.setAuth(context, true)
//        JPushInterface.setDebugMode(true)
//        JPushInterface.init(context)
        setUserAlias(context)
    }

    @JvmStatic
    fun setUserAlias(context: Context) {
        if (UserInfoManager.isLogin()) {
            ThreadUtils.runOnUiThreadDelayed({
                if (UserInfoManager.isLogin()) {
                    val userId = UserInfoManager.userId
//                    JPushInterface.setAlias(context, userId, userId.toString())
                }
            }, 10000)
        }
    }

    @JvmStatic
    fun clearUserAlias(context: Context) {
        // JPushInterface.deleteAlias(context, 1)
        // JPushInterface.setAlias(context, -1, "-1")
    }

    @JvmStatic
    fun isPushOpen(context: Context): Boolean {
        // return !JPushInterface.isPushStopped(context)
        return true
    }

    @JvmStatic
    fun openPush(context: Context) {
        // JPushInterface.resumePush(context)
    }

    @JvmStatic
    fun closePush(context: Context) {
        // JPushInterface.stopPush(context)
    }

}