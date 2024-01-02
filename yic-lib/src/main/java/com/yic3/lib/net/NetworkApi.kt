package com.yic3.lib.net

import android.annotation.SuppressLint
import android.os.Build
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.Utils
import com.bytedance.hume.readapk.HumeSDK
import com.google.gson.GsonBuilder
import com.yic3.lib.event.LogoutEvent
import com.yic3.lib.util.*
import me.hgj.jetpackmvvm.network.BaseNetworkApi
import me.hgj.jetpackmvvm.network.CoroutineCallAdapterFactory
import me.hgj.jetpackmvvm.network.interceptor.logging.LogInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkApi: BaseNetworkApi() {

    companion object {
        const val APP_ID = "10027"
        const val APP_PLATFORM = "android"

        var APP_VERSION: String? = null
        get() {
            if (field.isNullOrEmpty()) {
                field = AppUtils.getAppVersionName()
            }
            return field
        }

        var DEVICE_ID: String? = null
        @SuppressLint("MissingPermission")
        get() {
            if (field.isNullOrEmpty()) {
                field = DeviceUtils.getAndroidID()
//                field = if (PermissionUtils.isGranted(Manifest.permission.READ_PHONE_STATE)) {
//                    PhoneUtils.getDeviceId()
//                } else {
//                    DeviceUtils.getUniqueDeviceId()
//                }
            }
            return field
        }

        val CHANNEL: String
        get() {
            val channel: String = HumeSDK.getChannel(Utils.getApp())
            return channel.ifEmpty {
                "ocean"
            }
        }

        var BRAND: String? = Build.BRAND
        var MODEL: String? = Build.MODEL

        private val instance: NetworkApi by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { NetworkApi() }

        //双重校验锁式-单例 封装NetApiService 方便直接快速调用


        private var _service: ApiService? = null
        get() {
            if (field == null) {
                synchronized(LazyThreadSafetyMode.SYNCHRONIZED) {
                    field = instance.getApi(ApiService::class.java, HostManager.getHost())
                }
            }
            return field
        }

        val service: ApiService
        get() {
            return _service!!
        }

        const val USER_AGENT = "Android Device"

        fun changeHost(host: String) {
            _service = instance.getApi(ApiService::class.java, host)
        }

    }

    override fun setHttpClientBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        return builder.apply {
            addInterceptor(YCHeaderInterceptor())
            addInterceptor(ServerFailInterceptor())
            if (AppUtils.isAppDebug()) {
                addInterceptor(LogInterceptor())
            }
            connectTimeout(15, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
        }
    }

    override fun setRetrofitBuilder(builder: Retrofit.Builder): Retrofit.Builder {
        return builder.apply {
            addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            addCallAdapterFactory(CoroutineCallAdapterFactory())
        }
    }

    class YCHeaderInterceptor: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val resultRequest = request.newBuilder().also {
                it.addHeader("x-app-id", APP_ID)
                it.addHeader("x-token", UserInfoManager.token ?: "")
                it.addHeader("x-version", APP_VERSION ?: "")
                it.addHeader("x-platform", APP_PLATFORM)
                it.addHeader("x-device-id", DEVICE_ID ?: "")
                it.addHeader("x-channel", CHANNEL)
                it.addHeader("x-mobile-brand", BRAND ?: "")
                it.addHeader("x-mobile-model", MODEL ?: "")
            }
            return chain.proceed(resultRequest.build())
        }

    }

    class ServerFailInterceptor: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val encodedPath = request.url().encodedPath()
            val response = chain.proceed(request)
            return if (response.body() != null && response.body()!!.contentType() != null) {
                val mediaType = response.body()!!.contentType()
                val string = response.body()!!.string()
                val responseBody = ResponseBody.create(mediaType, string)

                try {
                    val apiResponse = GsonUtils.fromJson(string, ApiResponse::class.java)
                    //判断逻辑 模拟一下
                    when (apiResponse.code) {
                        1004 -> {
                            if (!encodedPath.contains(ApiService.APP_INIT_URL)) {
                                EventBus.getDefault().post(LogoutEvent())
                            }
                        }
                        1001, 1002, 1003 -> {

                        }
                        else -> {
                            if (apiResponse.code != 0) {
                                if (!encodedPath.contains(ApiService.USER_EVENT_URL)) {
                                    UserBehaviorUtil.postToService(
                                        StatEvent.网络请求错误,
                                        value = encodedPath,
                                        extras = mapOf(Pair("code", apiResponse.code), Pair("message", apiResponse.message)),
                                        type = StatType.错误
                                    )
                                }

                                val message = apiResponse.message
                                if (message.contains(";")) {
                                    ZZToast.showError(message.substringAfterLast(";"))
                                } else {
                                    ZZToast.showError(message)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    ZZToast.showError("网络错误，请稍后重试")
                }

                response.newBuilder().body(responseBody).build()
            } else {
                response
            }
        }

    }

}