package com.sunmi.uhf.fragment.setting.child

import android.os.Bundle
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentAreaSettingBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.setting.SettingModel

/**
 * @ClassName: AreaSettingFragment
 * @Description: 设置，区域设置
 * @Author: clh
 * @CreateDate: 20-9-14 下午5:10
 * @UpdateDate: 20-9-14 下午5:10
 */
class AreaSettingFragment : BaseFragment<FragmentAreaSettingBinding>() {
    lateinit var vm: SettingModel
    override fun getLayoutResource() = R.layout.fragment_area_setting

    override fun initVM() {
        vm = getViewModel(SettingModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        vm.title.value = resources.getString(R.string.setting_select_area_text)
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
        fun newInstance(args: Bundle?) = AreaSettingFragment()
            .apply { arguments = args }
    }
}