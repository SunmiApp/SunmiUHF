package com.sunmi.uhf.fragment.operation.tab

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.Constant
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.TabLockBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.operation.LabelOperationModel
import com.sunmi.uhf.fragment.operation.select.OperationAreaFragment
import com.sunmi.uhf.fragment.operation.select.OperationTypeFragment
import com.sunmi.uhf.utils.LiveDataBusEvent

/**
 * @ClassName: LockFragment
 * @Description: 标签操作页面 第二个 tab 锁定
 * @Author: clh
 * @CreateDate: 20-9-11 下午3:52
 * @UpdateDate: 20-9-11 下午3:52
 */
class LockFragment : BaseFragment<TabLockBinding>() {
    lateinit var vm: LabelOperationModel
    override fun getLayoutResource() = R.layout.tab_lock

    override fun initVM() {
        vm = getViewModel(LabelOperationModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        if (vm.areaData.value == null) {
            vm.areaData.value = "EPC"
        }
        if (vm.typeData.value == null) {
            vm.typeData.value = "开放"
        }
    }

    override fun initData() {
        LiveDataBusEvent.get().with(EventConstant.LABEL_OPERATION_AREA, String::class.java)
            .observe(viewLifecycleOwner, Observer {
                vm.areaData.value = it
            })
        LiveDataBusEvent.get().with(EventConstant.LABEL_OPERATION_TYPE, String::class.java)
            .observe(viewLifecycleOwner, Observer {
                vm.typeData.value = it
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
            EventConstant.EVENT_OPERATION_TYPE -> {
                //操作区域
                val args = Bundle().apply { putString(Constant.KEY_TYPE, vm.typeData.value) }
                switchFragment(
                    OperationTypeFragment.newInstance(args),
                    addToBackStack = true,
                    clearStack = false
                )
            }
        }
    }


    companion object {
        fun newInstance(args: Bundle?) = LockFragment()
            .apply { arguments = args }
    }
}