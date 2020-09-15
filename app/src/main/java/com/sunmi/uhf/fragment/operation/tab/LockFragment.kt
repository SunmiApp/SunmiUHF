package com.sunmi.uhf.fragment.operation.tab

import android.os.Bundle
import androidx.lifecycle.Observer
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.bean.CommonListBean
import com.sunmi.uhf.constants.Constant
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.TabLockBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.list.ListFragment
import com.sunmi.uhf.fragment.operation.LabelOperationModel
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
        LiveDataBusEvent.get().with(EventConstant.LABEL_SELECT, CommonListBean::class.java)
            .observe(viewLifecycleOwner, Observer {
                when(it.type){
                    EventConstant.EVENT_OPERATION_AREA ->      vm.areaData.value = it.select
                    EventConstant.EVENT_OPERATION_TYPE ->      vm.typeData.value = it.select
                }
            })
    }

    override fun onSimpleViewEvent(event: SimpleViewEvent) {
        super.onSimpleViewEvent(event)
        when (event.event) {
            EventConstant.EVENT_OPERATION_AREA -> {
                //操作区域
                val args = Bundle().apply {
                    putString(
                        Constant.KEY_TITLE,
                        resources.getString(R.string.select_operation_area_text)
                    )
                    putStringArrayList(
                        Constant.KEY_LIST,
                        resources.getStringArray(R.array.area_array).toList() as ArrayList<String>
                    )
                    putParcelable(
                        Constant.KEY_SELECT,
                        CommonListBean(
                            type = EventConstant.EVENT_OPERATION_AREA,
                            select = vm.areaData.value
                        )
                    )
                }
                switchFragment(
                    ListFragment.newInstance(args),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_OPERATION_TYPE -> {
                //操作区域
                val args = Bundle().apply {
                    putString(
                        Constant.KEY_TITLE,
                        resources.getString(R.string.set_operation_type_text)
                    )
                    putStringArrayList(
                        Constant.KEY_LIST,
                        resources.getStringArray(R.array.type_array).toList() as ArrayList<String>
                    )
                    putParcelable(
                        Constant.KEY_SELECT,
                        CommonListBean(
                            type = EventConstant.EVENT_OPERATION_TYPE,
                            select = vm.typeData.value
                        )
                    )
                }
                switchFragment(
                    ListFragment.newInstance(args),
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