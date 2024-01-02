package com.yic3.lib.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class YicFragmentAdapter : FragmentPagerAdapter {

    private val mFragmentList = mutableListOf<Fragment>()

    constructor(fragmentManager: FragmentManager, mFragmentList: List<Fragment>? = null) : super(
        fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
        mFragmentList?.let {
            this.mFragmentList.addAll(it)
        }
    }

    fun removeAfter(index: Int) {
        mFragmentList.removeAll(mFragmentList.subList(index, mFragmentList.size))
        notifyDataSetChanged()
    }

//    override fun getItemPosition(`object`: Any): Int {
//        return if (tagList.contains(`object`.hashCode().toLong())) {
//            POSITION_NONE // POSITION_UNCHANGED
//        } else {
//            POSITION_NONE
//        }
//    }

    fun addFragment(fragmentList: List<Fragment>?) {
        if (fragmentList == null) {
            return
        }
        mFragmentList.addAll(fragmentList)
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).hashCode().toLong()
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

}