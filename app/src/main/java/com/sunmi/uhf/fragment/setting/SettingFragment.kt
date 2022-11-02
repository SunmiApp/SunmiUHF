package com.sunmi.uhf.fragment.setting

import android.os.Bundle
import androidx.lifecycle.Observer
import com.sunmi.rfid.RFIDManager
import com.sunmi.uhf.App
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.bean.CommonListBean
import com.sunmi.uhf.constants.Config
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentSettingBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.setting.child.*
import com.sunmi.uhf.utils.LiveDataBusEvent

/**
 * @ClassName: LabelFilterFragmentLabelFilter
 * @Description: uhf 设置页面
 * @Author: clh
 * @CreateDate: 20-9-11 下午6:29
 * @UpdateDate: 20-9-11 下午6:29
 */
class SettingFragment : BaseFragment<FragmentSettingBinding>() {
    lateinit var vm: SettingModel
    override fun getLayoutResource() = R.layout.fragment_setting

    override fun initVM() {
        vm = getViewModel(SettingModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        var index = App.getPref().getParam(Config.KEY_LABEL, Config.DEF_LABEL)
        vm.labelName.value = resources.getStringArray(R.array.label_array)[index]
    }

    override fun initData() {
        LiveDataBusEvent.get().with(EventConstant.LABEL_SELECT, CommonListBean::class.java)
            .observe(viewLifecycleOwner, Observer {
                vm.labelName.value = it.select
                App.getPref().setParam(Config.KEY_LABEL, it.index ?: Config.DEF_LABEL)
            })
        RFIDManager.getInstance().apply {
            if (isConnect()) {
                when (getHelper()?.getScanModel()) {
                    RFIDManager.UHF_R2000, RFIDManager.UHF_S7100 -> {
                        vm.isInner.postValue(false)
                    }
                    RFIDManager.INNER -> {
                        vm.isInner.postValue(true)
                    }
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
            EventConstant.EVENT_SELECT_LABEL -> {
                //选择标签
                /*val args = Bundle().apply {
                    putString(
                            Constant.KEY_TITLE,
                            resources.getString(R.string.setting_select_label_text)
                    )
                    putStringArrayList(
                            Constant.KEY_LIST,
                            resources.getStringArray(R.array.label_array)
                                    .toList() as ArrayList<String>
                    )
                    putParcelable(
                            Constant.KEY_SELECT,
                            CommonListBean(
                                    type = EventConstant.EVENT_TARGET_CLICK,
                                    select = vm.labelName.value
                            )
                    )
                }
                switchFragment(
                        ListFragment.newInstance(args),
                        addToBackStack = true,
                        clearStack = false
                )*/
            }
            EventConstant.EVENT_SELECT_HANDLE -> {
                // TODO: 20-9-14 手柄选择
                //区域设置
                switchFragment(
                    HandleSelectFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_INVENTORY_MODE -> {
                // TODO: 20-9-14 盘存模式选择
                //区域设置
                switchFragment(
                    InventoryModeFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_AREA_SETTING -> {
                // TODO: 20-9-14 区域设置
                //区域设置
                switchFragment(
                    AreaSettingFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_COMMON_SETTING -> {
                //常规设置
                switchFragment(
                    CommonFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_ABOUT_DEVICE -> {
                //  关于设备
                switchFragment(
                    AboutDeviceFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_FIRMWARE_UPDATE -> {
                //  固件升级
                switchFragment(
                    FirmwareUpdateFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
        }

    }

    companion object {
        fun newInstance(args: Bundle?) = SettingFragment()
            .apply { arguments = args }
    }
}