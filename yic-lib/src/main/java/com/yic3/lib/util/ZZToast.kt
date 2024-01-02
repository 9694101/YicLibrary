package com.yic3.lib.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Display
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.blankj.utilcode.util.Utils
import com.yic3.lib.R


@SuppressLint("StaticFieldLeak")
object ZZToast {

    private var toast: Toast? = null

    @SuppressLint("StaticFieldLeak")
    private var toastImage: ImageView? = null

    @SuppressLint("StaticFieldLeak")
    private var toastText: TextView? = null

    @SuppressLint("StaticFieldLeak")
    private var layout: ViewGroup? = null
    private var okIcon: Int = 0
    private var errorIcon: Int = 0
    private var infoIcon: Int = 0
    private val HANDLER: Handler = Handler(Looper.getMainLooper())

    fun init(ctx: Context) {
        layout = View.inflate(ctx, R.layout.toast_tips, null) as ViewGroup
        toastImage = layout!!.findViewById(R.id.iv_toastImage)
        toastText = layout!!.findViewById(R.id.toastText)
    }

    private fun show(text: String?, toastIcon: Int) {
        if (text.isNullOrEmpty()) {
            return
        }
        HANDLER.post {
            try {
                if (toast != null) {
                    toast!!.cancel()
                }
                val context = Utils.getApp()
                toast = Toast(context)

                if (layout == null) {
                    layout = View.inflate(context, R.layout.toast_tips, null) as ViewGroup
                    toastImage = layout!!.findViewById(R.id.iv_toastImage)
                    toastText = layout!!.findViewById(R.id.toastText)
                }

                //text.setText("完全自定义Toast完全自定义Toast");
                toast!!.setGravity(Gravity.CENTER, 0, 0)
                toast!!.duration = Toast.LENGTH_SHORT
                toast!!.view = layout
                toastImage!!.setImageResource(toastIcon)
                toastText!!.text = text
                toastText!!.visibility = View.VISIBLE

                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                    try {
                        val field = View::class.java.getDeclaredField("mContext")
                        field.isAccessible = true
                        field.set(toast!!.view, ApplicationContextWrapperForApi25())
                    } catch (throwable: Throwable) {
                        throwable.printStackTrace()
                    }
                }
                toast!!.show()
            } catch (ignored: Exception) {
                //e.printStackTrace()
            }
        }
    }

    private fun show(resId: Int, toastIcon: Int) {
        val text = Utils.getApp().getString(resId)
        show(text, toastIcon)
    }

    fun setOkIcon(okIcon: Int) {
        ZZToast.okIcon = okIcon
    }

    fun setErrorIcon(errorIcon: Int) {
        ZZToast.errorIcon = errorIcon
    }

    fun setInfoIcon(infoIcon: Int) {
        ZZToast.infoIcon = infoIcon
    }

    @JvmStatic
    fun showOk(text: String?) {
        if (okIcon != 0) {
            show(text, okIcon)
        } else {
            show(text, R.mipmap.qmui_tips_done)
        }
    }

    fun showOk(resId: Int) {
        if (okIcon != 0) {
            show(resId, okIcon)
        } else {
            show(resId, R.mipmap.qmui_tips_done)
        }
    }

    fun showError(resId: Int) {
        if (errorIcon != 0) {
            show(resId, errorIcon)
        } else {
            show(resId, R.mipmap.qmui_tips_error)
        }
    }

    @JvmStatic
    fun showError(text: String?) {
        if (errorIcon != 0) {
            show(text, errorIcon)
        } else {
            show(text, R.mipmap.qmui_tips_error)
        }
    }

    fun showInfo(resId: Int) {
        if (infoIcon != 0) {
            show(resId, infoIcon)
        } else {
            show(resId, R.mipmap.qmui_tips_info)
        }
    }

    @JvmStatic
    fun showInfo(text: String?) {
        if (infoIcon != 0) {
            show(text, infoIcon)
        } else {
            show(text, R.mipmap.qmui_tips_info)
        }
    }

}

class ApplicationContextWrapperForApi25 : ContextWrapper(Utils.getApp()) {

    override fun getApplicationContext(): Context {
        return this
    }

    override fun getSystemService(name: String): Any {
        return if (Context.WINDOW_SERVICE == name) {
            WindowManagerWrapper(baseContext.getSystemService(name) as WindowManager)
        } else super.getSystemService(name)
    }

    private inner class WindowManagerWrapper constructor(private val base: WindowManager) :
        WindowManager {

        @Deprecated("Deprecated in Java")
        override fun getDefaultDisplay(): Display {
            return base.defaultDisplay
        }

        override fun removeViewImmediate(view: View) {
            base.removeViewImmediate(view)
        }

        override fun addView(view: View, params: ViewGroup.LayoutParams) {
            try {
                base.addView(view, params)
            } catch (e: WindowManager.BadTokenException) {
                e.printStackTrace()
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
        }

        override fun updateViewLayout(view: View, params: ViewGroup.LayoutParams) {
            base.updateViewLayout(view, params)
        }

        override fun removeView(view: View) {
            base.removeView(view)
        }
    }

}