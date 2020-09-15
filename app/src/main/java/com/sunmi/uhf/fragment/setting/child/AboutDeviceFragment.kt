package com.sunmi.uhf.fragment.setting.child

import android.os.Bundle
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentAboutDeviceBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.setting.SettingModel

/**
 * @ClassName: AboutDeviceFragment
 * @Description: 设置  关于设备
 * @Author: clh
 * @CreateDate: 20-9-14 下午5:23
 * @UpdateDate: 20-9-14 下午5:23
 */
class AboutDeviceFragment : BaseFragment<FragmentAboutDeviceBinding>() {
    lateinit var vm: SettingModel
    override fun getLayoutResource() = R.layout.fragment_about_device

    override fun initVM() {
        vm = getViewModel(SettingModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        vm.title.value = resources.getString(R.string.setting_about_device_text)
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
        fun newInstance(args: Bundle?) = AboutDeviceFragment()
            .apply { arguments = args }
    }
}