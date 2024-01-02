package com.yic3.lib.guide

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.yic3.lib.R
import com.yic3.lib.base.BaseFragment
import com.yic3.lib.databinding.FragmentGuideChoiceBinding
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import org.greenrobot.eventbus.EventBus

abstract class GuideBaseFragment<VM : BaseViewModel, DB: ViewDataBinding>: BaseFragment<VM, DB>(), GuideDataListener {

    companion object {
        const val FRAGMENT_BURIAL_ID = "burialId"
    }

    private var burialId: String? = null

    override fun createObserver() {

    }

    override fun initView(savedInstanceState: Bundle?) {
        burialId = arguments?.getString(FRAGMENT_BURIAL_ID)

        mDatabind.root.findViewById<View?>(R.id.next_textView)?.let {
            it.setOnClickListener {
                EventBus.getDefault().post(NextEvent(getSelectData(), getBurialId()))
            }
        }

        val guideBinding = mDatabind
        if (guideBinding is FragmentGuideChoiceBinding) {
            guideBinding.choiceRecyclerView.setHasFixedSize(true)
        }
    }

    override fun getBurialId(): String {
        return burialId ?: ""
    }

}