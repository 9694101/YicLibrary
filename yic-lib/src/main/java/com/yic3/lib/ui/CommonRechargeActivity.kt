package com.yic3.lib.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.databinding.ViewDataBinding
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.yic3.lib.base.BaseActivity
import com.yic3.lib.dialog.ZZDialog
import com.yic3.lib.entity.OrderPayEntity
import com.yic3.lib.event.UserInfoRefreshEvent
import com.yic3.lib.net.NetworkApi
import com.yic3.lib.util.AlipayResultEvent
import com.yic3.lib.util.AlipayUtil
import com.yic3.lib.util.StatEvent
import com.yic3.lib.util.UserBehaviorUtil
import com.yic3.lib.util.UserInfoManager
import com.yic3.lib.util.VipRole
import com.yic3.lib.util.WePayUtil
import com.yic3.lib.util.WechatUtil
import com.yic3.lib.util.WxPaySuccessEvent
import com.yic3.lib.util.ZZDialogUtil
import com.yic3.lib.util.ZZToast
import com.yic3.lib.util.registerEvent
import com.yic3.lib.util.unregisterEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

abstract class CommonRechargeActivity<VM : BaseViewModel, DB : ViewDataBinding>: BaseActivity<VM, DB>() {

    companion object {

        fun openActivity(source: String, cls: Class<out Activity>) {
            val bundle = bundleOf(
                Pair("source", source)
            )
            ActivityUtils.startActivity(bundle, cls)
        }

        const val THE_PACKETS = "包年"

        const val KEY_SOURCE = "source"
        const val DURATION = 5 * 60 * 1000L // ms

        const val TYPE_WECHAT = "weixin"
        const val TYPE_ALIPAY = "alipay"

        const val SOURCE_启动引导页 = "bootpage"
        const val SOURCE_个人中心 = "center"
        const val SOURCE_首页 = "home"
        const val SOURCE_专题 = "special"
        const val SOURCE_合同 = "contract"

    }

    protected val payChannelList = UserInfoManager.getPayChannelList()
    protected lateinit var source: String
    protected var payType: String = TYPE_WECHAT
    private var orderId: Int = 0

    private var isPaying: Boolean = false

    override fun initView(savedInstanceState: Bundle?) {
        registerEvent()
        source = intent?.getStringExtra(KEY_SOURCE) ?: SOURCE_启动引导页
        onBackPressedDispatcher.addCallback(this) {
            backOrStartHome()
        }
    }

    protected fun createOrder(goodsId: String, payType: String, payPrice: String, source: String) {
        this.payType = payType
        if (isPaying) {
            return
        }

        if (UserInfoManager.isLoginBeforeRecharge()) {
            ActivityUtils.startActivity(LoginActivity::class.java)
            return
        }

        onPayStart()
        val handler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            onPayEnd(false)
        }
        GlobalScope.launch(handler) {
            val params = mutableMapOf<String, Any>()
            params["goodsId"] = goodsId
            params["payType"] = payType
            params["payPrice"] = payPrice
            params["source"] = source

            withContext(Dispatchers.IO) {
                NetworkApi.service.getVipOrder(params)
            }.let {
                if (it.isSucces()) {
                    toPay(it.data)
                    isPaying = false
                } else {
                    onPayEnd(false)
                }
            }
        }

        UserBehaviorUtil.postToService(
            StatEvent.支付点击, payType,
            mapOf(Pair("payType", payType), Pair("goodsId", goodsId)))
    }

    @Deprecated("Deprecated in Java", ReplaceWith("backOrStartHome()"))
    override fun onBackPressed() {
        // super.onBackPressed()
        backOrStartHome()
    }

    @Subscribe
    fun onAlipayResult(event: AlipayResultEvent) {
        if (event.success) {
            searchOrderStatus()
        }
        onPayEnd(event.success)
    }

    @Subscribe
    fun onWeChatPayResult(event: WxPaySuccessEvent) {
        if (event.errorCode == 0) {
            searchOrderStatus()
        }
        onPayEnd(event.errorCode == 0)
    }

    open fun onPayStart() {
        isPaying = true
    }

    open fun onPayEnd(isSuccess: Boolean) {
        isPaying = false
    }

    private fun searchOrderStatus() {
        GlobalScope.launch(Dispatchers.Main) {
            showLoading("查询订单状态")
            try {
                withTimeout(10 * 1000) {
                    while (true) {
                        val searchOrder = NetworkApi.service.searchOrder(orderId)
                        if (searchOrder.isSucces() && searchOrder.data.status == 2) {
                            paySuccess()
                            break
                        }
                        delay(1000)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                ZZDialogUtil.showMessageDialog(this@CommonRechargeActivity, "订单提示",
                    "订单支付状态查询失败，请退出APP，稍后刷新查看，或者联系客服处理。", "联系客服",
                    object : ZZDialog.OnClickListener {
                        override fun onClick(view: View) {
                            UserInfoManager.getWxServiceInfo()?.let {
                                WechatUtil.openService(view.context, it["cropid"].toString(), it["url"].toString())
                            }
                        }
                    }, "退出APP", object : ZZDialog.OnClickListener {
                        override fun onClick(view: View) {
                            AppUtils.exitApp()
                        }
                    })
            } catch (ignored: Exception) {

            }
            dismissLoading()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterEvent()
    }

    protected open fun paySuccess() {
        ZZToast.showOk("支付成功")
        UserInfoManager.userInfo?.role = VipRole.终身会员.role
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                NetworkApi.service.getUserInfo()
            }.let {
                if (it.isSucces()) {
                    UserInfoManager.saveUserInfo(it.data)
                }
            }
        }
        // 充值成功才需要刷新
        backOrStartHome(true)
    }

    open fun backOrStartHome(needRefresh: Boolean = false) {
        if (needRefresh) {
            EventBus.getDefault().post(UserInfoRefreshEvent())
        }
        val cls = getHomeActivityClass()
        if (!ActivityUtils.isActivityExistsInStack(cls)) {
            ActivityUtils.startActivity(cls)
        }
        finish()
    }

    abstract fun getHomeActivityClass(): Class<out Activity>

    private fun toPay(it: OrderPayEntity) {
        orderId = it.orderId ?: 0
        when (payType) {
            TYPE_WECHAT -> {
                WePayUtil.pay(this, it)
            }
            TYPE_ALIPAY -> {
                it.payParam?.let { payParam ->
                    AlipayUtil.pay(this, payParam)
                }
            }
        }
    }
}