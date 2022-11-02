package com.sunmi.uhf.fragment.setting.child

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.lifecycle.Observer
import com.sunmi.rfid.RFIDManager
import com.sunmi.rfid.ReaderCall
import com.sunmi.rfid.constant.ParamCts
import com.sunmi.rfid.entity.DataParameter
import com.sunmi.uhf.App
import com.sunmi.uhf.BuildConfig
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.bean.CommonListBean
import com.sunmi.uhf.constants.Config
import com.sunmi.uhf.constants.Constant
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentSettingInventoryModeBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.list.ListFragment
import com.sunmi.uhf.fragment.setting.SettingModel
import com.sunmi.uhf.utils.LiveDataBusEvent
import com.sunmi.uhf.utils.LogUtils
import com.sunmi.widget.dialog.InputDialog

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
    private var sn = ""
    private val br = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ParamCts.BROADCAST_SN -> {
                    sn = intent.getStringExtra(ParamCts.SN) ?: ""
                    handlerPower(sn)
                }
            }
        }
    }
    private val optCall = object : ReaderCall() {
        override fun onSuccess(cmd: Byte, params: DataParameter?) {
            if (BuildConfig.DEBUG) LogUtils.d("darren", String.format("CMD: 0x%02X, params info: %s", cmd, params?.toString() ?: ""))
        }

        override fun onTag(cmd: Byte, state: Byte, tag: DataParameter?) {
            if (BuildConfig.DEBUG) LogUtils.d(
                "darren",
                "found tag cmd:" + String.format("%%02X", cmd) + ", state: " + String.format("%%02X", state)
                        + ("params info: " + tag?.toString() ?: "")
            )
        }

        override fun onFailed(cmd: Byte, errorCode: Byte, msg: String?) {
            if (BuildConfig.DEBUG) LogUtils.d(
                "darren",
                "failed cmd: ${String.format("%02X", cmd)}, errorCode: ${String.format("%02X", errorCode)}, msg: $msg"
            )
        }
    }

    override fun initVM() {
        vm = getViewModel(SettingModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        vm.title.value = resources.getString(R.string.take_inventory_model_text)
        /* 平衡 模式 */
        binding.balanceLl.modeNameTv.text = resources.getString(R.string.balance_mode_text)
        binding.balanceLl.modeDesTv.text = resources.getString(R.string.balance_mode_tip_text)
        binding.balanceLl.sessionTv.text = "S1"
        binding.balanceLl.powerTv.text = getPower()
        binding.balanceLl.powerSw.isChecked =
            App.getPref().getParam(Config.KEY_TAKE_AUTO_POWER + Constant.INT_BALANCE_MODE, Config.DEF_TAKE_AUTO_POWER)
        /* 高速 模式 */
        binding.speedLl.modeNameTv.text = resources.getString(R.string.speed_mode_text)
        binding.speedLl.modeDesTv.text = resources.getString(R.string.speed_mode_tip_text)
        binding.speedLl.powerTv.text = getPower()
        binding.speedLl.sessionLl.visibility = View.GONE
        binding.speedLl.sessionLlV.visibility = View.GONE
        binding.speedLl.powerSwFl.visibility = View.GONE
        binding.speedLl.powerSwFlV.visibility = View.GONE
        /* 遍历器 模式 */
        binding.iteratorLl.modeNameTv.text = resources.getString(R.string.iterator_mode_text)
        binding.iteratorLl.modeDesTv.text = resources.getString(R.string.iterator_mode_tip_text)
        binding.iteratorLl.sessionTv.text = "S2"
        binding.iteratorLl.powerTv.text = getPower()
        binding.iteratorLl.powerSw.isChecked =
            App.getPref().getParam(Config.KEY_TAKE_AUTO_POWER + Constant.INT_ITERATOR_MODE, Config.DEF_TAKE_AUTO_POWER)

        vm.mInventoryMode.value = App.getPref().getParam(Config.KEY_TAKE_MODE, Config.DEF_TAKE_MODE)
        /* 自定义 模式*/
        vm.mFlag.value = resources.getStringArray(R.array.flag_array)[
                App.getPref().getParam(Config.KEY_TAKE_FLAG, Config.DEF_TAKE_FLAG)]
        vm.mSession.value = resources.getStringArray(R.array.session_array)[
                App.getPref().getParam(Config.KEY_TAKE_SESSION, Config.DEF_TAKE_SESSION)]
        vm.mRFLink.value = resources.getStringArray(R.array.link_array)[
                App.getPref().getParam(Config.KEY_TAKE_LINK, Config.DEF_TAKE_LINK)]
        binding.customLl.focusSw.isChecked =
            App.getPref().getParam(Config.KEY_TAKE_TAG_FOCUS, Config.DEF_TAKE_TAG_FOCUS)
        vm.rfPower.value = App.getPref().getParam(Config.KEY_RF_POWER, Config.DEF_INVALID_POWER)
    }

    override fun initData() {
        binding.balanceLl.powerSw.setOnClickListener {
            App.getPref().setParam(Config.KEY_TAKE_AUTO_POWER + Constant.INT_BALANCE_MODE, binding.balanceLl.powerSw.isChecked)
        }
        binding.iteratorLl.powerSw.setOnClickListener {
            App.getPref().setParam(Config.KEY_TAKE_AUTO_POWER + Constant.INT_ITERATOR_MODE, binding.iteratorLl.powerSw.isChecked)
        }
        binding.customLl.focusSw.setOnClickListener {
            App.getPref().setParam(Config.KEY_TAKE_TAG_FOCUS, binding.customLl.focusSw.isChecked)
        }
        vm.mInventoryMode.observe(viewLifecycleOwner, Observer {
            App.getPref().setParam(Config.KEY_TAKE_MODE, it)
        })
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
                    EventConstant.EVENT_LINK_URL -> {
                        vm.mRFLink.value = it.select
                        App.getPref().setParam(Config.KEY_TAKE_LINK, it.index)
                    }
                    EventConstant.EVENT_SESSION_SELECT -> {
                        vm.mSession.value = it.select
                        App.getPref().setParam(Config.KEY_TAKE_SESSION, it.index)
                    }
                    EventConstant.EVENT_TAKE_BABEL_FLAG -> {
                        vm.mFlag.value = it.select
                        App.getPref().setParam(Config.KEY_TAKE_FLAG, it.index)
                    }
                }
                binding.customLl.arrowIv.performClick()
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
                        resources.getStringArray(R.array.session_array).toList() as ArrayList<String>
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
            EventConstant.EVENT_TAKE_BABEL_FLAG -> {
                //目标 flag a/b
                val args = Bundle().apply {
                    putString(
                        Constant.KEY_TITLE,
                        resources.getString(R.string.select_flag_text)
                    )
                    putStringArrayList(
                        Constant.KEY_LIST,
                        resources.getStringArray(R.array.flag_array).toList() as ArrayList<String>
                    )
                    putParcelable(
                        Constant.KEY_SELECT,
                        CommonListBean(
                            type = EventConstant.EVENT_TAKE_BABEL_FLAG,
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

            EventConstant.EVENT_POWER_CLICK -> {
                val hint = StringBuilder(getString(R.string.please_input_rf_power))
                val minValue = if (vm.isInner.value!!) {
                    Config.DEF_INNER_POWER_MIN
                } else {
                    Config.DEF_UHF_POWER_MIN
                }
                val maxValue = if (vm.isInner.value!!) {
                    Config.DEF_INNER_POWER_MAX
                } else {
                    Config.DEF_UHF_POWER_MAX
                }
                hint.append("(")
                hint.append(minValue)
                hint.append("~")
                hint.append(maxValue)
                hint.append(")")
                val dialog = InputDialog.Builder()
                    .setTitle(getString(R.string.setting_rf_power_text))
                    .setHint(hint.toString())
                    .setEditType(true)
                    .setInputType(InputType.TYPE_CLASS_NUMBER)
                    .setLeftText(getString(R.string.cancel_text))
                    .setRightText(getString(R.string.sure_text))
                    .build(context)
                dialog.setCallback(object : InputDialog.DialogOnClickCallback {
                    override fun left(text: String?) {
                        dialog.cancel()
                    }

                    override fun middle(text: String?) {
                    }

                    override fun right(text: String?) {
                        LogUtils.i("darren", "file name: $text")
                        if (text != null) {
                            if (text.trim().isEmpty()) {
                                dialog.inputError()
                                return
                            } else {
                                if (text.toInt() in minValue..maxValue) {
                                    App.getPref().setParam(Config.KEY_RF_POWER, text.toInt())
                                    vm.rfPower.value = text.toInt()
                                    dialog.dismiss()
                                } else {
                                    dialog.inputError()
                                }
                            }
                        } else {
                            dialog.inputError()
                        }
                    }
                })
                dialog.show()
            }
        }
    }

    private fun handlerPower(sn: String) {
        val rfBand = ParamCts.getRFFrequencyBand(sn)
        var power = 30
        when (rfBand[3]) {
            1 -> {
                power = 30
            }
            2 -> {
                power = 28
            }
            3 -> {
                power = 29
            }
        }
        binding.balanceLl.powerTv.text = power.toString()
        binding.speedLl.powerTv.text = power.toString()
        binding.iteratorLl.powerTv.text = power.toString()
        RFIDManager.getInstance().apply {
            if (isConnect()) {
                getHelper()?.registerReaderCall(optCall)
                getHelper()?.setOutputAllPower(power.toByte())
            }
        }
    }

    private fun getPower(): String {
        var power = "30"
        RFIDManager.getInstance().apply {
            if (isConnect()) {
                when (getHelper()?.getScanModel()) {
                    RFIDManager.UHF_R2000, RFIDManager.UHF_S7100 -> {
                        power = "30"
                    }
                    RFIDManager.INNER -> {
                        power = "26"
                    }
                }
            }
        }
        return power
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
        fun newInstance(args: Bundle?) = InventoryModeFragment()
            .apply { arguments = args }

    }
}