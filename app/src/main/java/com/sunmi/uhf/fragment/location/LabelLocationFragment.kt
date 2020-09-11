package com.sunmi.uhf.fragment.location

import android.os.Bundle
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentLabelLocationBinding
import com.sunmi.uhf.event.SimpleViewEvent


/**
 * @ClassName: LabelLocationFragment
 * @Description: 标签定位 页面
 * @Author: clh
 * @CreateDate: 20-9-10 下午2:50
 * @UpdateDate: 20-9-10 下午2:50
 */
class LabelLocationFragment : BaseFragment<FragmentLabelLocationBinding>() {
    lateinit var vm: LabelLocationModel

    override fun getLayoutResource() = R.layout.fragment_label_location

    override fun initVM() {
        vm = getViewModel(LabelLocationModel::class.java)
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
        fun newInstance(args: Bundle?) = LabelLocationFragment()
            .apply { arguments = args }
    }
}