package com.sunmi.uhf.fragment.operation.tab

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.Constant
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.TabReadWriteBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.location.LabelLocationFragment
import com.sunmi.uhf.fragment.operation.LabelOperationFragment
import com.sunmi.uhf.fragment.operation.LabelOperationModel
import com.sunmi.uhf.fragment.operation.select.OperationAreaFragment
import com.sunmi.uhf.utils.LiveDataBusEvent

/**
 * @ClassName: TabReadFragment
 * @Description: 标签操作页面 第一个 tab 读取写入
 * @Author: clh
 * @CreateDate: 20-9-11 下午3:48
 * @UpdateDate: 20-9-11 下午3:48
 */
class TabReadFragment : BaseFragment<TabReadWriteBinding>() {
    lateinit var vm: LabelOperationModel
    override fun getLayoutResource() = R.layout.tab_read_write

    override fun initVM() {
        vm = getViewModel(LabelOperationModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        if (vm.areaData.value == null) {
            vm.areaData.value = "EPC"
        }
    }

    override fun initData() {
        LiveDataBusEvent.get().with(EventConstant.LABEL_OPERATION_AREA, String::class.java)
            .observe(viewLifecycleOwner, Observer {
                vm.areaData.value = it
            })
    }

    override fun onSimpleViewEvent(event: SimpleViewEvent) {
        super.onSimpleViewEvent(event)
        when (event.event) {
            EventConstant.EVENT_OPERATION_AREA -> {
                //操作区域
                val args = Bundle().apply { putString(Constant.KEY_AREA, vm.areaData.value) }
                switchFragment(
                    OperationAreaFragment.newInstance(args),
                    addToBackStack = true,
                    clearStack = false
                )
            }
        }
    }


    companion object {
        fun newInstance(args: Bundle?) = TabReadFragment()
            .apply { arguments = args }
    }
}