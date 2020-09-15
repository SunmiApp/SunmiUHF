package com.sunmi.uhf.fragment.setting.child

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.bean.CommonListBean
import com.sunmi.uhf.constants.Constant
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentSettingInventoryModeBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.list.ListFragment
import com.sunmi.uhf.fragment.setting.SettingModel
import com.sunmi.uhf.utils.LiveDataBusEvent

/**
 * @ClassName: InventoryModeFragment
 * @Description: 设置  盘存模式选择
 * @Author: clh
 * @CreateDate: 20-9-15 下午3:06
 * @UpdateDate: 20-9-15 下午3:06
 */
class InventoryModeFragment : BaseFragment<FragmentSettingInventoryModeBinding>() {
    lateinit var vm: SettingModel
    override fun getLayoutResource() = R.layout.fragment_setting_inventory_mode

    override fun initVM() {
        vm = getViewModel(SettingModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        vm.title.value = resources.getString(R.string.take_inventory_model_text)
        /* 平衡 模式 */
        binding.balanceLl.modeNameTv.text = resources.getString(R.string.balance_mode_text)
        binding.balanceLl.modeDesTv.text = resources.getString(R.string.balance_mode_tip_text)
        /* 高速 模式 */
        binding.speedLl.modeNameTv.text = resources.getString(R.string.speed_mode_text)
        binding.speedLl.modeDesTv.text = resources.getString(R.string.speed_mode_tip_text)
        /* 遍历器 模式 */
        binding.iteratorLl.modeNameTv.text = resources.getString(R.string.iterator_mode_text)
        binding.iteratorLl.modeDesTv.text = resources.getString(R.string.iterator_mode_tip_text)
    }

    override fun initData() {
        binding.balanceLl.arrowIv.setOnClickListener {
            var flag = binding.balanceLl.modeDetailLl.visibility
            binding.balanceLl.arrowIv.setImageResource(if (flag == View.VISIBLE) R.drawable.arrow_down_icon else R.drawable.arrow_up_icon)
            binding.balanceLl.modeDetailLl.visibility =
                if (flag == View.VISIBLE) View.GONE else View.VISIBLE
        }
        binding.speedLl.arrowIv.setOnClickListener {
            var flag = binding.speedLl.modeDetailLl.visibility
            binding.speedLl.arrowIv.setImageResource(if (flag == View.VISIBLE) R.drawable.arrow_down_icon else R.drawable.arrow_up_icon)
            binding.speedLl.modeDetailLl.visibility =
                if (flag == View.VISIBLE) View.GONE else View.VISIBLE
        }
        binding.iteratorLl.arrowIv.setOnClickListener {
            var flag = binding.iteratorLl.modeDetailLl.visibility
            binding.iteratorLl.arrowIv.setImageResource(if (flag == View.VISIBLE) R.drawable.arrow_down_icon else R.drawable.arrow_up_icon)
            binding.iteratorLl.modeDetailLl.visibility =
                if (flag == View.VISIBLE) View.GONE else View.VISIBLE
        }
        binding.customLl.arrowIv.setOnClickListener {
            var flag = binding.customLl.modeDetailLl.visibility
            binding.customLl.arrowIv.setImageResource(if (flag == View.VISIBLE) R.drawable.arrow_down_icon else R.drawable.arrow_up_icon)
            binding.customLl.modeDetailLl.visibility =
                if (flag == View.VISIBLE) View.GONE else View.VISIBLE
        }
        LiveDataBusEvent.get().with(EventConstant.LABEL_SELECT, CommonListBean::class.java)
            .observe(viewLifecycleOwner, Observer {
                when (it.type) {
                    EventConstant.EVENT_LINK_URL -> vm.mRFLink.value = it.select
                    EventConstant.EVENT_SESSION_SELECT -> vm.mSession.value = it.select
                }
                binding.customLl.arrowIv.performClick()
            })
    }

    override fun onSimpleViewEvent(event: SimpleViewEvent) {
        super.onSimpleViewEvent(event)
        when (event.event) {
            EventConstant.EVENT_BACK -> {
                performBackClick()
            }
            EventConstant.EVENT_LINK_URL -> {
                //射频link
                val args = Bundle().apply {
                    putString(
                        Constant.KEY_TITLE,
                        resources.getString(R.string.setting_rf_link_text)
                    )
                    putStringArrayList(
                        Constant.KEY_LIST,
                        resources.getStringArray(R.array.link_array).toList() as ArrayList<String>
                    )
                    putParcelable(
                        Constant.KEY_SELECT,
                        CommonListBean(
                            type = EventConstant.EVENT_LINK_URL,
                            select = vm.mRFLink.value
                        )
                    )
                }
                switchFragment(
                    ListFragment.newInstance(args),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_SESSION_SELECT -> {
                //目标 session
                val args = Bundle().apply {
                    putString(
                        Constant.KEY_TITLE,
                        resources.getString(R.string.select_session_text)
                    )
                    putStringArrayList(
                        Constant.KEY_LIST,
                        resources.getStringArray(R.array.session_array)
                            .toList() as ArrayList<String>
                    )
                    putParcelable(
                        Constant.KEY_SELECT,
                        CommonListBean(
                            type = EventConstant.EVENT_SESSION_SELECT,
                            select = vm.mSession.value
                        )
                    )
                }
                switchFragment(
                    ListFragment.newInstance(args),
                    addToBackStack = true,
                    clearStack = false
                )
            }
        }
    }

    companion object {
        fun newInstance(args: Bundle?) = InventoryModeFragment()
            .apply { arguments = args }

    }
}