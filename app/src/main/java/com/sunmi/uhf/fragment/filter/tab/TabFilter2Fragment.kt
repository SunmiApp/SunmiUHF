package com.sunmi.uhf.fragment.filter.tab

import android.os.Bundle
import androidx.lifecycle.Observer
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.bean.CommonListBean
import com.sunmi.uhf.constants.Constant
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.LayoutTabFilterBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.filter.LabelFilterModel
import com.sunmi.uhf.fragment.filter.select.FilterRuleFragment
import com.sunmi.uhf.fragment.list.ListFragment
import com.sunmi.uhf.utils.LiveDataBusEvent

/**
 * @ClassName: TabFilter1Fragment
 * @Description: 标签过滤 ，过滤器2
 * @Author: clh
 * @CreateDate: 20-9-14 上午9:59
 * @UpdateDate: 20-9-14 上午9:59
 */
class TabFilter2Fragment : BaseFragment<LayoutTabFilterBinding>() {

    lateinit var vm: LabelFilterModel

    override fun getLayoutResource() = R.layout.layout_tab_filter

    override fun initVM() {
        vm = getViewModel(LabelFilterModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        vm.mBlockData.value = "EPC"
        vm.mFilterRuleData.value = "0"
        vm.mTargetData.value = "S0"
    }

    override fun initData() {
        LiveDataBusEvent.get().with(EventConstant.LABEL_RULE_INDEX, String::class.java)
            .observe(viewLifecycleOwner, Observer {

                vm.mFilterRuleData.value = it
            })
        LiveDataBusEvent.get().with(EventConstant.LABEL_SELECT, CommonListBean::class.java)
            .observe(viewLifecycleOwner, Observer {
                when (it.type) {
                    EventConstant.EVENT_BLOCK_CLICK -> vm.mBlockData.value = it.select
                    EventConstant.EVENT_TARGET_CLICK -> vm.mTargetData.value = it.select
                }
            })
    }

    override fun onSimpleViewEvent(event: SimpleViewEvent) {
        super.onSimpleViewEvent(event)
        when (event.event) {
            EventConstant.EVENT_BLOCK_CLICK -> {
                //操作区域
                val args = Bundle().apply {
                    putString(
                        Constant.KEY_TITLE,
                        resources.getString(R.string.select_operation_area_text)
                    )
                    putStringArrayList(
                        Constant.KEY_LIST,
                        resources.getStringArray(R.array.area_read_write_array).toList() as ArrayList<String>
                    )
                    putParcelable(
                        Constant.KEY_SELECT,
                        CommonListBean(
                            type = EventConstant.EVENT_BLOCK_CLICK,
                            select = vm.mTargetData.value
                        )
                    )
                }
                switchFragment(
                    ListFragment.newInstance(args),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_FILTER_RULE -> {
                //过滤规则
                val args = Bundle().apply {
                    putInt(Constant.KEY_RULE, vm.mFilterRuleData.value?.toInt() ?: 0)
                }
                switchFragment(
                    FilterRuleFragment.newInstance(args),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_TARGET_CLICK -> {
                //目标 session
                val args = Bundle().apply {
                    putString(Constant.KEY_TITLE, resources.getString(R.string.select_session_text))
                    putStringArrayList(
                        Constant.KEY_LIST,
                        resources.getStringArray(R.array.session_array)
                            .toList() as ArrayList<String>
                    )
                    putParcelable(
                        Constant.KEY_SELECT,
                        CommonListBean(
                            type = EventConstant.EVENT_TARGET_CLICK,
                            select = vm.mTargetData.value
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
        fun newInstance(args: Bundle?) = TabFilter2Fragment()
            .apply { arguments = args }
    }
}