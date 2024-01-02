package com.yic3.lib.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.SizeUtils
import com.yic3.lib.R
import me.hgj.jetpackmvvm.ext.view.afterTextChange

class EditTextWithDel @JvmOverloads constructor(
    mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(
    mContext, attrs, defStyleAttr
) {

    private var icDelete: Drawable? = null
    private var left: Drawable? = null

    init {
        init()
    }

    private fun init() {
        //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片,getCompoundDrawables()获取Drawable的四个位置的数组
        icDelete = compoundDrawables[2]
        left = compoundDrawables[0] ?: compoundDrawablesRelative[0]
        if (icDelete == null) {
            icDelete = ResourceUtils.getDrawable(R.mipmap.icon_edit_clear)
        }
        //设置图标的位置以及大小,getIntrinsicWidth()获取显示出来的大小而不是原图片的带小
        icDelete?.let {
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
        }
        afterTextChange {
            setImage()
        }
        setImage()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (icDelete != null && event.action == MotionEvent.ACTION_UP) {
            //得到手指离开EditText时的X Y坐标
            val x = event.rawX.toInt()
            //创建一个长方形
            val rect = Rect()
            //让长方形的宽等于edittext的宽，让长方形的高等于edittext的高
            getGlobalVisibleRect(rect)
            //把长方形缩短至右边30dp，约等于（padding+图标分辨率）
            rect.left = rect.right - SizeUtils.dp2px(30f) - paddingRight
            //如果x和y坐标在长方形当中，说明你点击了右边的xx图片,清空输入框
            if (x > rect.left && x < rect.right) {
                setText("")
            }
        }
        return super.onTouchEvent(event)
    }

    private fun setImage() {
        if (length() > 0) {
            setCompoundDrawablesWithIntrinsicBounds(left, null, icDelete, null)
        } else {
            setCompoundDrawablesWithIntrinsicBounds(left, null, null, null)
        }
    }

}