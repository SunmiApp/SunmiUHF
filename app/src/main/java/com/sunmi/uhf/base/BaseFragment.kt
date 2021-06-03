package com.sunmi.uhf.base

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.sunmi.uhf.event.LoadingDialogEvent
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.event.ViewEvent
import com.sunmi.widget.util.ToastUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel


/**
 * User: highsixty
 * Date: 2020-04-09 16:38
 * email: gaolulin@sunmi.com
 */
abstract class BaseFragment<T : ViewDataBinding> : Fragment() {

    private var dataBinding: T? = null
    protected val mainScope = MainScope()
    protected val handler = Handler()
    val binding: T
        get() {
            if (dataBinding == null)
                throw RuntimeException("请检查是否在OnDestroyView之后使用binding对象")
            return dataBinding!!
        }

    private val viewModels = mutableListOf<BaseViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, getLayoutResource(), container, false)
        return dataBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initVM()
        initVMEvent()
        dataBinding?.lifecycleOwner = viewLifecycleOwner
        initView()
        initData()
        initBus()
    }

    fun showShort(@StringRes resStr: Int) {
        if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
            ToastUtils.showShort(resStr)
        }
    }

    fun showShort(str: String) {
        if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
            ToastUtils.showShort(str)
        }
    }

    fun showLong(@StringRes resStr: Int) {
        if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
            ToastUtils.showLong(resStr)
        }
    }

    fun showLong(str: String) {
        if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
            ToastUtils.showLong(str)
        }
    }

    override fun onStop() {
        super.onStop()
        ToastUtils.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModels.clear()
        hideDialog()
        dataBinding = null
        handler.removeCallbacksAndMessages(null)
        ToastUtils.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }

    fun <T : BaseViewModel> getViewModel(modelClass: Class<T>): T {
        return getViewModelInternal(this, modelClass)
    }

    fun <T : BaseViewModel> getViewModelFromActivity(modelClass: Class<T>): T {
        val owner: ViewModelStoreOwner = activity ?: this
        return getViewModelInternal(owner, modelClass)
    }

    private fun <T : BaseViewModel> getViewModelInternal(
        owner: ViewModelStoreOwner,
        modelClass: Class<T>
    ): T {
        val viewModel = ViewModelProvider(owner).get(modelClass)
        viewModels.add(viewModel)
        viewModel.viewEvents.observe(viewLifecycleOwner, Observer {
            handlerEventObserver(it)
        })
        return viewModel
    }

    private fun initVMEvent() {
        viewModels.forEach { it.initEvent() }
    }

    /**
     * 统一处理所有ViewEvent事件
     */
    open fun handlerEventObserver(event: ViewEvent?) {
        when (event) {
            is LoadingDialogEvent -> {
                if (event.loading) {
                    showDialog()
                } else {
                    hideDialog()
                }
            }
            is SimpleViewEvent -> {
                onSimpleViewEvent(event)
            }
            else -> {
                onViewEvent(event)
            }
        }
    }


    /**
     * 处理SimpleViewEvent 事件
     */
    open fun onSimpleViewEvent(event: SimpleViewEvent) {

    }

    /**
     * 处理等待层、SimpleViewEvent事件意外的事件
     */
    open fun onViewEvent(event: ViewEvent?) {

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

    /**
     * 注册事件监听事件
     */
    open fun initBus() {}


    /**
     * fragment 内部切换fragment
     */
    fun switchFragment(containerViewId: Int, fragment: Fragment) {
        handler.post {
            var transaction = childFragmentManager.beginTransaction()
            transaction.replace(containerViewId, fragment, fragment::class.java.name)
            transaction.commitAllowingStateLoss()
            childFragmentManager.executePendingTransactions()
        }
    }

    /**
     * fragment调用baseactivity切换framgent
     */
    fun switchFragment(
        fragment: Fragment,
        addToBackStack: Boolean = true,
        clearStack: Boolean = false
    ) {
        activity?.let {
            (it as BaseActivity<*>)
            it.switchFragment(
                fragment,
                addToBackStack,
                clearStack
            )
        }

    }

    open fun onBackPress(): Boolean = false

    fun performBackClick() {
        handler.post { activity?.supportFragmentManager?.popBackStackImmediate() }
    }

    fun showDialog() {
        (activity as BaseActivity<*>).showDialog()
    }

    fun hideDialog() {
        (activity as BaseActivity<*>).hideDialog()
    }

    /**
     * 隐藏软键盘
     */
    fun hideSoftInput() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity?.window?.decorView?.windowToken, 0)
    }

}
