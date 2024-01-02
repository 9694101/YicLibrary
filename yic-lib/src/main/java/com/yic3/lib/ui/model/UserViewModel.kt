package com.yic3.lib.ui.model

import androidx.lifecycle.MutableLiveData
import com.yic3.lib.entity.UserInfoEntity
import com.yic3.lib.net.NetworkApi
import com.yic3.lib.util.UserInfoManager
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

open class UserViewModel: BaseViewModel() {

    val userInfoResult = MutableLiveData<UserInfoEntity>()

    fun getUserInfo() {
        request({
            NetworkApi.service.getUserInfo()
        }, {
            UserInfoManager.saveUserInfo(it)
            userInfoResult.postValue(it)
        })
    }

    val updateInfoResult = MutableLiveData<ResultState<UserInfoEntity?>>()

    fun updateUserInfo(userInfo: Map<String, Any>, isShowLoading: Boolean = false) {
        request({
            NetworkApi.service.userEdit(userInfo)
        }, updateInfoResult, isShowDialog = isShowLoading)
    }

    private val _uploadResult = MutableLiveData<String>()

    fun uploadAvatar(filePath: String) {
        val file = File(filePath)
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val requestBody = MultipartBody.Part.createFormData("file", file.name, requestFile)
        request({
            NetworkApi.service.fileUpload(requestBody)
        }, {
            _uploadResult.postValue(it)
        }, {
            updateInfoResult.postValue(ResultState.onAppError(it))
        }, isShowDialog = true)
    }

    init {
        _uploadResult.observeForever {
            val param = mutableMapOf<String, Any>()
            param["avatar"] = it
            updateUserInfo(param)
        }
    }

}