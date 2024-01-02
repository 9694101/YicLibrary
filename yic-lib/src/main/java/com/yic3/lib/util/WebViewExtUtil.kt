package com.yic3.lib.util

import android.util.DisplayMetrics
import android.webkit.WebSettings
import android.webkit.WebView

fun WebView.init() {
    val webSettings: WebSettings = this.settings

    webSettings.javaScriptEnabled = true // 很重要
    webSettings.javaScriptCanOpenWindowsAutomatically = true
    webSettings.domStorageEnabled = true
    webSettings.allowFileAccess = true
    webSettings.loadWithOverviewMode = true
    webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
//    webSettings.defaultZoom = when (resources.displayMetrics.densityDpi) {
//        DisplayMetrics.DENSITY_LOW -> WebSettings.ZoomDensity.CLOSE
//        DisplayMetrics.DENSITY_MEDIUM -> WebSettings.ZoomDensity.MEDIUM
//        DisplayMetrics.DENSITY_HIGH -> WebSettings.ZoomDensity.FAR
//        else -> WebSettings.ZoomDensity.MEDIUM
//    }
    // 打开资源复用，即可同时使用http和https的资源
    webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    // clearCacheFolder(getCacheDir(), System.currentTimeMillis()); // 清除系统缓存
    webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
    webSettings.builtInZoomControls = false
    webSettings.displayZoomControls = false
    webSettings.setSupportZoom(false)
    webSettings.setGeolocationEnabled(true)
    webSettings.allowContentAccess = false
    webSettings.textZoom = 100
    webSettings.useWideViewPort = true
//    webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)
//    webSettings.textSize = WebSettings.TextSize.NORMAL
//    webSettings.setNeedInitialFocus(false)
//    webSettings.defaultFontSize = 14

    // webSettings.defaultTextEncodingName = "utf-8" //设置默认编码
    webSettings.allowFileAccessFromFileURLs = true
    webSettings.loadsImagesAutomatically = true //支持自动加载图片
    this.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
    this.isHorizontalScrollBarEnabled = false
    isHorizontalFadingEdgeEnabled = false
    WebView.enableSlowWholeDocumentDraw()
}

fun WebView.loadHtml(content: String?) {
    this.loadDataWithBaseURL("", content ?: "", "text/html","UTF-8", "")
}