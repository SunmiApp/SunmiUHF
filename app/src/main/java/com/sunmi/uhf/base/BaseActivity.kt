package com.sunmi.uhf.base

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.sunmi.uhf.App
import com.sunmi.uhf.R
import com.sunmi.widget.util.ToastUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * User: highsixty
 * Date: 2020-04-09 15:16
 * email: gaolulin@sunmi.com
 */
abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {

    lateinit var binding: T
    lateinit var dialog: Dialog
    private val handler = Handler()
    protected val isPadFlag = isPad()
    protected val mainScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayoutResource())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        initLoadingDialog()
        initVM()
        binding.lifecycleOwner = this
        initView()
        initData()
        initBus()
        if (isPadFlag) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            onLandScape()
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            onPortrait()
        }
    }


    /**
     * 判断当前设备是手机还是平板
     * @return 平板返回 True，手机返回 False
     */
    private fun isPad(): Boolean {
        return ((App.mContext.resources.configuration.screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE)
    }

    /**
     * 指定xml文件
     */
    abstract fun getLayoutResource(): Int

    /**
     * 在这个方法中实现viewmodel的初始化
     */
    abstract fun initVM()

    /**
     * 在这个方法里实现databinding与viewmodel的绑定
     */
    abstract fun initView()

    /**
     * 在这个方法里实现数据初始化动作
     */
    abstract fun initData()

    abstract fun onPortrait()

    abstract fun onLandScape()

    /**
     * 注册事件监听事件
     */
    open fun initBus() {}

    /**
     * 切换fragment
     * containerViewId
     * fragmet
     * addToBackStack 是否加入回退栈
     * clearStack 是否清空回退栈
     */
    fun switchFragment(
        fragment: Fragment,
        addToBackStack: Boolean,
        clearStack: Boolean,
        containId: Int = getContainId()
    ) {
        handler.post {
            if (clearStack) {
                if (lifecycle.currentState != Lifecycle.State.RESUMED) {
                    fixBug()
                }
                var r = supportFragmentManager.popBackStackImmediate(
                    null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(containId, fragment, fragment::class.java.name)
            if (addToBackStack) {
                transaction.addToBackStack(fragment::class.java.name)
            }
            transaction.commitAllowingStateLoss()
            supportFragmentManager.executePendingTransactions()
        }

    }

    /**
     * 获取fragment容器id
     */
    abstract fun getContainId(): Int

    /**
     * 预防fragmentmanager在StateLoss的状态下执行popBackStackImmediate抛出异常
     */
    private fun fixBug() {
        try {
            var aClass = FragmentManager::class.java
            var method = aClass.getDeclaredMethod("noteStateNotSaved")
            method.setAccessible(true)
            method.invoke(supportFragmentManager)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 显示toast弹窗
     */
    fun showToast(msg: String?) {
        if (!msg.isNullOrEmpty()) {
            mainScope.launch() {
                ToastUtils.showShort(msg)
            }
        }
    }

    /**
     * 初始化等待层
     */
    private fun initLoadingDialog() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setDimAmount(0.6f)
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.window?.setBackgroundDrawableResource(R.color.transpant)
    }

    /**
     * 展示等待层
     */
    fun showDialog() {
        if (null == dialog) {
            initLoadingDialog()
        }
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    /**
     * 隐藏等待层
     */
    fun hideDialog() {
        if (null != dialog && dialog.isShowing) {
            dialog.dismiss()
        }
    }

    override fun onBackPressed() {
        val backStackEntryCount: Int = supportFragmentManager.backStackEntryCount
        if (backStackEntryCount <= 1) {
            finish()
            return
        }
        val fragment = supportFragmentManager.findFragmentById(getContainId())
        if (fragment is BaseFragment<*>) {
            if (!fragment.onBackPress()) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        mainScope.cancel()
    }
}
