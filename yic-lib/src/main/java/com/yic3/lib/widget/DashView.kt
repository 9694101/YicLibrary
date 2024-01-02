package com.yic3.lib.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.SizeUtils
import com.yic3.lib.R

class DashView: View {

    var dashWidth: Float = SizeUtils.dp2px(10f).toFloat()
    var dashGap: Float = SizeUtils.dp2px(10f).toFloat()
    var lineColor: Int = ColorUtils.getColor(R.color.black63)
    var dashOrientation: Int = 1
    var dashPaint = Paint()

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DashView)
        dashWidth = typedArray.getDimension(R.styleable.DashView_dashWidth, dashWidth)
        dashGap = typedArray.getDimension(R.styleable.DashView_dashGap, dashGap)
        lineColor = typedArray.getColor(R.styleable.DashView_lineColor, lineColor)
        dashOrientation = typedArray.getInt(R.styleable.DashView_dashOrientation, dashOrientation)
        dashPaint.color = lineColor
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (width == 0 || height == 0 || canvas == null) {
            return
        }
        when (dashOrientation) {
            1 -> {
                dashPaint.strokeWidth = height.toFloat()
                drawHorizontal(canvas)
            }
            2 -> {
                dashPaint.strokeWidth = width.toFloat()
                drawVertical(canvas)
            }
        }
    }

    private fun drawHorizontal(canvas: Canvas) {
        var totalWidth = 0f
        canvas.save()
        val pts = floatArrayOf(0f, 0f, dashWidth, 0f)
        canvas.translate(0f, height / 2f)
        while (totalWidth <= width) {
            canvas.drawLines(pts, dashPaint)
            canvas.translate(dashWidth + dashGap, 0f)
            totalWidth += dashWidth + dashGap
        }
        canvas.restore()
    }

    private fun drawVertical(canvas: Canvas) {
        var totalWidth = 0f
        canvas.save()
        val pts = floatArrayOf(0f, 0f, 0f, dashWidth)
        canvas.translate(0f, width / 2f)
        while (totalWidth <= height) {
            canvas.drawLines(pts, dashPaint)
            canvas.translate(0f, dashWidth + dashGap)
            totalWidth += dashWidth + dashGap
        }
        canvas.restore()
    }

}