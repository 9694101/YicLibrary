package com.yic3.lib.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.utilcode.util.ColorUtils
import com.yic3.lib.R


/**
 * 自定义对话框，兼容高低版本显示差异问题
 * Created by Administrator on 2015/11/6.
 */
class ZZDialog : Dialog, View.OnClickListener {

    private lateinit var contentView: LinearLayout
    private lateinit var divider: View // 分隔线，底部2个按钮之间
    private var message: TextView? = null // 显示的消息内容
    private var positive: TextView? = null // 确定
    private var negative: TextView? = null // 取消
    private var title: TextView? = null // 标题
    private var msg: CharSequence? = null // 消息文字
    private var pos: String? = null // 确定文字
    private var neg: String? = null // 取消文字
    private var textTitle: String? = null // 标题文字
    private var mColor = Color.BLACK //Color.rgb(0x33, 0x33, 0x33) // 消息字体颜色
    private var pColor = Color.WHITE // 确定字体颜色
    private var nColor = ColorUtils.getColor(R.color.theme_color) // 取消字体颜色

    private var onPositive: OnClickListener? = null // 点击确定的回调
    private var onNegative: OnClickListener? = null // 点击取消的回调

    private var isCancel = true // 是否可点击外部取消
    private var alwaysCenter = true

    constructor(context: Context) : super(context, R.style.Translucent_HALF)

    constructor(context: Context, theme: Int) : super(context, theme)

    override fun onClick(view: View) {
        dismiss()
        val id = view.id
        if (id == R.id.message_positive) {
            if (onPositive != null) {
                onPositive!!.onClick(view)
            }
        } else if (id == R.id.message_negative) {
            if (onNegative != null) {
                onNegative!!.onClick(view)
            }
        }
    }

    fun clickPositive() {
        positive?.performClick()
    }

    interface OnClickListener {
        fun onClick(view: View)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_message)
        contentView = findViewById(R.id.content_linearLayout)
        divider = findViewById(R.id.message_divider)
        message = findViewById(R.id.message_message)
        message!!.gravity = if (alwaysCenter) Gravity.CENTER else Gravity.CENTER_VERTICAL
        positive = findViewById(R.id.message_positive)
        negative = findViewById(R.id.message_negative)
        title = findViewById(R.id.message_title)
        positive!!.setOnClickListener(this)
        negative!!.setOnClickListener(this)
        window?.let {
            it.attributes = it.attributes.also { layoutParams ->
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            }
        }
        setView()
    }

    /**
     * 设置文字，颜色，显示
     */
    private fun setView() {
        if (msg.isNullOrEmpty()) {
            message!!.visibility = View.GONE
        } else {
            message!!.visibility = View.VISIBLE
            message!!.text = msg
            message!!.setTextColor(mColor)
        }
        if (textTitle.isNullOrEmpty()) {
            title!!.visibility = View.GONE
        } else {
            title!!.visibility = View.VISIBLE
            title!!.text = textTitle
            title!!.setTextColor(mColor)
        }

        var emptyCount = 0

        if (pos.isNullOrEmpty()) {
            positive!!.visibility = View.GONE
            emptyCount++
        } else {
            positive!!.visibility = View.VISIBLE
            positive!!.text = pos
            positive!!.setTextColor(pColor)
        }
        if (neg.isNullOrEmpty()) {
            negative!!.visibility = View.GONE
            emptyCount++
        } else {
            negative!!.visibility = View.VISIBLE
            negative!!.text = neg
            negative!!.setTextColor(nColor)
        }
        if (emptyCount > 0) {
            divider.visibility = View.GONE
        } else {
            // divider.setVisibility(View.VISIBLE);
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (isCancel) {
            super.onBackPressed()
        }
    }

    fun setMessage(msg: CharSequence?, mColor: Int): ZZDialog {
        this.msg = msg
        if (msg.isNullOrEmpty()) {
            message?.visibility = View.GONE
            return this
        }
        this.mColor = mColor
        if (message != null) {
            message!!.visibility = View.VISIBLE
            message!!.text = msg
            message!!.setTextColor(mColor)
        }
        return this
    }

    fun setTitleText(textTitle: String?): ZZDialog {
        this.textTitle = textTitle
        if (textTitle.isNullOrEmpty()) {
            title?.visibility = View.GONE
            return this
        }
        title?.let {
            it.visibility = View.VISIBLE
            it.text = textTitle
            it.setTextColor(mColor)
        }
        return this
    }

    fun setMessage(msg: String?): ZZDialog {
        this.msg = msg
        if (msg.isNullOrEmpty()) {
            message?.visibility = View.GONE
            return this
        }
        message?.let {
            it.visibility = View.VISIBLE
            it.text = msg
            it.setTextColor(mColor)
        }
        return this
    }

    fun setMessage(msg: CharSequence?, alwaysCenter: Boolean): ZZDialog {
        this.alwaysCenter = alwaysCenter
        this.msg = msg
        if (msg.isNullOrEmpty()) {
            message?.visibility = View.GONE
            return this
        }
        message?.let {
            it.visibility = View.VISIBLE
            it.text = msg
            it.setTextColor(mColor)
            it.gravity = if (alwaysCenter) Gravity.CENTER else Gravity.CENTER_VERTICAL
        }
        return this
    }

    fun setPositive(pos: String?, pColor: Int, onPositive: OnClickListener): ZZDialog {
        this.pos = pos
        if (pos.isNullOrEmpty()) {
            positive?.visibility = View.GONE
            return this
        }
        this.pColor = pColor
        this.onPositive = onPositive
        positive?.let {
            it.visibility = View.VISIBLE
            it.text = pos
            it.setTextColor(pColor)
        }
        return this
    }

    fun setPositive(pos: String?, onPositive: OnClickListener?): ZZDialog {
        this.pos = pos
        if (pos.isNullOrEmpty()) {
            positive?.visibility = View.GONE
            return this
        }
        this.onPositive = onPositive
        positive?.visibility = View.VISIBLE
        positive?.text = pos
        positive?.setTextColor(pColor)
        return this
    }

    fun setNegative(neg: String?, nColor: Int, onNegative: OnClickListener?): ZZDialog {
        this.neg = neg
        if (neg.isNullOrEmpty()) {
            negative?.visibility = View.GONE
            return this
        }
        this.nColor = nColor
        this.onNegative = onNegative
        negative?.let {
            it.visibility = View.VISIBLE
            it.text = neg
            it.setTextColor(nColor)
        }
        return this
    }

    fun setNegative(neg: String?, onNegative: OnClickListener?): ZZDialog {
        this.neg = neg
        if (neg.isNullOrEmpty()) {
            negative?.visibility = View.GONE
            return this
        }
        this.onNegative = onNegative
        negative?.let {
            it.visibility = View.VISIBLE
            it.text = neg
            it.setTextColor(nColor)
        }
        return this
    }

    class Builder(internal var context: Context) {

        private var zzDialog: ZZDialog = ZZDialog(this.context)

        fun build(): ZZDialog {
            return zzDialog
        }

        fun setTitle(title: String): Builder {
            zzDialog.setTitle(title)
            return this
        }

        fun setMessage(msg: String): Builder {
            zzDialog.setMessage(msg)
            return this
        }

        fun setPositive(pos: String, onPositive: OnClickListener): Builder {
            zzDialog.setPositive(pos, onPositive)
            return this
        }

        fun setNegative(neg: String, onNegative: OnClickListener): Builder {
            zzDialog.setNegative(neg, onNegative)
            return this
        }

    }

}
