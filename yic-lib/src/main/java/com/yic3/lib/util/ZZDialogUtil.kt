package com.yic3.lib.util

import android.content.Context
import android.view.WindowManager
import com.yic3.lib.dialog.ZZDialog


/**
 * 提供几个常用的对话框的显示方法
 * Created by Dell on 2017/11/27.
 */
object ZZDialogUtil {

    /**
     * 参数名见ZZDialog
     */
    fun showMessageDialog(
        context: Context?,
        title: String,
        message: CharSequence,
        positive: String,
        pListener: ZZDialog.OnClickListener? = null,
        negative: String? = null,
        nListener: ZZDialog.OnClickListener? = null,
        alwaysCenter: Boolean = true,
        canCancel: Boolean = false
    ): ZZDialog? {
        if (context == null) {
            return null
        }
        val dialog = ZZDialog(context)
        dialog.setTitleText(title)
            .setMessage(message, alwaysCenter)
            .setPositive(positive, pListener)
            .setNegative(negative, nListener)
        dialog.setCanceledOnTouchOutside(canCancel)
        dialog.setCancelable(canCancel)
        try {
            dialog.show()
        } catch (e: WindowManager.BadTokenException) {
            e.printStackTrace()
        }
        return dialog
    }

    /**
     * 参数名见ZZDialog
     */
    fun showMessageDialog(
        context: Context?,
        title: Int = 0,
        message: Int = 0,
        positive: Int = 0,
        pListener: ZZDialog.OnClickListener? = null,
        negative: Int = 0,
        nListener: ZZDialog.OnClickListener? = null,
        alwaysCenter: Boolean = true,
        canCancel: Boolean = false
    ): ZZDialog? {
        if (context == null) {
            return null
        }
        val dialog = ZZDialog(context)
        dialog.setTitleText(checkId(context, title))
            .setMessage(checkId(context, message), alwaysCenter)
            .setPositive(checkId(context, positive), pListener)
            .setNegative(checkId(context, negative), nListener)
        dialog.setCanceledOnTouchOutside(canCancel)
        dialog.setCancelable(canCancel)
        try {
            dialog.show()
        } catch (e: WindowManager.BadTokenException) {
            e.printStackTrace()
        }
        return dialog
    }

    private fun checkId(context: Context, id: Int) = if (id > 0) {
        context.getString(id)
    } else {
        ""
    }
}