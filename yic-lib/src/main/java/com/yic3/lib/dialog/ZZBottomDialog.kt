package com.yic3.lib.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager

import com.yic3.lib.R

/**
 * 底部弹出对话框
 * 默认宽度为屏幕宽度
 * Created by Administrator on 2016/6/21.
 */
open class ZZBottomDialog : Dialog {

    private var width = 0 // 不设置时默认为屏宽

    /**
     * 设置屏幕宽度
     * @param width 设置对话框的window宽度
     */
    fun setWidth(width: Int) {
        this.width = width
    }

    protected fun setView(layoutId: Int) {
        this.setView(LayoutInflater.from(context).inflate(layoutId, null, false))
    }

    /**
     * 设置显示视图
     * @param view 显示视图，非空
     */
    protected fun setView(view: View) {
        setView(view, -1)
    }

    protected fun setView(view: View, height: Int) {
        window?.let {
            it.setGravity(Gravity.BOTTOM)//位于屏幕底部
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //android.util.AndroidRuntimeException: requestFeature() must be called before adding content
            it.requestFeature(Window.FEATURE_NO_TITLE)
            it.setWindowAnimations(R.style.Bottom_Dialog)
            it.setContentView(view)
            if (width != 0) {
                val lp = it.attributes
                lp.width = width //设置宽度（屏幕等宽）
                if (height > 0) {
                    lp.height = height
                }
                it.attributes = lp
            }
        }
    }

    constructor(context: Context) : super(context) {
        this.width = getWidth()
    }

    constructor(context: Context, themeResId: Int) : super(context, themeResId) {
        this.width = getWidth()
    }

    protected constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener
    ) : super(context, cancelable, cancelListener) {
        this.width = getWidth()
    }

    /**
     * 获取屏幕宽度
     * @return int
     */
    private fun getWidth(): Int {
        return WindowManager.LayoutParams.MATCH_PARENT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(true)//触摸屏幕取消窗体
        setCancelable(true)//按返回键取消窗体

    }
}
