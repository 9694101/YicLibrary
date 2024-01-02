package com.yic3.lib.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.yic3.lib.ui.AppWebActivity

import java.io.Serializable
import java.net.URLDecoder
import java.util.HashMap

/**
 * 服务器返回结果处理类
 * 将网页url或者schema传入stringToMap（），得到map
 * scheme 满足标准格式XXX://a=b&c=d
 * 必有关键字action
 * Created by Administrator on 2016/4/28.
 */
object ZZScheme {

    private const val OPEN_BROWSER = "openBrowser" // 打开外部浏览器
    private const val OPEN_VIEW = "openView" // 打开app中的某个页面
    private const val OPEN_WEB_VIEW = "openWebView" // 打开app中的webView
    private const val CLOSE_VIEW = "closeView" // 关闭当前视图
    private const val TOAST = "toast"
    private const val ALERT = "alert"

    private const val ACTION = "action"
    const val TARGET = "target"

    /**
     * zxk://openWebView/target?url=$url&a=b&c=d
     */
    private fun stringToMap(schema: String): Map<String, String> {
        val uri = Uri.parse(schema)
        val map = HashMap<String, String>()
        map[ACTION] = uri.encodedAuthority!!
        val path = uri.encodedPath
        if (!path.isNullOrEmpty()) {
            map[TARGET] = path.substring(1)
        }
        uri.queryParameterNames?.forEach {
            map[it] = URLDecoder.decode(uri.getQueryParameter(it), "UTF-8")
        }
        return map
    }

    interface SchemeListener : Serializable {
        fun openView(context: Context?, map: Map<String, String>)
        fun getNewActivityTitle(map: Map<String, String>): String
        fun doOther(context: Context?, action: String, map: Map<String, String>)
        fun getNewActivityPageName(map: Map<String, String>): String
    }

    fun whatShouldIDo(context: Context?, url: String?, schemeListener: SchemeListener?) {
        if (url.isNullOrEmpty()) {
            return
        }
        val map = stringToMap(url)
        if (map.containsKey(ACTION)) {
            when (val action = map[ACTION] ?: return) {
                OPEN_VIEW ->
                    try {
                        schemeListener?.openView(context, map)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                OPEN_WEB_VIEW -> {
                    AppWebActivity.openActivity(map.getValue("url"), schemeListener!!.getNewActivityTitle(map))
                }
                OPEN_BROWSER -> {
                    val intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    val uri: Uri
                    if (map.containsKey("url")) {
                        val target = map.getValue("url").trim { it <= ' ' }
                        if ("" == target) {
                            return
                        }
                        uri = Uri.parse(target)
                        intent.data = uri
                        try {
                            context?.startActivity(intent)
                        } catch (e: Exception) {
                            ZZToast.showError("未找到浏览器")
                        }

                    }
                }
                TOAST -> ZZToast.showInfo(map["message"])
                ALERT -> {
                    if (context == null) {
                        return
                    }
                    ZZDialogUtil.showMessageDialog(context, schemeListener?.getNewActivityTitle(map) ?: map["title"].toString(), map["message"].toString(), "确定")
                }
                else -> schemeListener?.doOther(context, action, map)
            }
        }
    }

}
