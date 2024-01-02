package com.yic3.lib.base

import androidx.lifecycle.ViewModelStore
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.Utils
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.umeng.commonsdk.UMConfigure
import com.yic3.lib.event.LogoutEvent
import com.yic3.lib.net.NetworkApi
import com.yic3.lib.util.ZZToast
import me.hgj.jetpackmvvm.base.BaseApp
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import com.yic3.lib.R
import com.yic3.lib.ui.LoginActivity

open class BaseApplication : BaseApp() {

    //static 代码段可以防止内存泄露
    init {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(
                R.color.transparent,
                R.color.black63
            )//全局设置主题颜色
            ClassicsHeader(context)//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            layout.setPrimaryColorsId(
                R.color.transparent,
                R.color.black63
            )
            ClassicsFooter(context).also {
                //指定为经典Footer，默认是 BallPulseFooter
                it.setDrawableSize(20f)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
        ZZToast.init(this)
        Utils.init(this)
        UMConfigure.preInit(this, AccountConfig.UM_APP_KEY, NetworkApi.CHANNEL)
    }

    override fun onTerminate() {
        super.onTerminate()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun needReLogin(event: LogoutEvent) {
        ActivityUtils.getTopActivity()?.let {
            ActivityUtils.startActivity(LoginActivity::class.java)
//            if (it is AppCompatActivity) {
//                LoginDialog.show(it.supportFragmentManager)
//            }
        }
    }

}