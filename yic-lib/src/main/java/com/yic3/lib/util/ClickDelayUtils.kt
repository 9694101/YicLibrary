package com.yic3.lib.util

import android.view.View

/**
 * @author dhy
 * @date 2021/7/20
 * @des 短时间内不能连续点击
 */
object ClickDelayUtils {
    /**
     * hash值
     */
    private var hash: Int = 0
    /**
     * 最后点击时间
     */
    private var lastClickTime: Long = 0
    /**
     *防止多次点击
     */
    fun View.doClickDelay(time : Long = 500, clickAction: (View) -> Unit) {
        this.setOnClickListener {
            if (this.hashCode() != hash) {
                hash = this.hashCode()
                lastClickTime = System.currentTimeMillis()
                clickAction(it)
            } else {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime > time) {
                    lastClickTime = System.currentTimeMillis()
                    clickAction(it)
                }
            }
        }
    }
}