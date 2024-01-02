package com.yic3.lib.util

import android.app.Activity
import com.blankj.utilcode.util.AppUtils
import com.yic3.lib.event.LogoutEvent
import com.yic3.lib.ui.CommonRechargeActivity
import org.greenrobot.eventbus.EventBus

object UserClickUtil {

    private val APP_RECHARGE_ACTIVITY_NAME = "${AppUtils.getAppPackageName()}.recharge.AppRechargeActivity"

    fun checkLimit(source: String): Boolean {
        val cls: Class<out Activity> = Class.forName(APP_RECHARGE_ACTIVITY_NAME) as Class<out Activity>
        if (UserInfoManager.isOpenGuidePay()) {
            if (UserInfoManager.isOpenPay()) {
                if (!UserInfoManager.isVip()) {
                    CommonRechargeActivity.openActivity(source, cls)
                    return false
                }
            }
            if (!UserInfoManager.isLogin()) {
                EventBus.getDefault().post(LogoutEvent())
                return false
            }
        } else {
            if (!UserInfoManager.isLogin()) {
                EventBus.getDefault().post(LogoutEvent())
                return false
            }
            if (UserInfoManager.isOpenPay()) {
                if (!UserInfoManager.isVip()) {
                    CommonRechargeActivity.openActivity(source, cls)
                    return false
                }
            }
        }
        return true
    }

}