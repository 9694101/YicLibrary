package com.yic3.lib.widget

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager2.widget.ViewPager2
import org.libpag.PAGImageView
import org.libpag.PAGScaleMode

fun ViewPager2.setCurrentItem(
    item: Int,
    duration: Long,
    interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
    pagePxWidth: Int = width // 使用viewpager2.getWidth()获取
) {
    val pxToDrag: Int = pagePxWidth * (item - currentItem)
    val animator = ValueAnimator.ofInt(0, pxToDrag)
    var previousValue = 0
    animator.addUpdateListener { valueAnimator ->
        val currentValue = valueAnimator.animatedValue as Int
        val currentPxToDrag = (currentValue - previousValue).toFloat()
        fakeDragBy(-currentPxToDrag)
        previousValue = currentValue
    }
    animator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
            beginFakeDrag()
        }

        override fun onAnimationEnd(animation: Animator) {
            endFakeDrag()
        }

        override fun onAnimationCancel(animation: Animator) {

        }

        override fun onAnimationRepeat(animation: Animator) {

        }
    })
    animator.interpolator = interpolator
    animator.duration = duration
    animator.start()
}

fun ViewPager2.stopInertiaRolling() {
    getRecyclerView()?.stopInertiaRolling()
}

fun ViewPager2.getRecyclerView(): RecyclerView? {
    try {
        val field = this.javaClass.getDeclaredField("mRecyclerView")
        field.isAccessible = true
        return field.get(this) as RecyclerView
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

//停止惯性滚动
fun RecyclerView.stopInertiaRolling() {
    try {
        //如果是Support的RecyclerView则需要使用"cancelTouch"
        val field = this.javaClass.getDeclaredMethod("cancelScroll")
        field.isAccessible = true
        field.invoke(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun RecyclerView.clearNotifyAnimator() {
    itemAnimator?.let { animator ->
        animator.addDuration = 0
        animator.changeDuration = 0
        animator.moveDuration = 0
        animator.removeDuration = 0
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }
}

fun ViewGroup.findAllTextView(): List<TextView> {
    val list = mutableListOf<TextView>()
    for (index in 0 until childCount) {
        val childAt = getChildAt(index)
        if (childAt is TextView) {
            list.add(childAt)
        } else if (childAt is ViewGroup) {
            list.addAll(childAt.findAllTextView())
        }
    }
    return list
}

fun PAGImageView.play(fileName: String, repeat: Int) {
    path = "assets://$fileName"
    setRepeatCount(repeat)
    setCacheAllFramesInMemory(false)
    setScaleMode(PAGScaleMode.None)
    play()
}