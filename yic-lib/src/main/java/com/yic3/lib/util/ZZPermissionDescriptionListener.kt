package com.yic3.lib.util

import android.Manifest
import androidx.fragment.app.Fragment
import com.luck.picture.lib.interfaces.OnPermissionDescriptionListener

class ZZPermissionDescriptionListener: OnPermissionDescriptionListener {
    override fun onPermissionDescription(fragment: Fragment, permissionArray: Array<out String>) {
        var title = ""
        var content = ""
        when {
            permissionArray.contains(Manifest.permission.CAMERA) -> {
                title = Permission.摄像头权限.title
                content = Permission.摄像头权限.content
            }
            permissionArray.contains(Manifest.permission.READ_MEDIA_IMAGES)
            or permissionArray.contains(Manifest.permission.READ_EXTERNAL_STORAGE)-> {
                title = Permission.存储权限.title
                content = Permission.存储权限.content
            }
        }
        PermissionTipsUtil.showWithSnack(fragment.requireView(), fragment.layoutInflater, title, content)
    }

    override fun onDismiss(fragment: Fragment) {
        PermissionTipsUtil.dismiss()
    }
}