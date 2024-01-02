package com.yic3.lib.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View

class StatusBarView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (Companion.statusBarHeight == 0) {
            Companion.statusBarHeight = statusBarHeight
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Companion.statusBarHeight, MeasureSpec.AT_MOST))
    }

    private val statusBarHeight: Int
        get() {
            val resources = resources
            @SuppressLint("InternalInsetResource") val resourceId =
                resources.getIdentifier("status_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        }

    companion object {
        private var statusBarHeight = 0
    }
}