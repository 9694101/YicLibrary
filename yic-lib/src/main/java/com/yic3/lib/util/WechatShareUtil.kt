package com.yic3.lib.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.ImageUtils
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXFileObject
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.yic3.lib.R
import java.io.File


object WechatShareUtil {

    fun shareHtml(context: Context, title: String, describe: String, htmlUrl: String) {
        val api = WXAPIFactory.createWXAPI(context, UserInfoManager.getWxAppId())
        //初始化一个WXWebpageObject，填写url
        val webpage = WXWebpageObject()
        webpage.webpageUrl = htmlUrl

        //用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
        val msg = WXMediaMessage(webpage)
        msg.title = title
        msg.description = describe

        msg.thumbData = ImageUtils.bitmap2Bytes(ImageUtils.getBitmap(R.mipmap.icon_app_launcher_round))

        //构造一个Req
        val req = SendMessageToWX.Req()
        req.transaction = "webpage"
        req.message = msg
        req.scene = SendMessageToWX.Req.WXSceneSession

        //调用api接口，发送数据到微信
        api.sendReq(req)
    }

    fun shareImage(context: Context, bitmap: Bitmap?, scene: Int) {
        if (bitmap == null) {
            return
        }
        val api = WXAPIFactory.createWXAPI(context, UserInfoManager.getWxAppId())
        val wxImage = WXImageObject(bitmap)
        val msg = WXMediaMessage(wxImage)

        val req = SendMessageToWX.Req()
        req.transaction = "wxImage"
        req.message = msg
        req.scene = scene
        api.sendReq(req)
    }

    fun shareFile(context: Context, filePath: String) {
        val api = WXAPIFactory.createWXAPI(context, UserInfoManager.getWxAppId(), true)

        val wxFile = WXFileObject()
        val shareFile = File(filePath)
        // wxFile.fileData = FileIOUtils.readFile2BytesByStream(shareFile)
        wxFile.filePath = getFileUri(context, shareFile)
        val msg = WXMediaMessage(wxFile)
        msg.title = shareFile.name
        msg.thumbData = ImageUtils.bitmap2Bytes(ImageUtils.getBitmap(R.mipmap.icon_app_launcher_round))
        //构造一个Req
        val req = SendMessageToWX.Req()
        req.transaction = "filePage"
        req.message = msg
        req.scene = SendMessageToWX.Req.WXSceneSession

        //调用api接口，发送数据到微信
        api.sendReq(req)
    }

    private fun getFileUri(context: Context, file: File?): String? {
        if (file == null || !file.exists()) {
            return null
        }
        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",  // 要与`AndroidManifest.xml`里配置的`authorities`一致，假设你的应用包名为com.example.app
            file
        )

        // 授权给微信访问路径
        context.grantUriPermission(
            "com.tencent.mm",  // 这里填微信包名
            contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        return contentUri.toString() // contentUri.toString() 即是以"content://"开头的用于共享的路径
    }

}