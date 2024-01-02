package com.yic3.lib.util

import android.view.LayoutInflater
import com.chad.library.adapter.base.BaseQuickAdapter
import com.yic3.lib.R
import com.yic3.lib.databinding.LayoutEmptyBinding

fun BaseQuickAdapter<*, *>.setEmptyView(defaultImg: Int = R.mipmap.icon_default_empty, defaultText: String = "暂无内容") {
    val binding = LayoutEmptyBinding.inflate(LayoutInflater.from(context))
    binding.ivEmptyHint.setImageResource(defaultImg)
    binding.tvEmptyHint.text = defaultText
    setEmptyView(binding.root)
}