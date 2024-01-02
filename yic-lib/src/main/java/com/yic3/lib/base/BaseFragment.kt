package com.yic3.lib.base

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.LogUtils
import me.hgj.jetpackmvvm.base.fragment.BaseVmDbFragment
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

abstract class BaseFragment<VM : BaseViewModel, DB: ViewDataBinding> : BaseVmDbFragment<VM, DB>() {

    protected var hasViewCreated = false // 视图是否已加载
    protected var isFirstLoad = false // 是否首次加载

    private var loadingDialog: DialogFragment? = null // 加载等待对话框

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hasViewCreated = true
        isFirstLoad = true
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * setLoadingDialog
     * 设置自定义的加载等待对话框
     * @param view 自定义网络等待view
     * @author zhang
     */
    protected fun setLoadingDialog(view: View?) {
        if (view != null && loadingDialog != null) {
            loadingDialog!!.dialog?.setContentView(view)
        }
    }

    /**
     * initDialog
     * 初始化对话框，子类可重写该方法，与dismiss对应
     * @author zhang
     */
    private fun initDialog() {
        // 重用持有Activity的加载对话框
        if (loadingDialog == null) {
            loadingDialog = LoadingDialogFragment()
        }
    }

    fun getLoadingDialog(): DialogFragment? {
        if (this.loadingDialog == null) {
            initDialog()
        }
        return this.loadingDialog
    }

    override fun onResume() {
        super.onResume()
        if (!isHidden) {
            onFragmentRefresh()
        }
    }

    protected open fun onFragmentRefresh() {
        try {
            if (isFirstLoad && userVisibleHint) {
                isFirstLoad = false
                onViewPagerLazyLoad()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected open fun onViewPagerLazyLoad() {

    }

    @Deprecated("Deprecated in Java")
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            onFragmentRefresh()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            onFragmentRefresh()
        }
    }

    override fun showLoading(message: String) {
        initDialog()
        loadingDialog?.let {
            LogUtils.e("show home fragment loading")
            if (it is LoadingDialogFragment) {
                // it.tips = message
            }
            try {
                if (!it.isAdded) {
                    it.show(childFragmentManager, "loading")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun dismissLoading() {
        try {
            if (loadingDialog?.isAdded == true) {
                loadingDialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun lazyLoadData() {

    }

}