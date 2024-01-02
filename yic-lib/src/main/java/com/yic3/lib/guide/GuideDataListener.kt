package com.yic3.lib.guide

interface GuideDataListener {

    fun getSelectData(): Map<String, Any>

    fun getTitleCharSequence(): CharSequence

    fun getBurialId(): String

}