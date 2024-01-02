package com.yic3.lib.util

import android.app.Activity
import android.util.Log
import com.alipay.sdk.app.PayTask
import com.blankj.utilcode.util.GsonUtils
import com.yic3.lib.entity.AliPlayResult
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus


object AlipayUtil {

    fun pay(activity: Activity, body: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = withContext(Dispatchers.Default) {
                val alipay = PayTask(activity)
                alipay.payV2(body, true)
            }
            Log.e("zz", GsonUtils.toJson(result))
            val resultInfo: String? = result["result"] // 同步返回需要验证的信息
            var outTradeNo: String? = null
            if (!resultInfo.isNullOrEmpty()) {
                try {
                    val aliPlayResult: AliPlayResult =
                        GsonUtils.fromJson(resultInfo, AliPlayResult::class.java)
                    outTradeNo = aliPlayResult.alipay_trade_app_pay_response?.out_trade_no
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            val resultStatus: String? = result["resultStatus"]
            val success = resultStatus == "9000" && !outTradeNo.isNullOrEmpty()
            EventBus.getDefault().post(AlipayResultEvent(success))
        }
    }

}

class AlipayResultEvent(val success: Boolean)