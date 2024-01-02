package com.yic3.lib.util

import android.app.Application
import android.content.Context
import android.os.Process
import android.text.TextUtils
import com.blankj.utilcode.util.AppUtils
import com.github.gzuliyujiang.oaid.DeviceIdentifier
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.commonsdk.UMConfigure
import com.yic3.lib.base.AccountConfig
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

object ThirdInitUtil {

    fun init(context: Application) {
        // 获取当前进程名
        val processName: String? = getProcessName(Process.myPid())
        // 设置是否为上报进程
        val strategy = CrashReport.UserStrategy(context)
        strategy.isUploadProcess = processName == null || processName == context.packageName
        CrashReport.initCrashReport(context, AccountConfig.BUGLY_APP_ID, true, strategy)
        DeviceIdentifier.register(context)
        UMConfigure.submitPolicyGrantResult(context, true)
        UMConfigure.init(context, UMConfigure.DEVICE_TYPE_PHONE, "")
        UMConfigure.setLogEnabled(AppUtils.isAppDebug())
    }

    private fun getProcessName(pid: Int): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName = reader.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim { it <= ' ' }
            }
            return processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
        return null
    }

}