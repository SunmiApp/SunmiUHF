package com.sunmi.uhf.base

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.sunmi.uhf.R
import com.sunmi.uhf.event.LoadingDialogEvent
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.event.ViewEvent


/**
 * User: highsixty
 * Date: 2020/5/8 13:49
 * email: gaolulin@sunmi.com
 * 统一实现弹窗隐藏状态栏、设置默认透明背景
 */
open class BaseDialogFragment : DialogFragment() {
    /**
     * DialogFragment隐藏监听
     */
    var onDismissListener: (() -> Unit)? = null

    val viewModels = mutableListOf<BaseViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }

    fun <T : BaseViewModel> getViewModel(modelClass: Class<T>): T {
        return getViewModelInternal(this, modelClass)
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

    fun showDialog() {
        (activity as BaseActivity<*>).showDialog()
    }

    fun hideDialog() {
        (activity as BaseActivity<*>).hideDialog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModels.clear()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
    }

    /**
     * 隐藏软键盘
     */
    fun hideSoftInput() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(dialog?.window?.decorView?.windowToken, 0)
    }
}