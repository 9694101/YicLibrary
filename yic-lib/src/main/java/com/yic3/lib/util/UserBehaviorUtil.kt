package com.yic3.lib.util

import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.Utils
import com.umeng.analytics.MobclickAgent
import com.yic3.lib.entity.AppInitConfig

object UserBehaviorUtil {

    private const val KEY_SOURCE = "source"
    private const val KEY_TYPE = "type"
    private const val KEY_KEY = "key"
    private const val KEY_VALUE = "value"
    private const val KEY_EXTRA = "extra"

    fun postToService(event: StatEvent, value: Any? = null, extras: Map<String, Any>? = null, type: StatType = StatType.点击) {
        try {
            val map = mutableMapOf<String, Any>()
            map[KEY_SOURCE] = "client"
            map[KEY_TYPE] = type.type

            val initConfig = UserInfoManager.getInitConfig(AppInitConfig.GUIDE_FRAGMENT_DATA)
            val purposeKey = if (initConfig is Map<*, *>) {
                initConfig["key"] ?: "service"
            } else {
                "service"
            }

            val statKey = "$purposeKey.${event.key}"
            map[KEY_KEY] = statKey

            value?.let {
                map[KEY_VALUE] = it.toString()
            }

            extras?.let {
                map[KEY_EXTRA] = GsonUtils.toJson(it)
            }

//            if (!AppUtils.isAppDebug()) {
//                CoroutineScope(Dispatchers.Default).launch {
//                    try {
//                        NetworkApi.service.userBehavior(map)
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//                eventObject(statKey, map)
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun postToService(key: String, value: String? = null, extras: Map<String, Any>? = null, type: String = StatType.点击.type) {
        try {
            val map = mutableMapOf<String, Any>()
            map[KEY_SOURCE] = "client"
            map[KEY_TYPE] = type

            val initConfig = UserInfoManager.getInitConfig(AppInitConfig.GUIDE_FRAGMENT_DATA)
            val purposeKey = if (initConfig is Map<*, *>) {
                initConfig["key"] ?: "service"
            } else {
                "service"
            }

            val statKey = "$purposeKey.${key}"
            map[KEY_KEY] = statKey

            value?.let {
                map[KEY_VALUE] = value
            }
            extras?.let {
                map[KEY_EXTRA] = GsonUtils.toJson(extras)
            }
//            if (!AppUtils.isAppDebug()) {
//                CoroutineScope(Dispatchers.Default).launch {
//                    try {
//                        NetworkApi.service.userBehavior(map)
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//                eventObject(key, map)
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun eventObject(eventId: String, data: Map<String, Any>? = null) {
        try {
            val context = Utils.getApp()
            if (data == null) {
                MobclickAgent.onEvent(context, eventId)
            } else {
                MobclickAgent.onEventObject(context, eventId, data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

enum class StatType(val type: String) {
    点击("click"), 错误("error")
}

enum class StatEvent(val key: String) {
    立即体验("user.splash.click"),

    网络请求错误("user.network.error"),

    支付点击("user.vip.pay.click"),

    引导页点击("user.guide.next.click"),

    生成计划等待("user.guide.creating.wait"),

    分享招工详细("user.share.recruit.info"),

    分享我发布的招工("user.share.recruit.publish"),

    分享邀请有礼("user.share.invite.reward"),

    语音合成失败("user.syntherizer.result.fail")

}