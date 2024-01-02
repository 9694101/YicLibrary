package com.yic3.lib.entity

class UserInfoEntity(
    val bossId: Long, val avatar: String?,
    val name: String?, val gender: Int?,
    val age: Int?, val bestScore: Int,
    val guest: Boolean = false, var role: Int,
    val vipExpireTime: Long, val vipName: String,
    var subject: String, var type: String?,
    var phone: String?, var wxNickname: String?,
    var provinceId: String?, val craft: String?,
    val craftText: String, val isAuth: Int,
    var cityId: String?, var countyId: String?,
    var imageUrl: String?, var handImageUrl: String?
)