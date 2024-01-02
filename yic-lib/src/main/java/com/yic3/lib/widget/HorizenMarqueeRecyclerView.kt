package com.yic3.lib.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HorizonMarqueeRecyclerView: RecyclerView {

    var step: Int = 1
    var refreshTime: Long = 17

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        return false
    }

    private var marqueeJob: Job? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startMarqueeLoop()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopMarqueeLoop()
    }

    private fun startMarqueeLoop() {
        marqueeJob = GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                withContext(Dispatchers.IO) {
                    delay(refreshTime)
                }
                scrollBy(step, step)
            }
        }
    }

    private fun stopMarqueeLoop() {
        marqueeJob?.cancel()
    }

}