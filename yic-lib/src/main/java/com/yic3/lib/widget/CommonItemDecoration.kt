package com.yic3.lib.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class CommonItemDecoration private constructor(
    color: Int,
    backgroundColor: Int,
    decorationView: DecorationView
) : ItemDecoration() {

    private val mRect = Rect(0, 0, 0, 0)
    private val mPaint = Paint()
    private val emptyPaint = Paint()
    private val decorationView: DecorationView

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        outRect[0, 0, 0] = decorationView.getDividerHeight(position)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        drawVertical(c, parent)
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount
        if (childCount == 0) {
            return
        }
        var child: View?
        var params: RecyclerView.LayoutParams
        val layoutManager = parent.layoutManager
        if (layoutManager is LinearLayoutManager) {
            c.save()
            for (i in 0 until childCount) {
                child = parent.getChildAt(i)
                if (child == null) {
                    continue
                }
                params = child.layoutParams as RecyclerView.LayoutParams
                val top = child.bottom + params.bottomMargin
                val position = parent.getChildAdapterPosition(child)
                val bottom = top + decorationView.getDividerHeight(position)
                mRect[left, top, right] = bottom
                c.drawRect(mRect, emptyPaint)
                if (decorationView.needDrawLine(position)) {
                    mRect[left + decorationView.getPaddingLeft(position),
                            top,
                            right - decorationView.getPaddingRight(i)
                    ] = bottom
                    val dividerColor = decorationView.getDividerColor(position)
                    if (dividerColor != -1) {
                        mPaint.color = dividerColor
                    }
                    c.drawRect(mRect, mPaint)
                }
            }
            c.restore()
        }
    }

    abstract class DecorationView {

        open fun getDividerHeight(position: Int): Int {
            return 1
        }

        open fun getPaddingLeft(position: Int): Int {
            return 0
        }

        open fun getPaddingRight(position: Int): Int {
            return 0
        }

        open fun needDrawLine(position: Int): Boolean {
            return true
        }

        open fun getDividerColor(position: Int): Int {
            return -1
        }

    }

    companion object {
        fun createVertical(
            color: Int,
            backgroundColor: Int = Color.TRANSPARENT,
            decorationView: DecorationView = object : DecorationView() {}
        ): CommonItemDecoration {
            return CommonItemDecoration(color, backgroundColor, decorationView)
        }
    }

    init {
        mPaint.color = color
        emptyPaint.color = backgroundColor
        this.decorationView = decorationView
    }
}