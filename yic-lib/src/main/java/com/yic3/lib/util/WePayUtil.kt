package com.yic3.lib.util

import android.content.Context
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.yic3.lib.base.AccountConfig
import com.yic3.lib.entity.OrderPayEntity

object WePayUtil {

    fun pay(context: Context, info: OrderPayEntity) {
        val createWXAPI = WXAPIFactory.createWXAPI(context, UserInfoManager.getWxAppId())
        if (!createWXAPI.isWXAppInstalled) {
            ZZToast.showInfo("您还没有安装微信APP")
            return
        }
        val request = PayReq()
        request.appId = info.appId
        request.partnerId = info.partnerId
        request.prepayId = info.prepayId
        request.packageValue = info.`package`
        request.nonceStr = info.nonceStr
        request.timeStamp = info.timeStamp
        request.sign = info.sign
        createWXAPI.sendReq(request)
    }

}