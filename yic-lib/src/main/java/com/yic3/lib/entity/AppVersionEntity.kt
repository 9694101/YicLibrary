package com.yic3.lib.entity

data class AppVersionEntity(
    val title: String?,
    val url: String?,
    // 是否强制更新
    val force: Boolean? = false,
    //升级内容
    val description: String?,
    val version: String?
)
