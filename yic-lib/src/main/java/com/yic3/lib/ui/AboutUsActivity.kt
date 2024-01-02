package com.yic3.lib.ui

import android.annotation.SuppressLint
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.yic3.lib.base.BaseActivity
import com.yic3.lib.databinding.ActivityAboutUsBinding
import com.yic3.lib.util.H5Url
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class AboutUsActivity: BaseActivity<BaseViewModel, ActivityAboutUsBinding>() {
    override fun createObserver() {

    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.titleLayout.titleTextView.text = "关于我们"
        mDatabind.versionTextView.text = "v${AppUtils.getAppVersionName()}"
        mDatabind.userTextView.setOnClickListener {
            AppWebActivity.openActivity(H5Url.用户协议, "用户协议")
        }
        mDatabind.privacyTextView.setOnClickListener {
            AppWebActivity.openActivity(H5Url.隐私政策, "隐私政策")
        }
        mDatabind.logoImageView.setOnLongClickListener {
            ActivityUtils.startActivity(TestActivity::class.java)
            true
        }
    }

}