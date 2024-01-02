package com.yic3.lib.ui

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.SpanUtils
import com.yic3.lib.R
import com.yic3.lib.databinding.DialogLoginBinding
import com.yic3.lib.event.EventWxLogin
import com.yic3.lib.event.UserInfoRefreshEvent
import com.yic3.lib.ui.model.LoginViewModel
import com.yic3.lib.util.H5Url
import com.yic3.lib.util.UserInfoManager
import com.yic3.lib.util.WechatUtil
import com.yic3.lib.util.ZZToast
import me.hgj.jetpackmvvm.state.ResultState
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class LoginDialog: DialogFragment(), View.OnClickListener {

    companion object {
        fun show(fragmentManager: FragmentManager) {
            LoginDialog().show(fragmentManager, "login")
        }
    }

    lateinit var binding: DialogLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private var loginType: LoginType = LoginType.微信
    set(value) {
        field = value
        if (::binding.isInitialized) {
            when (value) {
                LoginType.微信 -> {
                    binding.phoneInfoLayout.isGone = true
                    binding.wechatImageView.isGone = true
                    binding.titleTextView.text = "微信登录"

                    binding.wechatInfoLayout.isVisible = true
                    binding.usePhoneLogin.isVisible = true
                }
                LoginType.手机 -> {
                    binding.phoneInfoLayout.isVisible = true
                    binding.wechatImageView.isVisible = true
                    binding.titleTextView.text = "手机登录"

                    binding.wechatInfoLayout.isGone = true
                    binding.usePhoneLogin.isGone = true
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        loginViewModel = LoginViewModel()
        createObserve()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun createObserve() {
        loginViewModel.sendResult.observe(this) {
            if (it is ResultState.Success) {
                binding.codeTextView.startCount()
            } else {
                binding.codeTextView.isEnabled = true
            }
        }
        loginViewModel.loginResult.observe(this) {
            if (it is ResultState.Success) {
                UserInfoManager.saveToken(it.data.token)
                EventBus.getDefault().post(UserInfoRefreshEvent())
                dismiss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.Translucent_HALF)
//        dialog.setCancelable(false)
//        dialog.setCanceledOnTouchOutside(false)

        dialog.let {
            binding = DialogLoginBinding.inflate(layoutInflater)
            it.setContentView(binding.root)
            it.window?.attributes = it.window?.attributes?.apply {
                width = WindowManager.LayoutParams.MATCH_PARENT
            }
        }

        initView()

        return dialog
    }

    private fun initView() {
        binding.codeTextView.setOnClickListener(this)
        binding.loginTextView.setOnClickListener(this)
        binding.wechatImageView.setOnClickListener(this)
        binding.usePhoneLogin.setOnClickListener(this)

        binding.agreementTextView.highlightColor = Color.TRANSPARENT
        val clickColor = Color.parseColor("#343649")
        binding.agreementTextView.text = SpanUtils.with(binding.agreementTextView)
            .append("《用户协议》")
            .setClickSpan(clickColor, false) {
                ActivityUtils.startActivity(AppWebActivity.getBundle(H5Url.用户协议, "用户协议"), AppWebActivity::class.java)
            }
            .append("&").setForegroundColor(clickColor)
            .append("《隐私条款》")
            .setClickSpan(clickColor, false) {
                ActivityUtils.startActivity(AppWebActivity.getBundle(H5Url.隐私政策, "隐私条款"), AppWebActivity::class.java)
            }
            .create()
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.use_phone_login) {
            loginType = LoginType.手机
            return
        }
        if (v?.id == R.id.wechat_imageView) {
            loginType = LoginType.微信
            return
        }
        if (!binding.loginAgreementCheckBox.isChecked) {
            ZZToast.showInfo("请先阅读并同意《用户协议》和《隐私条款》")
            return
        }
        when (v?.id) {
            R.id.code_textView -> {
                val phone = binding.phoneEditText.text.toString()
                if (RegexUtils.isMobileSimple(phone)) {
                    binding.codeTextView.isEnabled = false
                    loginViewModel.sendSms(phone)
                } else {
                    ZZToast.showInfo("请输入正确的手机号")
                }
            }
            R.id.login_textView -> {
                when (loginType) {
                    LoginType.手机 -> {
                        val phone = binding.phoneEditText.text.toString()
                        if (!RegexUtils.isMobileSimple(phone)) {
                            ZZToast.showInfo("请输入正确的手机号")
                            return
                        }
                        val code = binding.codeEditText.text.toString()
                        if (code.trim().isEmpty()) {
                            ZZToast.showInfo("请输入验证码")
                            return
                        }
                        loginViewModel.loginWithPhone(phone, code)
                    }
                    LoginType.微信 -> {
                        WechatUtil.login(requireContext())
                    }
                }
            }
        }
    }

    @Subscribe
    fun onWechatLogin(event: EventWxLogin) {
        loginViewModel.loginWithWechat(event.code)
    }

    enum class LoginType {
        手机, 微信
    }

}