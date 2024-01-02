package com.yic3.lib.base

import android.app.Dialog
import android.app.Instrumentation
import android.content.res.Resources
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.SPUtils
import com.gyf.immersionbar.ImmersionBar
import com.yic3.lib.R
import me.hgj.jetpackmvvm.base.activity.BaseVmDbActivity
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

abstract class BaseActivity<VM : BaseViewModel, DB : ViewDataBinding>: BaseVmDbActivity<VM, DB>() {

    companion object {

        const val SP_NAME = "setting"
        const val FONT_SCALE_KEY = "fontScale"

        var touchEventLocation = IntArray(2)

        var _sp_font_scale: Float? = null

        val baseFontScale: Float
            get() {
                if (_sp_font_scale == null) {
                    _sp_font_scale = SPUtils.getInstance(SP_NAME).getFloat(FONT_SCALE_KEY, 1f)
                }
                return _sp_font_scale!!
            }

        fun updateFontScale(scale: Float) {
            _sp_font_scale = scale
            SPUtils.getInstance(SP_NAME).put(FONT_SCALE_KEY, scale)

        }

    }

    private var loadingDialog: DialogFragment? = null // 加载等待对话框
    private var autoCloseInput = true

    open fun onBeforeInit() {
        ImmersionBar.with(this)
            .transparentStatusBar()
            .statusBarDarkFont(true)
            .init()
    }

    override fun createObserver() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        onBeforeInit()
        super.onCreate(savedInstanceState)
        findViewById<View>(R.id.back_textView)?.let {
            it.setOnClickListener { view ->
                goBack(view)
            }
        }
    }

    override fun getResources(): Resources {
        // 字体大小不跟随系统设置
        val resources = super.getResources()
        resources.configuration.fontScale = baseFontScale
        return createConfigurationContext(resources.configuration).resources
    }

    override fun showLoading(message: String) {
        initDialog()
        runOnUiThread {
            loadingDialog?.let {
                if (it is LoadingDialogFragment) {
                    // it.tips = message
                }
                if (!it.isAdded) {
                    it.show(supportFragmentManager, "loading")
                }
            }
        }
    }

    override fun dismissLoading() {
        runOnUiThread {
            try {
                if (loadingDialog?.isAdded == true) {
                    loadingDialog!!.dismiss()
                }
            } catch (ignored: Exception) {

            }
        }
    }

    fun goBack(view: View?) {
        // 系统的返回键会优先关闭掉输入键盘，因此这里模拟back按键的事件
        onBackPressedDispatcher.onBackPressed()
    }

    protected fun closeAutoCloseInput() {
        autoCloseInput = false
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        try {
            touchEventLocation[0] = event.x.toInt()
            touchEventLocation[1] = event.y.toInt()
            if (event.action == MotionEvent.ACTION_DOWN) {
                val v = currentFocus
                if (autoCloseInput) {
                    if (isShouldHideKeyboard(v, event)) {
                        KeyboardUtils.hideSoftInput(this)
                    }
                }
            }
            return super.dispatchTouchEvent(event)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


    // 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
    private fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.height
            val right = left + v.width
            return !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        }
        return false
    }

    /**
     * 模拟键盘事件方法
     * @param keyCode 按键
     */
    protected fun actionKey(keyCode: Int) {
        object : Thread() {
            override fun run() {
                try {
                    val inst = Instrumentation()
                    inst.sendKeyDownUpSync(keyCode)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }.start()
    }

    protected open fun initDialog() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialogFragment()
        }
    }

    protected fun setLoadingDialog(view: View?) {
        if (view != null && loadingDialog != null) {
            loadingDialog!!.dialog?.setContentView(view)
        }
    }

    fun getLoadingDialog(): DialogFragment? {
        if (this.loadingDialog == null) {
            initDialog()
        }
        return this.loadingDialog
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog = null
    }

}

class LoadingDialogFragment: DialogFragment() {

    var tips: String? = null
        set(value) {
            field = value
            tipTextView?.let {
                if (!value.isNullOrEmpty()) {
                    it.visibility = View.VISIBLE
                    it.text = value
                } else {
                    it.visibility = View.GONE
                }
            }
        }

    var tipTextView: TextView? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.Translucent_NoTitle_Dialog)
        val window = dialog.window
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_loading)
        tipTextView = dialog.findViewById(R.id.loading_message_textView)
        if (!tips.isNullOrEmpty()) {
            tipTextView!!.visibility = View.VISIBLE
            tipTextView!!.text = tips
        }
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun <T : Fragment> FragmentManager.getFragment(cls: Class<T>, bundle: Bundle? = null): T {
    var fragment: Fragment? = findFragmentByTag(cls.name)
    if (fragment == null) {
        fragment = cls.newInstance()
    }
    bundle?.let {
        fragment!!.arguments = it
    }
    return fragment as T
}