package com.sunmi.uhf.fragment.operation.tab

import android.os.Bundle
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.TabDestroyBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.operation.LabelOperationModel

/**
 * @ClassName: DestoryFragment
 * @Description: 标签操作页面 第三个 tab 销毁
 * @Author: clh
 * @CreateDate: 20-9-11 下午3:52
 * @UpdateDate: 20-9-11 下午3:52
 */
class DestroyFragment : BaseFragment<TabDestroyBinding>() {
    lateinit var vm: LabelOperationModel
    override fun getLayoutResource() = R.layout.tab_destroy

    override fun initVM() {
        vm = getViewModel(LabelOperationModel::class.java)
        binding.vm = vm
    }

    override fun initView() {

    }

    override fun initData() {

    }

    override fun onSimpleViewEvent(event: SimpleViewEvent) {
        super.onSimpleViewEvent(event)
        when (event.event) {
            EventConstant.EVENT_BACK -> {
                performBackClick()
            }
        }

    }


    companion object {
        fun newInstance(args: Bundle?) = DestroyFragment()
            .apply { arguments = args }
    }
}