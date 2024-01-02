package com.yic3.lib.entity

data class RechargePriceEntity(
    val id: String, val name: String,
    val description: String, val price: String,
    val tips: String?, val originPrice: String,
    val unit: String, val checked: Boolean = false,
    val showPrice: String, val tipsUrl: String?
)