package com.yic3.lib.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.widget.ImageView
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.yic3.lib.R
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.io.File
import java.util.concurrent.ExecutionException

/**
 * 网络图片异步加载类
 * 初始化后直接调用display
 * 3种初始化方式
 * 需要修改默认参数，使用多参数方法
 * 移除圆角功能，如需使用圆角，imageView请使用 SelectableRoundedImageView
 * 注：glide的圆角功能暂不完善，慎用，推荐使用 SelectableRoundedImageView
 * Created by Zhang on 2016/8/4.
 */
object ZZWebImage {

    /**
     * display
     * 显示
     *
     * @param url       图片地址
     * @param imageView 显示图片的控件
     * @author zb
     */
    @JvmStatic
    fun display(imageView: ImageView, url: String?, listener: ImageLoadingListener) {
        display(imageView, url, R.drawable.bg_default_image, listener)
    }

    fun display(imageView: ImageView, resId: Int) {
        if (imageView.context == null) {
            return
        }
        Glide.with(imageView.context).load(resId).apply(
            RequestOptions().centerCrop()
                .placeholder(imageView.drawable)
        ).into(imageView)
    }

    fun display(
        imageView: ImageView,
        url: String?,
        options: RequestOptions,
        listener: ImageLoadingListener? = null
    ) {
        if (url.isNullOrEmpty()) {
            return
        }
        if (imageView.context == null) {
            return
        }
        val apply = Glide.with(imageView.context).load(url).apply(options)
        if (listener != null) {
            apply.into(object : CustomTarget<Drawable>() {

                override fun onLoadStarted(placeholder: Drawable?) {
                    super.onLoadStarted(placeholder)
                    listener.start()
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    imageView.setImageDrawable(resource)
                    listener.end(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    listener.end(errorDrawable)
                }

                override fun onDestroy() {
                    super.onDestroy()
                    listener.end(null)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    listener.end(placeholder)
                }

            })
        } else {
            apply.into(imageView)
        }
    }

    @JvmStatic
    fun displayRound(imageView: ImageView, url: String?, radius: Float) {
        val bitmapTransform = RequestOptions
                .bitmapTransform(RoundedCornersTransformation(SizeUtils.dp2px(radius), 0))
                .placeholder(R.drawable.bg_default_image)
        display(imageView, url, bitmapTransform)
    }

    @SuppressLint("CheckResult")
    fun getDefaultOptions(res: Int = R.drawable.bg_default_image, listener: ImageLoadingListener? = null): RequestOptions {
        val requestOptions = RequestOptions()
        if (listener == null) {
            requestOptions.centerCrop()
        }
        requestOptions.placeholder(res)
        return requestOptions
    }

    fun displayVideo(imageView: ImageView?, url: String?) {
        if (imageView == null || url.isNullOrEmpty()) {
            return
        }
        if (imageView.context == null) {
            return
        }
        if (imageView.context is Activity) {
            val activity = imageView.context as Activity
            if (activity.isDestroyed || activity.isFinishing) {
                return
            }
        }
        Glide.with(imageView.context)
            .setDefaultRequestOptions(RequestOptions()
                .frame(100 * 1000) // 微秒
                .centerCrop()
                .placeholder(R.drawable.bg_default_image)
                .error(R.drawable.bg_default_image))
            .load(url)
            .into(imageView)
    }

    @SuppressLint("CheckResult")
    @JvmOverloads
    @JvmStatic
    fun display(
        imageView: ImageView?,
        url: String?,
        res: Int = R.drawable.bg_default_image,
        listener: ImageLoadingListener? = null
    ) {
        if (imageView == null) {
            return
        }
        if (imageView.context == null) {
            return
        }
        if (imageView.context is Activity) {
            val activity = imageView.context as Activity
            if (activity.isDestroyed || activity.isFinishing) {
                return
            }
        }
        if (url.isNullOrEmpty()) {
            display(imageView, res)
            return
        }

        val requestOptions = RequestOptions()
        if (listener == null) {
            requestOptions.centerCrop()
        }
        requestOptions.placeholder(res)

        if (url.endsWith("gif", true)) {
            val builder = Glide.with(imageView.context).asGif().load(url).apply(requestOptions)
            builder.into(imageView)
        } else {
            val builder = Glide.with(imageView.context).load(url).apply(requestOptions)
            if (listener != null) {
                builder.into(object : CustomTarget<Drawable>() {
                    override fun onLoadStarted(placeholder: Drawable?) {
                        super.onLoadStarted(placeholder)
                        listener.start()
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        imageView.setImageDrawable(resource)
                        listener.end(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        listener.end(errorDrawable)
                    }

                    override fun onDestroy() {
                        super.onDestroy()
                        listener.end(null)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        listener.end(placeholder)
                    }

                })
            } else {
                builder.into(imageView)
            }
        }

    }

    /**
     * 注意是异步
     * @param url 图片地址
     * @return Drawable
     */
    fun getDrawable(url: String): Drawable? {
        var drawable: Drawable? = null
        try {
            drawable = Glide.with(Utils.getApp())
                .load(url)
                .submit()
                .get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        return drawable
    }

    /**
     * 注意是异步
     * @param url 图片地址
     * @return Bitmap
     */
    fun getBitmap(url: String?): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = Glide.with(Utils.getApp())
                .asBitmap()
                .load(url)
                .submit()
                .get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        return bitmap
    }

    /**
     * 图片加载事件
     */
    interface ImageLoadingListener {

        fun start() {

        }

        fun end(drawable: Drawable?) {

        }

        fun onError() {

        }
    }

    /**
     * 下载
     * @param context
     * @param url
     * @param listener 用于回调（注意是异步线程）
     */
    fun download(context: Context, url: String, listener: DownloadListener) {
        PermissionUtils.permission(PermissionConstants.STORAGE)
            .callback(object : PermissionUtils.SimpleCallback {
                override fun onGranted() {
                    downloadImage(context, url, listener)
                }

                override fun onDenied() {
                    ZZToast.showError("请给予用户存储权限")
                }
            }).request()
    }

    private fun downloadImage(context: Context, url: String, listener: DownloadListener) {
        Thread(DownloadRunnable(context, url, listener)).start()
    }

    private class DownloadRunnable(
        var context: Context,
        var url: String,
        var listener: DownloadListener
    ) : Runnable {

        override fun run() {
            val bitmap = getBitmap(url)
            if (bitmap == null) {
                listener.download("图片下载失败")
                return
            }
            var fileName: String? = null
            if (url.contains("/")) {
                fileName = url.substring(url.lastIndexOf("/"))
                fileName.replace("png", "jpg")
            }
            if (fileName.isNullOrEmpty()) {
                fileName = "${TimeUtils.getNowString(TimeUtils.getSafeDateFormat("yyyyMMddHHmmss"))}.jpg"
            }

            val filePath =
                Environment.getExternalStorageDirectory().toString() + "/carry_me/image/" + fileName
            val file = File(filePath)

            if (file.exists()) {
                listener.download("图片已经存在")
                return
            }

            ImageUtils.save(bitmap, file, Bitmap.CompressFormat.JPEG)
            listener.download("图片下载保存成功")
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
        }
    }

    fun downloadFile(context: Context, url: String, listener: DownloadListener) {
        object : Thread() {
            override fun run() {
                try {
                    val file = Glide.with(context).downloadOnly().load(url).submit().get()

                    val fileName = TimeUtils.getNowString(TimeUtils.getSafeDateFormat("yyyyMMddHHmmss")) + ".jpg"
                    val filePath =
                        Environment.getExternalStorageDirectory().toString() + "/carry_me/image/"

                    val destFile = File(filePath + fileName)
                    FileUtils.copy(file, destFile)
                    file.delete()
                    context.sendBroadcast(
                        Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(destFile)
                        )
                    )
                    listener.download(String.format("图片已保存至%s文件夹", filePath))

                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                }

            }
        }.start()
    }

    interface DownloadListener {
        fun download(msg: String)
    }

}
