package com.yic3.lib.entity

class AppInitConfig {

    companion object {
        const val OPEN_SERVICE_CONFIG = "client.center.weixin.service"
        const val NEW_GUIDE_ENABLE = "client.guide.enable"
        const val GUIDE_PAY_ENABLE = "client.guide.pay.enable"
        const val PAY_ENABLE = "client.pay.enable"

        const val APP_UPDATE_INFO = "client.version.upgrade"

        const val APP_SHARE_URL = "client.center.share.url"

        const val HOME_BANNER = "client.home.banner"
        const val SEARCH_BANNER = "client.search.bid.banner"
        const val COMPANY_BANNER = "client.search.company.banner"

        const val HOST_URL = "client.api.url"
        const val RECHARGE_DISCOUNT_GOODS = "client.float.goods.tips"
        const val RECHARGE_DISCOUNT_GOODS_CREATE = "client.float.goods.create"

        const val GUIDE_PAY_ENABLE_CREATE = "client.guide.pay.page"
        const val GUIDE_FRAGMENT_DATA = "client.guide.config"

        const val LOGIN_BEFORE_RECHARGE = "client.login.first"
        const val WECHAT_APP_ID = "client.wxpay.appid"

        const val USER_SHARE_INVITATION_URL = "client.share.boss.url"
        const val USER_SHARE_INVITATION = "client.user.share.enable"

        const val USER_SHARE_WORKER_URL = "client.share.worker.url"
        const val USER_SHARE_RECRUIT_URL = "client.share.recruit.url"

        const val USER_COMMENT_WORKER = "client.evaluate.config"
    }

    var config: Map<String, Any>? = null
    var token: String = ""
    var userId: String = ""
    var hashId: String = ""
    var debug: Any? = null

}