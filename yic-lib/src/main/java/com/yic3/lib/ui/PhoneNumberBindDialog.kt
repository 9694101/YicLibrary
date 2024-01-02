package com.yic3.lib.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.yic3.lib.R
import com.yic3.lib.databinding.DialogPhoneNumberBindBinding
import com.yic3.lib.ui.model.LoginViewModel
import com.yic3.lib.util.ZZToast

class PhoneNumberBindDialog: DialogFragment() {

    private lateinit var mDatabind: DialogPhoneNumberBindBinding
    private lateinit var mViewModel: PhoneBindViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = PhoneBindViewModel()
        createObserve()
    }

    private fun createObserve() {
        mViewModel.bindResult.observe(this) {

        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.Translucent_HALF)

        dialog.let {
            mDatabind = DialogPhoneNumberBindBinding.inflate(layoutInflater)
            it.setContentView(mDatabind.root)
            it.window?.attributes = it.window?.attributes?.apply {
                width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(60f)
            }
        }

        initView()

        return dialog
    }

    private fun initView() {
        mDatabind.codeTextView.setOnClickListener {
            val phone = mDatabind.phoneEditText.text.toString()
            if (RegexUtils.isMobileSimple(phone)) {
                mDatabind.codeTextView.isEnabled = false
                mViewModel.sendSms(phone)
            } else {
                ZZToast.showInfo("请输入正确的手机号")
            }
        }
        mDatabind.bindTextView.setOnClickListener {
            val phone = mDatabind.phoneEditText.text.toString()
            if (!RegexUtils.isMobileSimple(phone)) {
                ZZToast.showInfo("请输入正确的手机号")
                return@setOnClickListener
            }
            val code = mDatabind.codeEditText.text.toString()
            if (code.trim().isEmpty()) {
                ZZToast.showInfo("请输入验证码")
                return@setOnClickListener
            }
            mViewModel.bindPhoneNumber(phone, code)
        }
    }

}

class PhoneBindViewModel: LoginViewModel() {

    val bindResult = MutableLiveData<Boolean>()

    fun bindPhoneNumber(phone: String, code: String) {
        bindResult.postValue(true)
    }

}