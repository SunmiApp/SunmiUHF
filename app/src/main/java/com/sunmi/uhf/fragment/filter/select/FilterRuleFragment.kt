package com.sunmi.uhf.fragment.filter.select

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunmi.uhf.R
import com.sunmi.uhf.adapter.FilterRuleAdapter
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.Constant
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentFilterListBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.filter.LabelFilterModel
import com.sunmi.uhf.utils.LiveDataBusEvent

/**
 * @ClassName: FilterRuleFragment
 * @Description: 过滤器  选择过滤规则
 * @Author: clh
 * @CreateDate: 20-9-14 上午11:14
 * @UpdateDate: 20-9-14 上午11:14
 */
class FilterRuleFragment : BaseFragment<FragmentFilterListBinding>() {
    lateinit var vm: LabelFilterModel

    private lateinit var adapter: FilterRuleAdapter

    override fun getLayoutResource() = R.layout.fragment_filter_list

    override fun initVM() {
        vm = getViewModel(LabelFilterModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        val rule = arguments?.getInt(Constant.KEY_RULE, 0) ?: 0
        vm.title.value = resources.getString(R.string.filter_rule_text)
        adapter = FilterRuleAdapter()
        adapter.selected = rule
        binding.dataRv.layoutManager = LinearLayoutManager(activity)
        binding.dataRv.adapter = adapter
    }

    override fun initData() {
        vm.createRuleList()
        vm.mRuleList.observe(viewLifecycleOwner, Observer {
            adapter.setNewInstance(it)
        })
        adapter.setOnItemClickListener { _, _, position ->
            adapter.selected = position
            adapter.notifyDataSetChanged()
            activity?.supportFragmentManager?.popBackStackImmediate()
            LiveDataBusEvent.get().with(EventConstant.LABEL_RULE_INDEX, String::class.java)
                .postValue(position.toString())
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
        fun newInstance(args: Bundle?) = FilterRuleFragment()
            .apply { arguments = args }
    }

}