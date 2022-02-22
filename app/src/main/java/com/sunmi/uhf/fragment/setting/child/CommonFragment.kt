package com.sunmi.uhf.fragment.setting.child

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.lifecycle.Observer
import com.sunmi.rfid.RFIDManager
import com.sunmi.rfid.constant.ParamCts
import com.sunmi.uhf.App
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.bean.CommonListBean
import com.sunmi.uhf.constants.Config
import com.sunmi.uhf.constants.Constant
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentCommonSettingBinding
import com.sunmi.uhf.dialog.HandleRebootDialog
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.list.ListFragment
import com.sunmi.uhf.fragment.setting.SettingModel
import com.sunmi.uhf.utils.LiveDataBusEvent
import com.sunmi.uhf.utils.LogUtils

/**
 * @ClassName: CommonFragment
 * @Description: 设置 常规设置
 * @Author: clh
 * @CreateDate: 20-9-14 下午4:37
 * @UpdateDate: 20-9-14 下午4:37
 */
class CommonFragment : BaseFragment<FragmentCommonSettingBinding>() {
    var dialog: HandleRebootDialog? = null
    lateinit var vm: SettingModel
    val br = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ParamCts.BROADCAST_READER_BOOT) {
                LogUtils.w("darren", "reader boot.")
                hideDialog()
                showShort(R.string.hint_reset_completed)
            }
        }

    }

    override fun getLayoutResource() = R.layout.fragment_common_setting

    override fun initVM() {
        vm = getViewModel(SettingModel::class.java)
        binding.vm = vm
        context?.registerReceiver(br, IntentFilter(ParamCts.BROADCAST_READER_BOOT))
    }

    override fun initView() {
        vm.title.value = resources.getString(R.string.setting_common_text)
        val index = App.getPref().getParam(Config.KEY_HANDLE_TYPE, Config.DEF_HANDLE_TYPE)
        vm.mHandleType.value = resources.getStringArray(R.array.hand_type_array)[index]
        binding.voiceSw.isChecked = App.getPref().getParam(Config.KEY_TIP_VOICE, Config.DEF_TIP_VOICE)
        binding.lightSw.isChecked = App.getPref().getParam(Config.KEY_TIP_LIGHT, Config.DEF_TIP_LIGHT)
    }

    override fun initData() {
        LiveDataBusEvent.get().with(EventConstant.LABEL_SELECT, CommonListBean::class.java)
                .observe(viewLifecycleOwner, Observer {
                    vm.mHandleType.value = it.select
                    App.getPref().setParam(Config.KEY_HANDLE_TYPE, it.index ?: Config.DEF_HANDLE_TYPE)
                })
    }

    override fun initBus() {
        binding.voiceSw.setOnCheckedChangeListener { _, check ->
            App.getPref().setParam(Config.KEY_TIP_VOICE, check)
        }
        binding.lightSw.setOnCheckedChangeListener { _, check ->
            App.getPref().setParam(Config.KEY_TIP_LIGHT, check)
        }
    }

    override fun onSimpleViewEvent(event: SimpleViewEvent) {
        super.onSimpleViewEvent(event)
        when (event.event) {
            EventConstant.EVENT_BACK -> {
                performBackClick()
            }
            EventConstant.EVENT_HANDLE_TYPE -> {
                //手柄触发方式
                val args = Bundle().apply {
                    putString(
                            Constant.KEY_TITLE,
                            resources.getString(R.string.setting_select_handle_type_text)
                    )
                    putStringArrayList(
                            Constant.KEY_LIST,
                            resources.getStringArray(R.array.hand_type_array)
                                    .toList() as ArrayList<String>
                    )
                    putParcelable(
                            Constant.KEY_SELECT,
                            CommonListBean(
                                    type = EventConstant.EVENT_HANDLE_TYPE,
                                    select = vm.mHandleType.value
                            )
                    )
                }
                switchFragment(
                        ListFragment.newInstance(args),
                        addToBackStack = true,
                        clearStack = false
                )
            }
            EventConstant.EVENT_HANDLE_REBOOT -> {
                //重启手柄
                showSureDialog()
            }
        }
    }


    /**
     * 重启手柄  二次确认弹窗
     *
     */
    private fun showSureDialog() {
        val showing = (dialog?.isAdded ?: false || dialog?.dialog?.isShowing ?: false)
        if (showing) dialog?.dismiss()
        if (dialog == null) {
            dialog = HandleRebootDialog.newInstance(null)
        }
        dialog?.listener = object : (() -> Unit) {
            override fun invoke() {
                dialog?.dismiss()
                RFIDManager.getInstance().getHelper()?.reset()
                showDialog()
            }
        }
        dialog?.show(parentFragmentManager, HandleRebootDialog::class.java.name)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dialog?.dismiss()
        context?.unregisterReceiver(br)
    }

    companion object {
        fun newInstance(args: Bundle?) = CommonFragment()
                .apply { arguments = args }
    }
}