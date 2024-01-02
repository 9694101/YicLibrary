package com.yic3.lib.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SpanUtils
import com.yic3.lib.R
import com.yic3.lib.databinding.DialogAppPricacyBinding
import com.yic3.lib.net.NetworkApi
import com.yic3.lib.ui.AppWebActivity
import com.yic3.lib.util.H5Url
import com.yic3.lib.util.ChannelUtil

class AppPrivacyDialog(context: Context): Dialog(context, R.style.Translucent_HALF) {

    private lateinit var binding: DialogAppPricacyBinding
    var privacyListener: OnPrivacyListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAppPricacyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setLayout(ScreenUtils.getAppScreenWidth(), WindowManager.LayoutParams.WRAP_CONTENT)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        binding.refuseTextView.setOnClickListener {
            dismiss()
            privacyListener?.onRefuse()
        }
        binding.agreeTextView.setOnClickListener {
            dismiss()
            privacyListener?.onAgree()
        }

        binding.refuseTextView.text = when (NetworkApi.CHANNEL) {
            ChannelUtil.CHANNEL_HUAWEI -> "不同意并退出APP"
            ChannelUtil.CHANNEL_XIAOMI -> "不同意"
            else -> "不同意并退出APP"
        }

        val clickColor = ColorUtils.getColor(R.color.theme_color)
        val appName = AppUtils.getAppName()
        binding.contentTextView.text = SpanUtils.with(binding.contentTextView)
            .append("欢迎使用")
            .append(appName)
            .append("！")
            .append(appName)
            .append("非常重视您的隐私和个人信息保护。在您使用")
            .append(appName)
            .append("前，请认真阅读")
            .append("《用户协议》").setClickSpan(clickColor, false) {
                AppWebActivity.openActivity(H5Url.用户协议, "用户协议")
            }
            .append("及")
            .append("《个人信息保护政策》").setClickSpan(clickColor, false) {
                AppWebActivity.openActivity(H5Url.隐私政策, "个人信息保护政策")
            }
            .append("，您同意并接受全部条款后方可开始使用")
            .append(appName)
            .append("。")
            .create()
    }

}

interface OnPrivacyListener {
    fun onRefuse()
    fun onAgree()
}