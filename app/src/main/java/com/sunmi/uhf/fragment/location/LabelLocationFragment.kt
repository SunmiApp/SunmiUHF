package com.sunmi.uhf.fragment.location

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.lifecycle.Observer
import com.sunmi.rfid.RFIDManager
import com.sunmi.rfid.constant.CMD
import com.sunmi.rfid.constant.ParamCts
import com.sunmi.rfid.entity.DataParameter
import com.sunmi.uhf.App
import com.sunmi.uhf.R
import com.sunmi.uhf.constants.Config
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentLabelLocationBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.ReadBaseFragment
import com.sunmi.uhf.utils.LogUtils
import com.sunmi.widget.util.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * @ClassName: LabelLocationFragment
 * @Description: 标签定位 页面
 * @Author: clh
 * @CreateDate: 20-9-10 下午2:50
 * @UpdateDate: 20-9-10 下午2:50
 */
class LabelLocationFragment : ReadBaseFragment<FragmentLabelLocationBinding>() {
    lateinit var vm: LabelLocationModel
    private var isLoop = false
    private var targetId = ""

    override fun getLayoutResource() = R.layout.fragment_label_location

    override fun initVM() {
        vm = getViewModel(LabelLocationModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        binding.voiceSw.isChecked = App.getPref().getParam(Config.KEY_TIP_VOICE, Config.DEF_TIP_VOICE)
        binding.lightSw.isChecked = App.getPref().getParam(Config.KEY_TIP_LIGHT, Config.DEF_TIP_LIGHT)
        binding.epcTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                targetId = binding.epcTv.text.toString().replace(" ", "").toUpperCase()
            }
        })
        binding.voiceSw.setOnClickListener {
            App.getPref().setParam(Config.KEY_TIP_VOICE, binding.voiceSw.isChecked)
        }
        binding.lightSw.setOnClickListener {
            App.getPref().setParam(Config.KEY_TIP_LIGHT, binding.lightSw.isChecked)
        }
    }

    override fun initData() {
        super.initData()
        vm.start.observe(viewLifecycleOwner, Observer { startStop(it) })
    }

    override fun onSimpleViewEvent(event: SimpleViewEvent) {
        super.onSimpleViewEvent(event)
        when (event.event) {
            EventConstant.EVENT_BACK -> {
                performBackClick()
            }
        }
    }

    private fun startStop(en: Boolean) {
        if (TextUtils.isEmpty(targetId)) {
            ToastUtils.showShort(R.string.input_epc_text)
            return
        }
        if (en) {
            tidList.clear()
            tagList.clear()
            binding.signalVv.setSignal(0f)
            start()
        } else {
            stop()
        }
    }

    override fun handleBottomStart() {
        vm.start.value = true
    }

    override fun handleBottomStop() {
        vm.start.value = false
    }

    override fun start() {
        super.start()
        if (!isLoop) {
            mainScope.launch(Dispatchers.IO) {
                RFIDManager.getInstance().helper.apply {
                    when (App.getPref().getParam(Config.KEY_LABEL, Config.DEF_LABEL)) {
                        0 -> {
                            // 6C标签盘存
                            registerReaderCall(call)
                            customizedSessionTargetInventory(0x00, 0x00, 0x00, 0, 0, 20)
                            isLoop = true
                        }
                        /*1 -> {
                            // 6B标签盘存
                            registerReaderCall(call)
                            iso180006BInventory()
                            isLoop = true
                        }*/
                        else -> {
                            LogUtils.e("darren", "error label index")
                        }
                    }
                }
            }
        }
    }

    override fun stop() {
        super.stop()
        if (isLoop) {
            mainScope.launch(Dispatchers.IO) {
                RFIDManager.getInstance().helper.apply {
                    inventory(1)
                    unregisterReaderCall()
                    isLoop = false
                }
            }
        }
    }

    override fun onCallSuccess(cmd: Byte, params: DataParameter?) {
        when (cmd) {
            CMD.CUSTOMIZED_SESSION_TARGET_INVENTORY -> {
                isLoop = false
                if (state) {
                    start()
                }
            }
            /*CMD.ISO18000_6B_INVENTORY -> {
                isLoop = false
                if (state) {
                    start()
                }
            }*/
            else -> {
                LogUtils.d("darren", "other success.")
            }
        }
    }

    override fun onCallTag(cmd: Byte, state: Byte, tag: DataParameter?) {
        if (tag == null) return
        when (cmd) {
            CMD.CUSTOMIZED_SESSION_TARGET_INVENTORY -> {
                // 6C标签盘存
                val epc = (tag.getString(ParamCts.TAG_EPC) ?: "").replace(" ", "").toUpperCase()
                LogUtils.i("darren", "found tag:$epc")
                if (TextUtils.equals(targetId, epc)) {
                    playTips()
                    val rssi = (Integer.parseInt(tag.getString(ParamCts.TAG_RSSI, "129")) - 129)
                    LogUtils.i("darren", "found tag rssi:$rssi")
                    notifyTagDataChange(rssi)
                }
            }
            /*CMD.ISO18000_6B_INVENTORY -> {
                val uid = tag.getString(ParamCts.TAG_UID) ?: ""
                LogUtils.i("darren", "found tag:$uid")
                notifyTagDataChange()
            }*/
            else -> {
                LogUtils.d("darren", "other found tag.")
            }
        }
    }

    override fun onCallFailed(cmd: Byte, errorCode: Byte, msg: String?) {
        when (cmd) {
            CMD.CUSTOMIZED_SESSION_TARGET_INVENTORY -> {
                isLoop = false
                if (state) {
                    start()
                }
            }
            /*CMD.ISO18000_6B_INVENTORY -> {
                isLoop = false
                if (state) {
                    start()
                }
            }*/
            else -> {
                LogUtils.d("darren", "other failed.")
            }
        }
    }

    private fun notifyTagDataChange(rssi: Int) {
        mainScope.launch {
            val pro = 100 - (((rssi * -1) - 31) * PERCENT_ITEM)
            LogUtils.i("darren", "signal: $pro")
            binding.signalVv.setSignal(pro)
        }
    }

    companion object {
        fun newInstance(args: Bundle?) = LabelLocationFragment()
            .apply { arguments = args }

        const val PERCENT_ITEM = 100f / (94 - 31)
    }
}