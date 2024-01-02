package com.yic3.lib.net

import me.hgj.jetpackmvvm.network.BaseResponse

data class ApiResponse<T>(var code: Int, var message: String, var data: T): BaseResponse<T>() {

    override fun getResponseCode(): Int {
        return code
    }

    override fun getResponseData(): T {
        return data
    }

    override fun getResponseMsg(): String {
        return message
    }

    override fun isSucces(): Boolean {
        return code == 0
    }

}