package com.sunmi.uhf.fragment.operation.select

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunmi.uhf.R
import com.sunmi.uhf.adapter.OperationAdapter
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.Constant
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentOperationAreaBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.operation.LabelOperationModel
import com.sunmi.uhf.utils.LiveDataBusEvent

/**
 * @ClassName: OperationAreaFragment
 * @Description: 操作区域 选择
 * @Author: clh
 * @CreateDate: 20-9-11 下午4:40
 * @UpdateDate: 20-9-11 下午4:40
 */
class OperationAreaFragment : BaseFragment<FragmentOperationAreaBinding>() {
    lateinit var vm: LabelOperationModel

    private lateinit var adapter: OperationAdapter
    override fun getLayoutResource() = R.layout.fragment_operation_area

    override fun initVM() {
        vm = getViewModel(LabelOperationModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        val area = arguments?.getString(Constant.KEY_AREA, "") ?: ""
        adapter = OperationAdapter()
        adapter.selected = area
        binding.dataRv.layoutManager = LinearLayoutManager(activity)
        binding.dataRv.adapter = adapter
    }

    override fun initData() {
        vm.createOperationAreaList()
        vm.operationAreaList.observe(viewLifecycleOwner, Observer {
            adapter.setNewInstance(it)
        })
        adapter.setOnItemClickListener { _, _, position ->
            val area = vm.operationAreaList.value?.get(position)
            if (area.isNullOrEmpty()) return@setOnItemClickListener
            adapter.selected = area
            adapter?.notifyDataSetChanged()
            Log.i("xxx", "area:$area")
            activity?.supportFragmentManager?.popBackStackImmediate()
            LiveDataBusEvent.get().with(EventConstant.LABEL_OPERATION_AREA, String::class.java)
                .postValue(area)


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
        fun newInstance(args: Bundle?) = OperationAreaFragment()
            .apply { arguments = args }
    }
}