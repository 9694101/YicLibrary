package com.yic3.lib.util

import android.content.Context
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.tencent.mm.opensdk.constants.Build
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.modelbiz.WXOpenCustomerServiceChat
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.yic3.lib.base.AccountConfig


object WechatUtil {

    const val WX_USER_INFO = "snsapi_userinfo"
    const val MINI_ID = "gh_5ce445ba8d6d"

    @JvmStatic
    fun login(context: Context, appId: String = UserInfoManager.getWxAppId()) {
        val api = WXAPIFactory.createWXAPI(context, appId, false)
        api.registerApp(appId)
        if (!api.isWXAppInstalled) {
            ZZToast.showInfo("您还没有安装微信APP")
        } else {
            val req = SendAuth.Req()
            req.scope = WX_USER_INFO
            req.state = "wx_login"
            api.sendReq(req)
        }
    }

    @JvmStatic
    fun openMini(context: Context, path: String) {
        val api = WXAPIFactory.createWXAPI(context, UserInfoManager.getWxAppId())
        if (!api.isWXAppInstalled) {
            ZZToast.showInfo("您还没有安装微信APP")
            return
        }
        val req = WXLaunchMiniProgram.Req()
        req.userName = MINI_ID // 填小程序原始id
        req.path = path ////拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
        req.miniprogramType = if (AppUtils.isAppDebug()) {
            WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW
        } else {
            WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE
        }
        api.sendReq(req)
    }

    @JvmStatic
    fun openService(context: Context, corpId: String, url: String) {
        val api = WXAPIFactory.createWXAPI(context, UserInfoManager.getWxAppId())
        if (!api.isWXAppInstalled) {
            ZZToast.showInfo("您还没有安装微信APP")
            return
        }
        // 判断当前版本是否支持拉起客服会话
        // 判断当前版本是否支持拉起客服会话
        if (api.wxAppSupportAPI >= Build.SUPPORT_OPEN_CUSTOMER_SERVICE_CHAT) {
            val req = WXOpenCustomerServiceChat.Req()
            req.corpId = corpId // 企业ID
            req.url = url // 客服URL
            api.sendReq(req)
        }
    }

}

class WxPaySuccessEvent(val errorCode: Int)