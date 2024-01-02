package com.yic3.lib.entity

data class OrderPayEntity(
    val prepayId: String?,
    val partnerId: String?,
    val timeStamp: String?,
    val nonceStr: String?,
    val `package`: String?,
    val sign: String?,
    val payParam: String?,
    val appId: String?,
    val orderId: Int?,
    val orderNo: String?
)