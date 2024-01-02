package com.yic3.lib.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.yic3.lib.R


/**
 * ZZCountDownButton
 * 倒计时按钮，UI的用法同普通Button
 * 开始时调用startCount
 * 停止stop
 * 重置reset
 * Created by zb on 2016/8/2.
 */
class ZZCountDownButton : AppCompatTextView {

    private var hintText: String? = null // 初始提示文字
    internal var countText: String? = null // 倒计时文字
    internal var count: Int = 0 // 倒计时秒数
    private var max_count = 60
    internal var currentStatus: CountStatus? = null // 当前计时状态

    /*倒计时刷新handler*/
    @SuppressLint("HandlerLeak")
    internal var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (currentStatus) {
                CountStatus.START -> {
                    count--
                    countText = "(" + count + "s)重新获取"
                    text = countText
                    if (count <= 0) {
                        resetCount()
                    }
                    this.sendEmptyMessageDelayed(1, 1000)
                }
                CountStatus.STOP -> {}
                CountStatus.RESET -> {}
                else -> {}
            }
        }
    }

    enum class CountStatus {
        START, STOP, RESET
    }

    /**
     * 继承父类的构造方法
     *
     * @param context context
     */
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    /**
     * init
     * 从xml中获取倒计时时间参数
     *
     * @param context c
     * @param attrs   xml
     * @author zb
     */
    @SuppressLint("CustomViewStyleable")
    private fun init(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.countSecond)
        max_count = a.getInt(R.styleable.countSecond_second, max_count)
        a.recycle()
    }

    /**
     * setCount
     * 代码设置时间
     *
     * @param second 秒
     * @author zb
     */
    fun setMaxCount(second: Int) {
        this.max_count = second
    }


    /**
     * startCount
     * 开始倒计时
     *
     * @author zb
     */
    fun startCount() {
        if (currentStatus == CountStatus.START) {
            return
        }
        if (hintText.isNullOrEmpty()) {
            hintText = text.toString()
        }
        currentStatus = CountStatus.START
        count = max_count
        isEnabled = false
        handler.sendEmptyMessageDelayed(1, 1000)
    }

    /**
     * stopCount
     * 停止倒计时
     *
     * @author zb
     */
    fun stopCount() {
        currentStatus = CountStatus.STOP
        count = max_count
        text = "重新获取"
        isEnabled = true
        handler.removeMessages(1)
    }

    /**
     * resetCount
     * 恢复到最开始状态
     *
     * @author zb
     */
    fun resetCount() {
        currentStatus = CountStatus.RESET
        count = max_count
        text = hintText
        isEnabled = true
        handler.removeMessages(1)
    }


}
