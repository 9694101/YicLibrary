package com.yic3.lib.util

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.SnackbarUtils
import com.yic3.lib.databinding.LayoutPermissionTipsBinding

object PermissionTipsUtil {

    fun showWithSnack(target: View, inflater: LayoutInflater, title: String, content: String) {
        SnackbarUtils.with(target)
            .setBgColor(Color.TRANSPARENT)
            .setDuration(SnackbarUtils.LENGTH_INDEFINITE)
            .show(true)
        SnackbarUtils.addView(LayoutPermissionTipsBinding.inflate(inflater).also {
            it.titleTextView.text = title
            it.contentTextView.text = content
        }.root.also {
            it.rotation = 180f
        }, ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    fun dismiss() {
        SnackbarUtils.dismiss()
    }

}