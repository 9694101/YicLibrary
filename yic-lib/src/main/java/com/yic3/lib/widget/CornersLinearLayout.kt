package com.yic3.lib.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.LinearLayout
import com.yic3.lib.R

/**
 * 圆角LinearLayout
 * Created by zb on 2018/6/28.
 */

class CornersLinearLayout : LinearLayout {

    private var round = 0f //圆角半径像素值
    private var leftTop = 0f
    private var rightTop = 0f
    private var leftBottom = 0f
    private var rightBottom = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initStyle(context, attrs, 0)
    }

    @SuppressLint("Recycle")
    private fun initStyle(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.CornersLinearLayout, defStyleAttr, 0
        )
        round = a.getDimension(R.styleable.CornersLinearLayout_round_corner, round)
        leftTop = a.getDimension(R.styleable.CornersLinearLayout_left_top, round)
        rightTop = a.getDimension(R.styleable.CornersLinearLayout_right_top, round)
        leftBottom = a.getDimension(R.styleable.CornersLinearLayout_left_bottom, round)
        rightBottom = a.getDimension(R.styleable.CornersLinearLayout_right_bottom, round)

    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initStyle(context, attrs, defStyleAttr)
    }

    fun setRound(round: Float) {
        this.round = round
    }

    override fun dispatchDraw(canvas: Canvas) {
        val path = Path()
        val rectF = RectF(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            (width - paddingRight).toFloat(),
            (height - paddingBottom).toFloat()
        )
        //path.addRoundRect(rectF, round, round, Path.Direction.CW)
        val radii = floatArrayOf(leftTop, leftTop, rightTop, rightTop, leftBottom, leftBottom, rightBottom, rightBottom)
        path.addRoundRect(rectF, radii, Path.Direction.CW)

        canvas.drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // 先对canvas进行裁剪
        canvas.clipPath(path)

        super.dispatchDraw(canvas)
    }
}