package com.yic3.lib.ui.model

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.yic3.lib.base.getFragment
import com.yic3.lib.entity.AppInitConfig
import com.yic3.lib.guide.GuideBaseFragment
import com.yic3.lib.util.GuideDataUtil
import com.yic3.lib.util.StatEvent
import com.yic3.lib.util.UserBehaviorUtil
import com.yic3.lib.util.UserInfoManager
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

abstract class GuideViewModel: BaseViewModel() {

    abstract fun getLaunchFragmentList(fragmentManager: FragmentManager): List<Fragment>

    open fun getLaunchFragmentListByConfig(fragmentManager: FragmentManager): List<Fragment> {
        val list = mutableListOf<Fragment>()
        val config = UserInfoManager.getInitConfig(AppInitConfig.GUIDE_FRAGMENT_DATA)
        if (config is Map<*, *> && config["value"] is List<*>) {
            (config["value"] as List<*>).forEach { name ->
                getFragmentClass(name.toString())?.let {
                    list.add(getFragment(fragmentManager, name.toString(), it))
                }
            }
        }
        return list
    }

    abstract fun getFragmentClass(name: String?): Class<out Fragment>?

    protected open fun <T : Fragment> getFragment(fragmentManager: FragmentManager, burialId: String, cls: Class<T>): Fragment {
        val fragment = fragmentManager.getFragment(cls)
        fragment.arguments = bundleOf(Pair(GuideBaseFragment.FRAGMENT_BURIAL_ID, burialId))
        return fragment
    }

    fun saveSelectData(data: Map<String, Any>, burialId: String) {
        GuideDataUtil.putAll(data)
        val map = mutableMapOf(Pair("data", data))
        UserBehaviorUtil.postToService(StatEvent.引导页点击.key, burialId, extras = map)
    }

}