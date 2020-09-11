package com.sunmi.uhf.fragment.operation.select

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunmi.uhf.R
import com.sunmi.uhf.adapter.OperationAdapter
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.Constant
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentOperationAreaBinding
import com.sunmi.uhf.databinding.FragmentOperationTypeBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.operation.LabelOperationModel
import com.sunmi.uhf.utils.LiveDataBusEvent

/**
 * @ClassName: OperationAreaFragment
 * @Description: 设置操作方式 选择
 * @Author: clh
 * @CreateDate: 20-9-11 下午4:40
 * @UpdateDate: 20-9-11 下午4:40
 */
class OperationTypeFragment : BaseFragment<FragmentOperationTypeBinding>() {
    lateinit var vm: LabelOperationModel
    private lateinit var adapter: OperationAdapter

    override fun getLayoutResource() = R.layout.fragment_operation_type

    override fun initVM() {
        vm = getViewModel(LabelOperationModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        val type = arguments?.getString(Constant.KEY_TYPE, "") ?: ""
        adapter = OperationAdapter()
        adapter.selected = type
        binding.dataRv.layoutManager = LinearLayoutManager(activity)
        binding.dataRv.adapter = adapter
    }

    override fun initData() {
        vm.createOperationTypeList()
        vm.operationTypeList.observe(viewLifecycleOwner, Observer {
            adapter.setNewInstance(it)
        })
        adapter.setOnItemClickListener { _, _, position ->
            val type = vm.operationTypeList.value?.get(position)
            if (type.isNullOrEmpty()) return@setOnItemClickListener
            adapter.selected = type
            adapter?.notifyDataSetChanged()
            activity?.supportFragmentManager?.popBackStackImmediate()
            LiveDataBusEvent.get().with(EventConstant.LABEL_OPERATION_TYPE, String::class.java)
                .postValue(type)

        }
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
        fun newInstance(args: Bundle?) = OperationTypeFragment()
            .apply { arguments = args }
    }
}