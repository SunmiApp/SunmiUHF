package com.sunmi.uhf.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseDialogFragment

/**
 * @ClassName: SureBackDialog
 * @Description: 快速读取 返回健的  二次确认 弹窗
 * @Author: clh
 * @CreateDate: 20-9-9 上午10:36
 * @UpdateDate: 20-9-9 上午10:36
 */
class SureBackDialog : BaseDialogFragment() {

    /* 退出监听 */
    var listener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_sure_back, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cancelTv = view.findViewById<TextView>(R.id.sure_back_cancel_tv)
        val exitTv = view.findViewById<TextView>(R.id.sure_back_exit_tv)
        cancelTv.setOnClickListener {
            dismiss()
        }
        exitTv.setOnClickListener {
            listener?.invoke()
        }
    }

    companion object {
        fun newInstance(args: Bundle?) = SureBackDialog().apply {
            arguments = args
        }
    }


}