package com.yic3.lib.util

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.Utils
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.util.FileDownloadHelper
import com.yic3.lib.R
import com.yic3.lib.databinding.DialogAppUpdateBinding
import com.yic3.lib.entity.AppInitConfig
import com.yic3.lib.entity.AppVersionEntity
import kotlinx.coroutines.*
import java.io.File

object AppUpdateUtil {

    private const val APP_UPDATE_CHECK = "appUpdateCheck"
    private val saveKey: String
    get() {
        return TimeUtils.getNowString(TimeUtils.getSafeDateFormat("yyyy-MM-dd")) + "_" + AppUtils.getAppVersionName()
    }

    fun checkVersion(needToast: Boolean = false) {
        val initConfig = UserInfoManager.getInitConfig(AppInitConfig.APP_UPDATE_INFO)

        if (initConfig is Map<*, *>) {
            val android = initConfig["android"]
            val versionInfo = GsonUtils.fromJson(GsonUtils.toJson(android), AppVersionEntity::class.java)

            if (AppUtils.getAppVersionName() >= (versionInfo.version ?: "")) {
                if (needToast) {
                    ZZToast.showInfo("当前已是最新版本")
                }
                return
            }
            // 非强制且已经提示过
            if (versionInfo.force != true) {
                if (isNotice() && !needToast) {
                    return
                }
                clear()
                hasNotice()
            }
            showUpdateDialog(versionInfo)
        } else {
            if (needToast) {
                ZZToast.showInfo("当前已是最新版本")
            }
        }
    }

    private var appUpdateDialog: AppUpdateDialog? = null

    private fun showUpdateDialog(versionEntity: AppVersionEntity) {
        val topActivity = ActivityUtils.getTopActivity()
        if (topActivity is AppCompatActivity) {
            appUpdateDialog = AppUpdateDialog().also {
                it.versionEntity = versionEntity
                it.update = { force ->
                    if (force) {
                        foreUpgrade(versionEntity.url!!)
                    } else {
                        backUpgrade(versionEntity.url!!)
                    }
                }
            }
            try {
                appUpdateDialog?.show(topActivity.supportFragmentManager, "appUpdate")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun isNotice(): Boolean {
        return SPUtils.getInstance(APP_UPDATE_CHECK).getBoolean(saveKey)
    }

    private fun hasNotice() {
        SPUtils.getInstance(APP_UPDATE_CHECK).put(saveKey, true)
    }

    private fun clear() {
        SPUtils.getInstance(APP_UPDATE_CHECK).clear()
    }

    private var notificationManager: NotificationManager? = null
    private var notificationBuilder: Notification.Builder? = null

    private fun backUpgrade(url: String) {
        FileDownloader.setup(Utils.getApp())
        buildNotify()
        getDownload(url, { pro, apkPath ->
            try {
                notifyDownloadNotification(Utils.getApp(), pro, apkPath)
            } catch (ignored: Exception) {

            }
        }, {
            install(it)
        }).start()
    }

    private fun foreUpgrade(url: String) {
        FileDownloader.setup(Utils.getApp())
        getDownload(url, { pro, _ ->
            appUpdateDialog?.updateProgress(pro)
        }, {
            appUpdateDialog?.downloadComplete(it)
            install(it)
        }).start()
    }

    private fun buildNotify() {
        val context = Utils.getApp()
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "DRIVER_DOWNLOAD_ID"
            val channelName = "${AppUtils.getAppName()}升级通知"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationManager?.createNotificationChannel(notificationChannel)
            Notification.Builder(context, channelId)
        } else {
            Notification.Builder(context)
        }
        notificationBuilder?.setOnlyAlertOnce(true)
    }

    private fun getDownloadPath(context: Context): String {
        return context.filesDir.absolutePath + File.separator + "download"
    }

    private fun getDownload(url: String, progress: (pro: Int, apkPath: String?) -> Unit, complete: (path: String) -> Unit): BaseDownloadTask {
        // val url = "https://cos.pgyer.com/ed756d26b09e5a32895f4219ffd42393.apk?sign=f32f624b00293203eeb12b9a88d24390&t=1679396921&response-content-disposition=attachment%3Bfilename%3D%E4%BA%B2%E4%BA%B2%E6%97%A5%E8%AE%B0_1.1.3.apk"
        val app = Utils.getApp()
        val downloadPath = getDownloadPath(app) + if (url.contains("/")) {
            url.substring(url.lastIndexOf("/"))
        } else {
            File.separator + url
        }
        return FileDownloader.getImpl().create(url)
            .setPath(downloadPath)
            .setCallbackProgressMinInterval(300)
            .setListener(object : FileDownloadListener() {
                override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

                }

                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    progress((soFarBytes / (totalBytes / 100)), task?.targetFilePath)
                }

                override fun completed(task: BaseDownloadTask?) {
                    progress(100, task?.targetFilePath)
                    task?.targetFilePath?.let(complete)
                }

                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {

                }

                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    ZZToast.showError("下载失败")
                    appUpdateDialog?.dismiss()
                }

                override fun warn(task: BaseDownloadTask?) {

                }
            })
    }

    fun install(apkPath: String) {
        AppUtils.installApp(apkPath)
    }

    private fun notifyDownloadNotification(
        context: Context,
        progress: Int,
        apkPath: String? = null
    ) {
        val remoteViews = RemoteViews(context.packageName, R.layout.download_notify_remote_view)
        remoteViews.setTextViewText(
            R.id.txt_title,
            if (progress == 100) "新版本已下载完成,点击安装" else "正在下载新版本"
        )
        remoteViews.setProgressBar(R.id.progress_bar, 100, progress, false)
        remoteViews.setTextViewText(R.id.txt_progress, String.format("%d%%", progress))

        notificationBuilder?.let {
            it.setSmallIcon(R.mipmap.icon_app_launcher)
            it.setAutoCancel(false)
            it.setOngoing(true)
            it.setCustomContentView(remoteViews)
            if (apkPath != null) {
                val installIntent = Intent(Intent.ACTION_VIEW)
                val apkFile = File(apkPath)
                installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                installIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val uri: Uri = FileProvider.getUriForFile(
                    context.applicationContext,
                    context.applicationContext.packageName + ".fileprovider",
                    apkFile
                )
                installIntent.setDataAndType(uri, "application/vnd.android.package-archive")
                val mPendingIntent = PendingIntent.getActivity(context, 0, installIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                it.setContentIntent(mPendingIntent)
            }
            val notification = it.build()
            notificationManager?.notify(0x10000, notification)
        }
    }

}

class AppUpdateDialog: DialogFragment() {

    private lateinit var mDataBinding: DialogAppUpdateBinding
    private var isComplete: Boolean = false
    private var apkPath: String? = null

    var versionEntity: AppVersionEntity? = null
    var update: ((force: Boolean) -> Unit)? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mDataBinding = DialogAppUpdateBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext(), R.style.Translucent_HALF).also {
            it.setContentView(mDataBinding.root)
            if (versionEntity?.force == true) {
                it.setCancelable(false)
            }
            it.setCanceledOnTouchOutside(false)
            it.window?.apply {
                attributes = attributes.also { layoutParams ->
                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                }
            }
        }

        isCancelable = versionEntity?.force != true

        mDataBinding.titleTextView.text = versionEntity?.title
        mDataBinding.contentTextView.text = versionEntity?.description
        mDataBinding.closeImageView.setOnClickListener {
            dismiss()
//            if (versionEntity.force == true) {
//                ActivityUtils.finishAllActivities()
//                // AppUtils.exitApp()
//            }
        }

        mDataBinding.updateTextView.setOnClickListener {
            if (isComplete && apkPath != null) {
                AppUpdateUtil.install(apkPath!!)
                return@setOnClickListener
            }
            update?.invoke(versionEntity?.force == true)
            if (versionEntity?.force != true) {
                dismiss()
                return@setOnClickListener
            }
            mDataBinding.updateTextView.alpha = 0.7f
            mDataBinding.updateTextView.isEnabled = false
            mDataBinding.contentTextView.isGone = true
            mDataBinding.downloadLayout.isVisible = true
        }
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (versionEntity?.force == true) {
            try {
                if (FileDownloadHelper.getAppContext() != null) {
                    FileDownloader.getImpl().pauseAll()
                }
            } catch (ignored: Exception) {
                // 这里未初始化，可能会闪退
            }
            ActivityUtils.finishAllActivities()
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateProgress(progress: Int) {
        mDataBinding.progressTextView.text = "$progress%"
        mDataBinding.updateProgressBar.progress = progress
    }

    fun downloadComplete(apkPath: String) {
        isComplete = true
        this.apkPath = apkPath
        mDataBinding.downloadTipTextView.text = "下载完成请安装"
        mDataBinding.updateTextView.let {
            it.alpha = 1f
            it.text = "立即安装"
            it.isEnabled = true
        }

    }

}