package com.sunmi.uhf.fragment.setting.child

import android.os.Bundle
import android.view.View
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentSettingHandleSelectBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.setting.SettingModel

/**
 * @ClassName: HandleSelectFragment
 * @Description: 设置   手柄选择
 * @Author: clh
 * @CreateDate: 20-9-15 下午4:59
 * @UpdateDate: 20-9-15 下午4:59
 */
class HandleSelectFragment : BaseFragment<FragmentSettingHandleSelectBinding>() {
    lateinit var vm: SettingModel
    override fun getLayoutResource() = R.layout.fragment_setting_handle_select

    override fun initVM() {
        vm = getViewModel(SettingModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        vm.title.value = resources.getString(R.string.setting_select_handle_text)
        binding.oneLl.itemNameTv.text = resources.getString(R.string.handle_type_1_text)
        binding.twoLl.itemNameTv.text = resources.getString(R.string.handle_type_2_text)
    }

    override fun initData() {
        binding.oneLl.arrowIv.setOnClickListener {
            var flag = binding.oneLl.modeDetailLl.visibility
            binding.oneLl.arrowIv.setImageResource(if (flag == View.VISIBLE) R.drawable.arrow_down_icon else R.drawable.arrow_up_icon)
            binding.oneLl.modeDetailLl.visibility =
                if (flag == View.VISIBLE) View.GONE else View.VISIBLE
        }
        binding.twoLl.arrowIv.setOnClickListener {
            var flag = binding.twoLl.modeDetailLl.visibility
            binding.twoLl.arrowIv.setImageResource(if (flag == View.VISIBLE) R.drawable.arrow_down_icon else R.drawable.arrow_up_icon)
            binding.twoLl.modeDetailLl.visibility =
                if (flag == View.VISIBLE) View.GONE else View.VISIBLE
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

    companion object {
        fun newInstance(args: Bundle?) = HandleSelectFragment()
            .apply { arguments = args }

    }
}