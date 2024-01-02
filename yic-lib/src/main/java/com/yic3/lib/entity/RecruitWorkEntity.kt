package com.yic3.lib.entity

data class RecruitWorkEntity(
    val children: List<WorkChildren>,
    val id: Int,
    val name: String,
    val pid: Int
)

data class WorkChildren(
    val children: List<Any>,
    val id: Int,
    val name: String,
    val pid: Int
)