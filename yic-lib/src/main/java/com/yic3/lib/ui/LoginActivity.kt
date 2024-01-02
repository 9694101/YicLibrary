package com.yic3.lib.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.SpanUtils
import com.yic3.lib.R
import com.yic3.lib.base.BaseActivity
import com.yic3.lib.databinding.ActivityLoginBinding
import com.yic3.lib.event.EventWxLogin
import com.yic3.lib.event.UserInfoRefreshEvent
import com.yic3.lib.event.UserLoginChangedEvent
import com.yic3.lib.guide.SplashViewModel
import com.yic3.lib.ui.model.LoginViewModel
import com.yic3.lib.util.H5Url
import com.yic3.lib.util.UserInfoManager
import com.yic3.lib.util.WechatUtil
import com.yic3.lib.util.ZZToast
import com.yic3.lib.util.postEvent
import com.yic3.lib.util.registerEvent
import com.yic3.lib.util.unregisterEvent
import me.hgj.jetpackmvvm.state.ResultState
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class LoginActivity: BaseActivity<LoginViewModel, ActivityLoginBinding>(), View.OnClickListener {

    companion object {
        fun openActivity(defaultMode: ShowMode = ShowMode.微信) {
            val bundle = bundleOf(Pair("mode", defaultMode))
            ActivityUtils.startActivity(bundle, LoginActivity::class.java)
        }
    }

    override fun createObserver() {
        mViewModel.sendResult.observe(this) {
            if (it is ResultState.Success) {
                mDatabind.codeTextView.startCount()
            } else {
                mDatabind.codeTextView.isEnabled = true
            }
        }
        mViewModel.loginResult.observe(this) {
            if (it is ResultState.Success) {
                UserInfoManager.saveToken(it.data.token)
                UserInfoManager.saveUserInfo(it.data.bossInfo)
                UserInfoRefreshEvent(false).postEvent()
                UserLoginChangedEvent().postEvent()
                SplashViewModel.saveHasGuide()
                finish()
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        registerEvent()
        mDatabind.codeTextView.setOnClickListener(this)
        mDatabind.loginTextView.setOnClickListener(this)
        mDatabind.wechatImageView.setOnClickListener(this)
        mDatabind.phoneImageView.setOnClickListener(this)
        mDatabind.loginWechatLayout.setOnClickListener(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getSerializableExtra("mode", ShowMode::class.java)
        } else {
            intent?.getSerializableExtra("mode") as ShowMode?
        }?.let {
            showMode(it)
        }

        mDatabind.agreementTextView.highlightColor = Color.TRANSPARENT
        val clickColor = ColorUtils.getColor(R.color.theme_color)
        mDatabind.agreementTextView.text = SpanUtils.with(mDatabind.agreementTextView)
            .append("《用户协议》")
            .setBold()
            .setClickSpan(clickColor, false) {
                ActivityUtils.startActivity(AppWebActivity.getBundle(H5Url.用户协议, "用户协议"), AppWebActivity::class.java)
            }
            .append("和")
            .append("《隐私协议》")
            .setBold()
            .setClickSpan(clickColor, false) {
                ActivityUtils.startActivity(AppWebActivity.getBundle(H5Url.隐私政策, "隐私条款"), AppWebActivity::class.java)
            }
            .create()

        mDatabind.wechatLoginAgreement.highlightColor = Color.TRANSPARENT
        mDatabind.wechatLoginAgreement.text = SpanUtils.with(mDatabind.wechatLoginAgreement)
            .append("《用户协议》")
            .setBold()
            .setClickSpan(clickColor, false) {
                ActivityUtils.startActivity(AppWebActivity.getBundle(H5Url.用户协议, "用户协议"), AppWebActivity::class.java)
            }
            .append("和")
            .append("《隐私协议》")
            .setBold()
            .setClickSpan(clickColor, false) {
                ActivityUtils.startActivity(AppWebActivity.getBundle(H5Url.隐私政策, "隐私条款"), AppWebActivity::class.java)
            }
            .create()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterEvent()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.code_textView -> {
                val phone = mDatabind.phoneEditText.text.toString()
                if (RegexUtils.isMobileSimple(phone)) {
                    mDatabind.codeTextView.isEnabled = false
                    mViewModel.sendSms(phone)
                } else {
                    ZZToast.showInfo("请输入正确的手机号")
                }
            }
            R.id.login_textView -> {
                val phone = mDatabind.phoneEditText.text.toString()
                if (!RegexUtils.isMobileSimple(phone)) {
                    ZZToast.showInfo("请输入正确的手机号")
                    return
                }
                val code = mDatabind.codeEditText.text.toString()
                if (code.trim().isEmpty()) {
                    ZZToast.showInfo("请输入验证码")
                    return
                }
                if (!mDatabind.loginAgreementCheckBox.isChecked) {
                    ZZToast.showInfo("请先阅读并同意《用户协议》和《隐私条款》")
                    return
                }
                mViewModel.loginWithPhone(phone, code)
            }
            R.id.wechat_imageView -> {
                showMode(ShowMode.微信)
            }
            R.id.phone_imageView -> {
                showMode(ShowMode.手机号)
            }
            R.id.login_wechat_layout -> {
                if (!mDatabind.wechatLoginAgreementCheckBox.isChecked) {
                    ZZToast.showInfo("请先阅读并同意《用户协议》和《隐私条款》")
                    return
                }
                WechatUtil.login(this)
            }
        }
    }

    private fun showMode(mode: ShowMode) {
        when (mode) {
            ShowMode.手机号 -> {
                mDatabind.wechatLoginLayout.isGone = true
                mDatabind.phoneLoginLayout.isVisible = true
            }
            ShowMode.微信 -> {
                mDatabind.wechatLoginLayout.isVisible = true
                mDatabind.phoneLoginLayout.isGone = true
            }
            ShowMode.一键登录 -> {

            }
        }
    }

    @Subscribe
    fun onWechatLogin(event: EventWxLogin) {
        mViewModel.loginWithWechat(event.code)
    }
}

enum class ShowMode {
    手机号, 微信, 一键登录
}