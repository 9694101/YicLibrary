package com.yic3.lib.util

import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.PermissionUtils

object ZZPermissionUtil {

    fun requestPermission(permission: Permission, title: String = permission.title,
                          content: String = permission.content, callback: PermissionUtils.SimpleCallback
    ) {
        if (!PermissionUtils.isGranted(*permission.permission)) {
            val topActivity = ActivityUtils.getTopActivity()
            PermissionTipsUtil.showWithSnack(topActivity.window.decorView, topActivity.layoutInflater, title, content)
        }
        PermissionUtils.permission(*permission.permission).callback(object : PermissionUtils.SimpleCallback {
            override fun onGranted() {
                PermissionTipsUtil.dismiss()
                callback.onGranted()
            }

            override fun onDenied() {
                PermissionTipsUtil.dismiss()
                callback.onDenied()
            }
        }).request()
    }

}

enum class Permission(vararg val permission: String, val title: String, val content: String) {

    存储权限(PermissionConstants.STORAGE, title = "存储空间/照片权限说明", content = "用于在上传、修改头像场景中读取和写入相册"),
    摄像头权限(PermissionConstants.CAMERA, title = "相机权限使用说明", content = "用于上传头像时拍摄照片的场景"),
    麦克风权限(PermissionConstants.MICROPHONE, title = "麦克风权限使用说明", content = "用于录制语音、语音输入、拍摄视频的场景"),
    摄像头和麦克风权限(PermissionConstants.MICROPHONE, PermissionConstants.CAMERA, title = "相机/麦克风权限使用说明", content = "用于视频录制的场景"),
    定位权限(PermissionConstants.LOCATION, title = "定位权限使用说明", content = "用于定位当前位置，计算与目标地的距离")

}