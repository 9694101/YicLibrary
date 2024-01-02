package com.yic3.lib.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.view.isGone
import com.blankj.utilcode.util.ActivityUtils
import com.yic3.lib.base.BaseActivity
import com.yic3.lib.databinding.ActivityAppWebBinding
import com.yic3.lib.util.init
import com.yic3.lib.util.loadHtml
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

open class AppWebActivity: BaseActivity<BaseViewModel, ActivityAppWebBinding>() {

    companion object {

        const val HTML_DIRECT = "file:///android_asset/"
        const val ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone"
        const val KEY_URL = "url"
        const val KEY_TITLE = "title"
        const val KEY_TYPE = "type"

        fun getBundle(url: String, title: String, type: String = "url"): Bundle {
            return Bundle().also {
                it.putString(KEY_URL, url)
                it.putString(KEY_TITLE, title)
                it.putString(KEY_TYPE, type)
            }
        }

        fun openActivity(url: String, title: String = "", type: String = "url") {
            ActivityUtils.startActivity(getBundle(url, title, type), AppWebActivity::class.java)
        }

    }

    private lateinit var webSettings: WebSettings
    private var urlPath: String? = null // 网页完整地址
    private var type: String? = "url"


    override fun createObserver() {

    }

    override fun initView(savedInstanceState: Bundle?) {
        intent?.let {
            urlPath = it.getStringExtra(KEY_URL)
            mDatabind.titleLayout.titleTextView.text = it.getStringExtra(KEY_TITLE)
            type = it.getStringExtra(KEY_TYPE)
        }
        initWebView()
        if (!TextUtils.isEmpty(urlPath)) {
            setUrl()
        }

    }

    private fun setUrl() {
        if (urlPath != null) {
            when (type) {
                "url" -> mDatabind.appWebView.loadUrl(urlPath!!)
                "data" -> mDatabind.appWebView.loadData(urlPath!!, "text/html", "UTF-8")
                "html" -> mDatabind.appWebView.loadHtml(getHtmlData(urlPath!!))
            }
        }
    }

    private fun getHtmlData(bodyHTML: String): String {
        val head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style></head>"
        return "<html>$head<body>$bodyHTML</body></html>"
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        mDatabind.appWebView.init()
        mDatabind.appWebView.apply {
            isLongClickable = false // 不允许长按
            requestFocus()
            isVerticalScrollBarEnabled = false
            setVerticalScrollbarOverlay(false)
            isHorizontalScrollBarEnabled = false
            setHorizontalScrollbarOverlay(false)
            webViewClient = ZZWebViewClient()
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    if (newProgress < 100) {
                        mDatabind.progressBar.progress = newProgress
                    } else {
                        mDatabind.progressBar.isGone = true
                    }
                }

                override fun onGeolocationPermissionsShowPrompt(
                    origin: String?,
                    callback: GeolocationPermissions.Callback?
                ) {
                    callback?.invoke(origin, true, false);
                    super.onGeolocationPermissionsShowPrompt(origin, callback)
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    // 图片不加标题
                    if (url!!.contains(".jpg") || url!!.contains(".JPG") ||
                        url!!.contains(".png") || url!!.contains(".PNG") ||
                        url!!.contains(".jpeg") || url!!.contains(".JPEG")
                    ) {
                        return
                    }
                    if (mDatabind.titleLayout.titleTextView.text.isNullOrEmpty()) {
                        mDatabind.titleLayout.titleTextView.text = title
                    }
                }
            }
        }
    }

    /**
     * Android，调用js无法获取到方法的参数返回，因此这里是调用了js的弹窗方法，重写弹窗方法，获取到参数的返回值
     *
     * @param url     永远回调的是本地html的地址，本地不可用
     * @param message 调用参数的返回值（多个方法请在返回值前端加参数判断）
     */
    protected fun onJsMethod(url: String, message: String) {

    }

    /**
     * 注：网络重定向会引起方法异常，如果一定有，请重写此方法，直接写false
     * @return TRUE表示已经消费该事件，ZZWebActivity将不结束界面
     */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (mDatabind.appWebView.canGoBack()) {
            if (urlPath == null || urlPath == mDatabind.appWebView.url) {
                super.onBackPressed()
                return
            }
            mDatabind.appWebView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDatabind.appWebView.destroy()
    }

    /**
     * 重写webView的WebViewClient类，使webView在app内部打开
     */
    private inner class ZZWebViewClient : android.webkit.WebViewClient() {

        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            // 需要拦截scheme操作请写在此处
            if (onShouldLoad(url)) {
                return true
            }
            if (url.contains("UpLoadFiles")) {
                return false
            }

            view.loadUrl(url)
            return true
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        //	网络加载完成
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            onPageLoadFinish(view)
        }
    }

    /**
     * 对点击的事件，即跳转url的拦截处理
     *
     * @param url 按钮点击时的指令
     * @return true将拦截，false将执行webView.loadUrl
     */
    protected fun onShouldLoad(url: String): Boolean {
        // return WebClickUtils.dealWebEvent(getContext(), url);
        return false
    }

    /**
     * 页面加载完成时调用的方法，一般是传入初始化显示的参数
     *
     * @param webView
     */
    fun onPageLoadFinish(webView: WebView) {}

}