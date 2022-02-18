package com.sunmi.uhf.fragment.filter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.android.material.tabs.TabLayoutMediator
import com.sunmi.rfid.RFIDManager
import com.sunmi.rfid.ReaderCall
import com.sunmi.rfid.constant.CMD
import com.sunmi.rfid.constant.ParamCts
import com.sunmi.rfid.entity.DataParameter
import com.sunmi.uhf.BuildConfig
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentLabelFilterBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.filter.tab.TabFilter1Fragment
import com.sunmi.uhf.fragment.filter.tab.TabFilter2Fragment
import com.sunmi.uhf.utils.LiveDataBusEvent
import com.sunmi.uhf.utils.LogUtils


/**
 * @ClassName: LabelFilterFragmentLabelFilter
 * @Description: 标签过滤 页面
 * @Author: clh
 * @CreateDate: 20-9-11 下午6:29
 * @UpdateDate: 20-9-11 下午6:29
 */
class LabelFilterFragment : BaseFragment<FragmentLabelFilterBinding>() {
    lateinit var vm: LabelFilterModel
    val filterMap = HashMap<Byte, DataParameter>()
    private val optCall = object : ReaderCall() {
        override fun onSuccess(cmd: Byte, params: DataParameter?) {
            if (BuildConfig.DEBUG) LogUtils.d("darren", String.format("CMD: 0x%02X, params info: %s", cmd, params?.toString() ?: ""))
            when (cmd) {
                CMD.OPERATE_TAG_MASK -> {
                    params?.let {
                        if (params.getByte(ParamCts.MASK_COUNT, 0) > 0 && params.containsKey(ParamCts.MASK_ID)) {
                            val id = params.getByte(ParamCts.MASK_ID)
                            filterMap[id] = params
                            LiveDataBusEvent.get().with(EventConstant.LABEL_FILTER_DATA, DataParameter::class.java)
                                .postValue(params)
                        }
                    }
                }
            }
        }

        override fun onTag(cmd: Byte, state: Byte, tag: DataParameter?) {
            if (BuildConfig.DEBUG) LogUtils.d(
                "darren",
                "found tag cmd:" + String.format("%%02X", cmd) + ", state: " + String.format("%%02X", state)
                        + ("params info: " + tag?.toString())
            )
        }

        override fun onFailed(cmd: Byte, errorCode: Byte, msg: String?) {
            if (BuildConfig.DEBUG) LogUtils.d(
                "darren",
                "failed cmd: ${String.format("%02X", cmd)}, errorCode: ${String.format("%02X", errorCode)}, msg: $msg"
            )
        }
    }

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
        RFIDManager.getInstance().apply {
            if (isConnect()) {
                getHelper()?.let {
                    it.registerReaderCall(optCall)
                    it.getTagMask()
                }
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        RFIDManager.getInstance().apply {
            if (isConnect()) {
                getHelper()?.unregisterReaderCall()
            }
        }
    }

    companion object {
        fun newInstance(args: Bundle?) = LabelFilterFragment()
            .apply { arguments = args }
    }
}