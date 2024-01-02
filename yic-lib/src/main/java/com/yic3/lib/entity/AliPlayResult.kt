package com.yic3.lib.entity

class AliPlayResult {
    /**
     * alipay_trade_app_pay_response : {"code":"10000","msg":"Success","app_id":"2021000119690678","auth_app_id":"2021000119690678","charset":"UTF-8","timestamp":"2020-09-29 16:27:24","out_trade_no":"1601368022","total_amount":"21.00","trade_no":"2020092922001417990501171173","seller_id":"2088621958667433"}
     * sign : YZJDBIa/knhyZf0YzXPEB6ECEXHiwJwod0q1INRje9BydfoF8N4I+jm2JK0FWH/ioAjjKnmN7So+0U1Nytx67Y3J7pS4RYHrhMsM/pRKEb9XlmBM2incqes6o+rPKd9cOE0uGSPUy1EWxeW5pmEqLPXA0m/Hg0Xn2J0XVHwQKKsAPw4bcKjLOVfXaTbDUY/RidDIfi6ExeSIumcM8ikR2rAYhZaVW0vO9BJ8GWS1KkvpcEpHYgoe7q7TaWU5HI5yERTHenF5WLu4yKxWcPYYjS2cKG1KBdIf1kO+ss2WXK4qJPMoWvv2rSPHpBGAzpD8eEVb5Y+WCyDiSGENYUTTFA==
     * sign_type : RSA2
     */
    var alipay_trade_app_pay_response: AlipayTradeAppPayResponseBean? = null
    var sign: String? = null
    var sign_type: String? = null

}

class AlipayTradeAppPayResponseBean {
    /**
     * code : 10000
     * msg : Success
     * app_id : 2021000119690678
     * auth_app_id : 2021000119690678
     * charset : UTF-8
     * timestamp : 2020-09-29 16:27:24
     * out_trade_no : 1601368022
     * total_amount : 21.00
     * trade_no : 2020092922001417990501171173
     * seller_id : 2088621958667433
     */
    var code: String? = null
    var msg: String? = null
    var app_id: String? = null
    var auth_app_id: String? = null
    var charset: String? = null
    var timestamp: String? = null
    var out_trade_no: String? = null
    var total_amount: String? = null
    var trade_no: String? = null
    var seller_id: String? = null
}