package com.sunmi.uhf.fragment.operation

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.android.material.tabs.TabLayoutMediator
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentLabelOperationBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.operation.tab.DestroyFragment
import com.sunmi.uhf.fragment.operation.tab.LockFragment
import com.sunmi.uhf.fragment.operation.tab.TabReadFragment

/**
 * @ClassName: LabelOperationFragment
 * @Description: 标签操作
 * @Author: clh
 * @CreateDate: 20-9-11 下午3:12
 * @UpdateDate: 20-9-11 下午3:12
 */
class LabelOperationFragment : BaseFragment<FragmentLabelOperationBinding>() {
    lateinit var vm: LabelOperationModel
    override fun getLayoutResource() = R.layout.fragment_label_operation

    override fun initVM() {
        vm = getViewModel(LabelOperationModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        binding.tagViewPager.setPageTransformer(
            MarginPageTransformer(
                resources.getDimensionPixelSize(
                    R.dimen.sunmi_16px
                )
            )
        )
        binding.tagViewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 3

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> TabReadFragment.newInstance(null)
                    1 -> LockFragment.newInstance(null)
                    else -> DestroyFragment.newInstance(null)
                }
            }
        }
        TabLayoutMediator(binding.tabLayout, binding.tagViewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position -> // Styling each tab here
                when (position) {
                    0 -> tab.text = getString(R.string.read_write_text)
                    1 -> tab.text = getString(R.string.lock_text)
                    else -> tab.text = getString(R.string.destory_text)
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
        fun newInstance(args: Bundle?) = LabelOperationFragment()
            .apply { arguments = args }
    }
}