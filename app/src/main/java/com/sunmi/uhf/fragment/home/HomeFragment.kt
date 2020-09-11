package com.sunmi.uhf.fragment.home

import android.os.Bundle
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentHomeBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.filter.LabelFilterFragment
import com.sunmi.uhf.fragment.location.LabelLocationFragment
import com.sunmi.uhf.fragment.operation.LabelOperationFragment
import com.sunmi.uhf.fragment.readwrite.ReadWriteFragment
import com.sunmi.uhf.fragment.setting.SettingFragment
import com.sunmi.uhf.fragment.takeinventory.TakeInventoryFragment

/**
 * @ClassName: HomeFragment
 * @Description: 首页
 * @Author: clh
 * @CreateDate: 20-9-7 下午3:27
 * @UpdateDate: 20-9-7 下午3:27
 */
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    lateinit var vm: HomeViewMode

    override fun getLayoutResource() = R.layout.fragment_home

    override fun initVM() {
        vm = getViewModel(HomeViewMode::class.java)
    }

    override fun initView() {
        binding.vm = vm
    }

    override fun initData() {
    }

    override fun onSimpleViewEvent(event: SimpleViewEvent) {
        super.onSimpleViewEvent(event)
        when (event.event) {
            EventConstant.EVENT_FAST_READ_WRITE -> {
                //快速读取
                switchFragment(
                    ReadWriteFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_TAKE_INVENTORY -> {
                //盘存
                switchFragment(
                    TakeInventoryFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_LABEL_OPERATION -> {
                //标签操作存页
                switchFragment(
                    LabelOperationFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_LABEL_LOCATION -> {
                //标签定位
                switchFragment(
                    LabelLocationFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_LABEL_FILTER -> {
                //标签过滤
                switchFragment(
                    LabelFilterFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_SETTING -> {
                //setting
                switchFragment(
                    SettingFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
        }
    }


    companion object {
        fun newInstance(args: Bundle?) = HomeFragment()
            .apply { arguments = args }
    }
}