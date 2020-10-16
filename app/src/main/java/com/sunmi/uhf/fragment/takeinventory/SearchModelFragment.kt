package com.sunmi.uhf.fragment.takeinventory

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunmi.uhf.R
import com.sunmi.uhf.adapter.LabelInfoAdapter
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.bean.LabelInfoBean
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentSearchBinding
import com.sunmi.uhf.event.SimpleViewEvent

/**
 * @ClassName: SearchModelFragment
 * @Description: 搜索模式选择
 * @Author: clh
 * @CreateDate: 20-9-10 下午1:57
 * @UpdateDate: 20-9-10 下午1:57
 */
class SearchModelFragment : BaseFragment<FragmentSearchBinding>() {

    lateinit var vm: TakeInventoryModel
    lateinit var adapter: LabelInfoAdapter
    private val list = mutableListOf<LabelInfoBean>()

    override fun getLayoutResource() = R.layout.fragment_search

    override fun initVM() {
        vm = getViewModel(TakeInventoryModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        adapter = LabelInfoAdapter()
        binding.labelRv.layoutManager = LinearLayoutManager(activity)
        binding.labelRv.adapter = adapter
    }

    override fun initData() {
        vm.filterLabelList.value = list
        vm.filterLabelList.observe(viewLifecycleOwner, Observer {
            adapter.setNewInstance(it)
        })
        vm.editModel.observe(viewLifecycleOwner, Observer {
            adapter?.editable = it
            adapter?.notifyDataSetChanged()
        })
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
        fun newInstance(args: Bundle?) = SearchModelFragment()
            .apply { arguments = args }
    }
}