package com.sunmi.uhf.fragment.filter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.android.material.tabs.TabLayoutMediator
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentLabelFilterBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.filter.tab.TabFilter1Fragment
import com.sunmi.uhf.fragment.filter.tab.TabFilter2Fragment


/**
 * @ClassName: LabelFilterFragmentLabelFilter
 * @Description: 标签过滤 页面
 * @Author: clh
 * @CreateDate: 20-9-11 下午6:29
 * @UpdateDate: 20-9-11 下午6:29
 */
class LabelFilterFragment : BaseFragment<FragmentLabelFilterBinding>() {
    lateinit var vm: LabelFilterModel
    override fun getLayoutResource() = R.layout.fragment_label_filter

    override fun initVM() {
        vm = getViewModel(LabelFilterModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        vm.title.value = resources.getString(R.string.data_filter_text)
        binding.tagViewPager.setPageTransformer(
            MarginPageTransformer(
                resources.getDimensionPixelSize(
                    R.dimen.sunmi_16px
                )
            )
        )
        binding.tagViewPager.offscreenPageLimit = 1
        binding.tagViewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> TabFilter1Fragment.newInstance(null)
                    else -> TabFilter2Fragment.newInstance(null)
                }
            }
        }
        TabLayoutMediator(binding.tabLayout, binding.tagViewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position -> // Styling each tab here
                when (position) {
                    0 -> tab.text = getString(R.string.filter1_text)
                    else -> tab.text = getString(R.string.filter2_text)
                }
            }).attach()
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