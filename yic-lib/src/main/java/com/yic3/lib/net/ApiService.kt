package com.yic3.lib.net

import com.yic3.lib.entity.*
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @GET(APP_INIT_URL)
    suspend fun appConfig(@Query("oaid") oaId: String,
                          @Query("imei") imei: String,
                          @Query("ua") userAgent: String = NetworkApi.USER_AGENT
    )
    : ApiResponse<AppInitConfig>

    @PUT("/api/boss")
    suspend fun userEdit(@Body requestMap: Map<String, @JvmSuppressWildcards Any>): ApiResponse<UserInfoEntity?>

    @GET("/api/boss")
    suspend fun getUserInfo(): ApiResponse<UserInfoEntity>

    @POST("/api/boss/logout")
    suspend fun logout(): ApiResponse<LogoutEntity>

    @GET("/api/goods")
    suspend fun getVipRechargeList(): ApiResponse<List<RechargePriceEntity>>

    @POST("/api/order")
    suspend fun getVipOrder(@Body requestMap: Map<String, @JvmSuppressWildcards Any>): ApiResponse<OrderPayEntity>

    @GET("/api/order/status")
    suspend fun searchOrder(@Query("orderId") orderId: Int): ApiResponse<OrderStatusEntity>

    @FormUrlEncoded
    @POST("/api/sms/send")
    suspend fun sendSms(@Field("phone") phone: String): ApiResponse<Any>

    @POST(USER_LOGIN)
    suspend fun login(@Body requestMap: Map<String, @JvmSuppressWildcards Any>): ApiResponse<LogoutEntity>

    @Multipart
    @POST("/api/upload")
    suspend fun fileUpload(@Part file: MultipartBody.Part): ApiResponse<String>

    companion object {
        const val APP_INIT_URL: String = "/api/launch"
        const val USER_EVENT_URL: String = "/api/boss/event"
        const val USER_LOGIN: String = "/api/boss/login"
    }

    @POST(USER_EVENT_URL)
    suspend fun userBehavior(@Body requestMap: Map<String, @JvmSuppressWildcards Any>): ApiResponse<Any>

    @GET("/api/version/check")
    suspend fun checkAppUpdate(): ApiResponse<AppVersionEntity>

}