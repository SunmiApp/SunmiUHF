package com.sunmi.uhf.fragment.setting

import android.os.Bundle
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentLabelFilterBinding
import com.sunmi.uhf.databinding.FragmentSearchBinding
import com.sunmi.uhf.databinding.FragmentSettingBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.filter.LabelFilterFragment
import com.sunmi.uhf.fragment.filter.LabelFilterModel
import com.sunmi.uhf.fragment.location.LabelLocationModel

/**
 * @ClassName: LabelFilterFragmentLabelFilter
 * @Description: uhf 设置页面
 * @Author: clh
 * @CreateDate: 20-9-11 下午6:29
 * @UpdateDate: 20-9-11 下午6:29
 */
class SettingFragment : BaseFragment<FragmentSettingBinding>() {
    lateinit var vm: SettingModel
    override fun getLayoutResource() = R.layout.fragment_setting

    override fun initVM() {
        vm = getViewModel(SettingModel::class.java)
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
        fun newInstance(args: Bundle?) = SettingFragment()
            .apply { arguments = args }
    }
}