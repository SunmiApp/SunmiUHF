package com.sunmi.uhf.fragment.filter

import android.os.Bundle
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentLabelFilterBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.location.LabelLocationFragment
import com.sunmi.uhf.fragment.location.LabelLocationModel

/**
 * @ClassName: LabelFilterFragmentLabelFilter
 * @Description: java类作用描述
 * @Author: clh
 * @CreateDate: 20-9-11 下午6:29
 * @UpdateDate: 20-9-11 下午6:29
 */
class LabelFilterFragment:BaseFragment<FragmentLabelFilterBinding>() {
    lateinit var vm: LabelFilterModel
    override fun getLayoutResource()= R.layout.fragment_label_filter

    override fun initVM() {
        vm = getViewModel(LabelFilterModel::class.java)
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
        fun newInstance(args: Bundle?) = LabelFilterFragment()
            .apply { arguments = args }
    }
}