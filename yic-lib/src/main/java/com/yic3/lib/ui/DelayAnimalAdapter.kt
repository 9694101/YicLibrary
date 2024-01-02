package com.yic3.lib.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import com.blankj.utilcode.util.ScreenUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.animation.BaseAnimation
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.coroutines.*

abstract class DelayAnimalAdapter<T, VH: BaseViewHolder>(layoutId: Int, private val spanCount: Int = 1): BaseQuickAdapter<T, VH>(layoutId) {

    init {
        adapterAnimation = ShowAnimal()
        isAnimationFirstOnly = true
    }

    inner class ShowAnimal: BaseAnimation {
        override fun animators(view: View): Array<Animator> {
            val translateAnimator = ObjectAnimator.ofFloat(view, "translationX", ScreenUtils.getAppScreenWidth().toFloat(), 0f)
            translateAnimator.duration = 200
            return arrayOf(translateAnimator)
        }

    }

    fun clear() {
        super.setNewInstance(null)
    }

    override fun setNewInstance(list: MutableList<T>?) {
        super.setNewInstance(null)
        GlobalScope.launch(Dispatchers.Main) {
            list?.forEachIndexed { index, it ->
                addData(it)
                if (index % spanCount == spanCount - 1) {
                    withContext(Dispatchers.IO) {
                        Thread.sleep(50)
                    }
                }
            }
        }
    }

}