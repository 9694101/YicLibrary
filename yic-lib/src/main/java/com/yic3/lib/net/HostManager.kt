package com.yic3.lib.net

import com.blankj.utilcode.util.SPUtils

object HostManager {

    private const val DEFAULT_HOST = "http://easy.boss.orgpeel.cn" // "https://recruit.yic3.cn" // "https://recruit.sg37.cn"

    private fun getSp(): SPUtils {
        return SPUtils.getInstance("hostConfig")
    }

    private var _host: String? = null

    fun getHost(): String {
//        if (_host.isNullOrEmpty()) {
//            _host = getSp().getString("host")
//        }
        if (_host.isNullOrEmpty()) {
            _host = DEFAULT_HOST
        }
        return _host!!
    }

    fun saveHost(host: String) {
        getSp().put("host", host)
        _host = host
    }

}