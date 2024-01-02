package com.yic3.lib.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ScreenUtils
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.yic3.lib.databinding.DialogImageShareBinding
import com.yic3.lib.util.Permission
import com.yic3.lib.util.StatEvent
import com.yic3.lib.util.UserBehaviorUtil
import com.yic3.lib.util.UserInfoManager
import com.yic3.lib.util.WechatShareUtil
import com.yic3.lib.util.ZZDialogUtil
import com.yic3.lib.util.ZZPermissionUtil
import com.yic3.lib.util.ZZToast

class ShareImageDialog: DialogFragment() {

    private lateinit var mDatabind: DialogImageShareBinding
    private var shareInfoView: View? = null
    private var id: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mDatabind = DialogImageShareBinding.inflate(inflater)
        initView()
        return mDatabind.root
    }

    private fun initView() {
        addShareInfoLayout()
        mDatabind.wechatImageView.setOnClickListener {
            WechatShareUtil.shareImage(it.context,
                ImageUtils.view2Bitmap(shareInfoView),
                SendMessageToWX.Req.WXSceneSession)
            postShareBehavior()
        }
        mDatabind.circleImageView.setOnClickListener {
            WechatShareUtil.shareImage(it.context,
                ImageUtils.view2Bitmap(shareInfoView),
                SendMessageToWX.Req.WXSceneTimeline)
            postShareBehavior()
        }
        mDatabind.saveImageView.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
                if (Environment.isExternalStorageManager()) {
                    ImageUtils.save2Album(ImageUtils.view2Bitmap(mDatabind.shareContentLayout), Bitmap.CompressFormat.PNG)
                    ZZToast.showOk("图片已保存至相册")
                } else {
                    ZZDialogUtil.showMessageDialog(it.context, "提示", "请开启文件访问权限，否则无法保存图片", "确定",
                        object : ZZDialog.OnClickListener {
                            override fun onClick(view: View) {
                                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                                startActivity(intent)
                            }
                        }, "取消")
                }
            } else {
                ZZPermissionUtil.requestPermission(Permission.存储权限, callback = object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        ImageUtils.save2Album(ImageUtils.view2Bitmap(mDatabind.shareContentLayout), Bitmap.CompressFormat.PNG)
                        ZZToast.showOk("图片已保存至相册")
                    }

                    override fun onDenied() {

                    }
                })
            }
        }
        mDatabind.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun postShareBehavior() {
        when (tag) {
            "shareRecruit" -> {
                UserBehaviorUtil.postToService(StatEvent.分享招工详细, id ?: 0)
            }
            "sharePublish" -> {
                UserBehaviorUtil.postToService(StatEvent.分享我发布的招工, UserInfoManager.userId)
            }
            "userInvitation" -> {
                UserBehaviorUtil.postToService(StatEvent.分享邀请有礼, UserInfoManager.userId)
            }
            "shareWorker" -> {

            }
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addShareInfoLayout() {
        shareInfoView?.let {
            if (it.parent != null) {
                (it.parent as ViewGroup).removeView(it)
            }
            mDatabind.shareContentLayout.addView(it, it.layoutParams)
            it.isClickable = true
            mDatabind.shareContentLayout.setOnClickListener {
                dismiss()
            }
            it.post {
                val height = shareInfoView!!.measuredHeight
                val parentHeight: Int = mDatabind.shareContentLayout.measuredHeight - 2 * BarUtils.getStatusBarHeight()

                if (height <= parentHeight) {
                    return@post
                }

                val scale = parentHeight.toFloat() / height
                val animation = ScaleAnimation(
                    1f, scale, 1f, scale,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                )
                animation.duration = 200
                animation.fillAfter = true
                shareInfoView!!.startAnimation(animation)

            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object: Dialog(requireContext(), com.yic3.lib.R.style.Translucent_NoTitle_Dialog) {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                window?.let {
                    it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    BarUtils.transparentStatusBar(it)
                    if (databindIsInit()) {
                        it.setContentView(mDatabind.root)
                    }
                    it.attributes = it.attributes?.also { params ->
                        // 透过状态栏
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                        }
                        params.width = WindowManager.LayoutParams.MATCH_PARENT
                        params.height = ScreenUtils.getScreenHeight()
                    }
                    it.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
                }

            }
        }
    }

    private fun databindIsInit(): Boolean {
        return ::mDatabind.isInitialized
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (isAdded) {
            return
        }
        try {
            super.show(manager, tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showView(shareView: View, manager: FragmentManager, tag: String?, id: Long? = null) {
        shareInfoView = shareView
        this.id = id
        show(manager, tag)
    }

}