package com.yic3.lib.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlin.math.min

class MarqueeRecyclerView: RecyclerView {

    companion object {
        private const val MAX_DYNAMIC_SHOW = 1
        private const val WAIT_TIME = 2000L
    }


    var position = 0
    var waitTime = WAIT_TIME

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    init {
        layoutManager = SmoothScrollLayoutManager(context)
        // LinearSnapHelper().attachToRecyclerView(this)
    }

    fun setAdapter(adapter: MarqueeAdapter<*, *>, itemHeight: Int = height, needSnap: Boolean = true) {
        if (adapter.itemCount == 0) {
            return
        }
        this.adapter = adapter
        isVisible = true
        if (height != itemHeight || MAX_DYNAMIC_SHOW != 1) {
            layoutParams = layoutParams.also {
                it.height = min(MAX_DYNAMIC_SHOW, adapter.itemCount) * itemHeight
            }
        }
        if (needSnap) {
            LinearSnapHelper().attachToRecyclerView(this)
        }
        startLoop()
    }

    private val scrollRunnable = Runnable {
        position++
        smoothScrollToPosition(position)
        next()
    }

    private fun next() {
        removeCallbacks(scrollRunnable)
        postDelayed(scrollRunnable, waitTime * 2)
    }

    private fun startLoop() {
        removeCallbacks(scrollRunnable)
        postDelayed(scrollRunnable, waitTime)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        return false
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    inner class SmoothScrollLayoutManager(context: Context) : LinearLayoutManager(context) {
        override fun smoothScrollToPosition(
            recyclerView: RecyclerView,
            state: State,
            position: Int
        ) {
            // super.smoothScrollToPosition(recyclerView, state, position)
            val smoothScroller = object : LinearSmoothScroller(recyclerView.context) {
                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    // return resources.getDimension(R.dimen.dp_80) / displayMetrics.densityDpi
                    return waitTime.toFloat() / displayMetrics.densityDpi
                }
            }
            smoothScroller.targetPosition = position
            startSmoothScroll(smoothScroller)
        }
    }

}

abstract class MarqueeAdapter<T, VH : BaseViewHolder>(layoutId: Int): BaseQuickAdapter<T, VH>(layoutId) {

    override fun getDefItemCount(): Int {
        return Int.MAX_VALUE / 2
    }

    override fun getItemViewType(position: Int): Int {
        return super.getDefItemViewType(position)
    }

    override fun getItemOrNull(position: Int): T? {
        return super.getItemOrNull(position % data.size)
    }

    override fun getItem(position: Int): T {
        return super.getItem(position % data.size)
    }

}