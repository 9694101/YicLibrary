package com.yic3.lib.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.core.view.isGone
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.luck.picture.lib.photoview.PhotoView
import com.yic3.lib.R
import com.yic3.lib.util.ZZWebImage
import com.yic3.lib.widget.HackyViewPager

/**
 * 点击查看大图
 * 单击退出，双击放大/还原，双指缩放，左右滑动查看
 */
class ImageDialog : Dialog, ViewPager.OnPageChangeListener, DialogInterface.OnDismissListener {

    internal var path: List<String>? = null // 图片路径
    private var samplePagerAdapter: SamplePagerAdapter? = null // viewPager adapter
    internal var loadingDialog: View? = null // 网络缓冲状态
    private var mViewPager: ViewPager? = null
    internal lateinit var loadOk: Array<Boolean?> // 记录每张图片的下载状态，下载成功后，下次点击查看时不弹出等待对话框
    internal var position: Int = 0 // 当前显示的图片位置
    private var dotView: LinearLayout? = null
    private var lastPosition: Int = 0

    internal var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                OPEN -> if (loadingDialog != null) {
                    loadingDialog!!.visibility = View.VISIBLE
                }
                CLOSE -> if (loadingDialog != null) {
                    loadingDialog!!.visibility = View.GONE
                }
                DISMISS -> {
                    val context = context
                    if (context is Activity) {
                        if (!context.isDestroyed && !context.isFinishing) {
                            dismiss()
                        }
                    } else {
                        try {
                            dismiss()
                        } catch (e: Exception) {

                        }
                    }
                }
                else -> {
                }
            }
        }
    }

    /**
     * 设置图片路劲
     *
     * @param path
     */
    fun setPath(path: List<String>): ImageDialog {
        this.path = path
        loadOk = arrayOfNulls(path.size)
        for (i in loadOk.indices) {
            loadOk[i] = false
        }
        if (samplePagerAdapter != null) {
            samplePagerAdapter!!.notifyDataSetChanged()
        }
        return this
    }

    @JvmOverloads
    constructor(
        context: Context,
        theme: Int = R.style.Translucent_NoTitle_Dialog
    ) : super(context, theme)

    fun setPosition(position: Int): ImageDialog {
        this.position = position
        mViewPager?.setCurrentItem(position, false)
        dotView?.let {
            it.getChildAt(lastPosition)?.isSelected = false
            it.getChildAt(position)?.isSelected = true
        }
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.let {
            it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            BarUtils.transparentStatusBar(it)
            it.setContentView(R.layout.dialog_view_pager)
            it.attributes = it.attributes?.also { params ->
                // 透过状态栏
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
                params.width = WindowManager.LayoutParams.MATCH_PARENT
                params.height = ScreenUtils.getScreenHeight()
            }
            it.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        }
        if (path != null) {
            loadOk = arrayOfNulls(path!!.size)
            for (i in loadOk.indices) {
                loadOk[i] = false
            }
        }
        loadingDialog = findViewById(R.id.loading_view)
        mViewPager = findViewById<HackyViewPager>(R.id.view_pager)
        dotView = findViewById(R.id.dotLayout)
        samplePagerAdapter = SamplePagerAdapter()
        mViewPager!!.apply {
            adapter = samplePagerAdapter
            addOnPageChangeListener(this@ImageDialog)
            setCurrentItem(position, false)
            offscreenPageLimit = path?.size ?: 1
        }
        setOnDismissListener(this)

        path?.let {
            if (it.size > 1) {
                val width = SizeUtils.dp2px(5f)
                val mLayoutParams = LinearLayout.LayoutParams(width, width)
                mLayoutParams.rightMargin = width
                for (i in it.indices) {
                    val view = View(context)
                    view.setBackgroundResource(R.drawable.selector_dot)
                    view.layoutParams = mLayoutParams
                    dotView?.addView(view)
                }
                dotView?.getChildAt(position)?.isSelected = true
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (loadingDialog != null) { // 图片查看关闭时，消失网络等待
            loadingDialog!!.visibility = View.GONE
        }
    }

    internal inner class SamplePagerAdapter : PagerAdapter() {

        override fun getCount(): Int {
            return path?.size ?: 0
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val photoView = PhotoView(container.context)

            ZZWebImage.display(
                photoView,
                path!![position],
                object : ZZWebImage.ImageLoadingListener {
                    override fun start() {
                        if (loadOk[position] != true && position == mViewPager!!.currentItem) {
                            handler.sendEmptyMessage(Companion.OPEN)
                        }
                    }

                    override fun end(drawable: Drawable?) {
                        loadOk[position] = true
                        handler.sendEmptyMessage(Companion.CLOSE)
                    }

                })
            // 这里用Glide是为了与相册同步
            // Now just add PhotoView to ViewPager and return it
            container.addView(
                photoView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // 单击图片退出
            photoView.setOnOutsidePhotoTapListener {
                handler.sendEmptyMessage(Companion.DISMISS)
            }
            photoView.setOnPhotoTapListener { _, _, _ ->
                handler.sendEmptyMessage(Companion.DISMISS)
            }
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

    }


    override fun onPageScrolled(i: Int, v: Float, i1: Int) {

    }

    override fun onPageSelected(i: Int) {
        dotView?.let {
            it.getChildAt(lastPosition)?.isSelected = false
            it.getChildAt(i)?.isSelected = true
        }
        lastPosition = i
    }

    override fun onPageScrollStateChanged(i: Int) {

    }

    companion object {
        const val CLOSE = 0
        const val OPEN = 1
        const val DISMISS = 2
    }

}
