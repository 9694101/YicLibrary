package com.yic3.lib.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class YicViewPager2FragmentAdapter: FragmentStateAdapter {

    private val mFragmentList = mutableListOf<Fragment>()
    private val tagList = mutableListOf<Long>()

    constructor(fragmentManager: FragmentManager, lifecycle: Lifecycle, mFragmentList: List<Fragment>? = null) : super(
        fragmentManager, lifecycle
    ) {
        mFragmentList?.let {
            this.mFragmentList.addAll(it)
        }
    }

    fun removeAfter(index: Int) {
        mFragmentList.removeAll(mFragmentList.subList(index, mFragmentList.size))
        tagList.clear()
        notifyDataSetChanged()
    }

    fun addFragment(fragmentList: List<Fragment>?) {
        if (fragmentList == null) {
            return
        }
        mFragmentList.addAll(fragmentList)
        tagList.clear()
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        val toLong = createFragment(position).hashCode().toLong()
        tagList.add(toLong)
        return toLong
    }

    override fun getItemCount(): Int {
        return mFragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun containsItem(itemId: Long): Boolean {
        return itemId in 0 until itemCount
    }

}