package com.yic3.lib.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.yic3.lib.R

class RatioFrameLayout: FrameLayout {

    var ratio: Float = 1f
    set(value) {
        field = value
        requestLayout()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttr(attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttr(attrs)
    }

    private fun initAttr(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioLinearLayout)
        ratio = typedArray.getFloat(R.styleable.RatioLinearLayout_width_height_ratio, ratio)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = (width / ratio).toInt()
        val heightMeasureSpecNew = MeasureSpec.makeMeasureSpec(
            height,
            MeasureSpec.EXACTLY
        )
        super.onMeasure(widthMeasureSpec, heightMeasureSpecNew)
    }
}