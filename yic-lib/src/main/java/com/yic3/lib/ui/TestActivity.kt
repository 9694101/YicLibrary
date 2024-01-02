package com.yic3.lib.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.GsonBuilder
import com.yic3.lib.ui.model.TestViewModel
import com.yic3.lib.R
import com.yic3.lib.base.BaseActivity
import com.yic3.lib.databinding.ActivityTestBinding
import com.yic3.lib.entity.HostEntity
import com.yic3.lib.guide.SplashViewModel
import com.yic3.lib.net.HostManager
import com.yic3.lib.net.NetworkApi
import com.yic3.lib.util.UserInfoManager
import me.hgj.jetpackmvvm.state.ResultState

class TestActivity: BaseActivity<TestViewModel, ActivityTestBinding>(), OnItemClickListener {

    private val testAdapter = TestAdapter()

    override fun createObserver() {
        mViewModel.hostResult.observe(this) {
            testAdapter.selectIndex = it?.indexOfFirst { host ->
                host.default
            } ?: -1
            testAdapter.setNewInstance(it?.toMutableList())
        }
        mViewModel.initConfigResult.observe(this) {
            mDatabind.testTextView.text =  GsonBuilder().setPrettyPrinting().create().toJson(it)
            if (it is ResultState.Success) {
                UserInfoManager.saveInitConfig(it.data)
                SplashViewModel.clearGuide()
                if (UserInfoManager.isOpenGuide()) {
                    ActivityUtils.startActivity(packageName, "$packageName.guide.GuideFirstActivity")
                } else {
                    ActivityUtils.startLauncherActivity()
                }
                ActivityUtils.finishAllActivitiesExceptNewest()
                finish()
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.titleLayout.titleTextView.text = "测试"
        mDatabind.testRecyclerView.adapter = testAdapter
        mViewModel.getHostList()
        mDatabind.titleLayout.functionTextView.setOnClickListener {
            testAdapter.getItemOrNull(testAdapter.selectIndex)?.let {
                UserInfoManager.saveUserInfo(null)
                UserInfoManager.clearToken()
                HostManager.saveHost(it.address)
                NetworkApi.changeHost(it.address)
                mViewModel.getInitConfig(application)
            }
        }
        mDatabind.titleLayout.functionTextView.text = "确定"
        testAdapter.setOnItemClickListener(this)
        val toJson = GsonBuilder().setPrettyPrinting().create().toJson(UserInfoManager.initConfig)
        mDatabind.testTextView.text = toJson
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        testAdapter.selectIndex = position

    }
}

class TestAdapter: BaseQuickAdapter<HostEntity, BaseViewHolder>(R.layout.item_test_host) {

    var selectIndex = -1
    @SuppressLint("NotifyDataSetChanged")
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun convert(holder: BaseViewHolder, item: HostEntity) {
        holder.setText(R.id.name_textView, item.name)
        holder.setText(R.id.host_textView, item.address)
        holder.getView<View>(R.id.host_textView).isSelected = selectIndex == holder.absoluteAdapterPosition
    }

}