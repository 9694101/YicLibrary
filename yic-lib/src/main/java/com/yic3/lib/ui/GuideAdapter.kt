package com.yic3.lib.ui

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yic3.lib.guide.GuideDataListener

class GuideAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    private val mFragmentList = mutableListOf<Fragment>()

    override fun getItemCount(): Int {
        return mFragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addFragment(fragmentList: List<Fragment>?) {
        if (fragmentList == null) {
            return
        }
        mFragmentList.addAll(fragmentList)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return itemId in 0 until itemCount
    }

    fun getPageTitle(position: Int): CharSequence? {
        val item = getItem(position)
        return if (item is GuideDataListener) {
            item.getTitleCharSequence()
        } else item.javaClass.name
    }

}