package com.sunmi.uhf.fragment.setting.child

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import com.sunmi.rfid.RFIDManager
import com.sunmi.rfid.ReaderCall
import com.sunmi.rfid.constant.CMD
import com.sunmi.rfid.constant.ParamCts
import com.sunmi.rfid.entity.DataParameter
import com.sunmi.uhf.BuildConfig
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentAboutDeviceBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.setting.SettingModel
import com.sunmi.uhf.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * @ClassName: AboutDeviceFragment
 * @Description: 设置  关于设备
 * @Author: clh
 * @CreateDate: 20-9-14 下午5:23
 * @UpdateDate: 20-9-14 下午5:23
 */
class AboutDeviceFragment : BaseFragment<FragmentAboutDeviceBinding>() {
    lateinit var vm: SettingModel
    private val br = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ParamCts.BROADCAST_ON_CONNECT,
                ParamCts.BROADCAST_READER_BOOT -> getData()
                ParamCts.BROADCAST_SN -> {
                    vm.sn.value = intent.getStringExtra(ParamCts.SN)
                    val rfBand = ParamCts.getRFFrequencyBand(vm.sn.value ?: "")
                    vm.moduleType.value = if (rfBand[0] == 1) {
                        when (rfBand[3]) {
                            0x01 -> getString(R.string.module_type_america)
                            0x02 -> getString(R.string.module_type_europe)
                            0x03 -> getString(R.string.module_type_china)
                            else -> getString(R.string.module_type_america)
                        }
                    } else {
                        resources.getString(R.string.hint_unknow)
                    }
                }
                ParamCts.BROADCAST_FIRMWARE_VERSION -> {
                    val main = intent.getIntExtra(ParamCts.FIRMWARE_MAIN_VERSION, 0)
                    val min = intent.getIntExtra(ParamCts.FIRMWARE_MIN_VERSION, 0)
                    vm.uhfVer.value = "$main.$min"
                }
                ParamCts.BROADCAST_BATTERY_REMAINING_PERCENTAGE,
                ParamCts.BROADCAST_BATTER_LOW_ELEC -> {
                    vm.batteryRate.value = intent.getIntExtra(ParamCts.BATTERY_REMAINING_PERCENT, 100)
                }
                ParamCts.BROADCAST_BATTER_CHARGING -> {
                    vm.batteryCharge.value = when (intent.getByteExtra(ParamCts.BATTERY_CHARGING, 0.toByte())) {
                        0x00.toByte() -> getString(R.string.hint_un_charge)
                        0x01.toByte() -> getString(R.string.hint_pre_charge)
                        0x02.toByte() -> getString(R.string.hint_fast_charge)
                        0x03.toByte() -> getString(R.string.hint_charge_done)
                        else -> getString(R.string.hint_un_charge)
                    }
                }
                ParamCts.BROADCAST_BATTER_CHARGING_NUM_TIMES -> {
                    vm.batteryTimes.value = intent.getIntExtra(ParamCts.BATTERY_CHARGING_NUM_TIMES, 0)
                }
                // TODO: 10/14/20 SDK更新后修改 ParamCts.BROADCAST_BATTERY_VOLTAGE
                "com.sunmi.rfid.batteryVoltage" -> {
                    val v = intent.getIntExtra(ParamCts.BATTERY_VOLTAGE, 0)
                    LogUtils.i("darren", "UHF battery Voltage: ${v}mV")
                    vm.batteryVoltage.value = v
                }

            }
        }
    }
    private val optCall = object : ReaderCall() {
        override fun onSuccess(cmd: Byte, params: DataParameter?) {
            if (BuildConfig.DEBUG) LogUtils.i(
                "darren",
                String.format("CMD: 0x%02X, params info: %s", cmd, params?.toString() ?: "")
            )
            mainScope.launch {
                when (cmd) {
                    CMD.GET_FIRMWARE_VERSION -> {
                        val main = params?.getByte(ParamCts.FIRMWARE_MAIN_VERSION)
                        val min = params?.getByte(ParamCts.FIRMWARE_MIN_VERSION)
                        vm.moduleVer.value = "RFID $main.$min(${params?.getString(ParamCts.FIRMWARE_VERSION) ?: ""})"
                    }
                    CMD.GET_READER_TEMPERATURE -> {
                        val p = params?.getByte(ParamCts.PLUS_MINUS, 1)?.toInt() ?: 1
                        var t = params?.getByte(ParamCts.TEMPERATURE, 0)?.toInt() ?: 0
                        t = if (p == 0) -1 * t else t
                        vm.moduleTemplate.value = t
                    }
                }
            }
        }

        override fun onTag(cmd: Byte, state: Byte, tag: DataParameter?) {
        }

        override fun onFailed(cmd: Byte, errorCode: Byte, msg: String?) {
            if (BuildConfig.DEBUG) LogUtils.e(
                "darren",
                String.format("CMD: 0x%02X, Error Code: 0x%02X, msg info: %s", cmd, errorCode, msg)
            )
        }
    }

    override fun getLayoutResource() = R.layout.fragment_about_device

    override fun initVM() {
        vm = getViewModel(SettingModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        vm.title.value = resources.getString(R.string.setting_about_device_text)
    }

    override fun initData() {
        context?.registerReceiver(br, IntentFilter().apply {
            addAction(ParamCts.BROADCAST_ON_CONNECT)
            addAction(ParamCts.BROADCAST_READER_BOOT)
            addAction(ParamCts.BROADCAST_SN)
            addAction(ParamCts.BROADCAST_FIRMWARE_VERSION)
            addAction(ParamCts.BROADCAST_BATTERY_REMAINING_PERCENTAGE)
            addAction(ParamCts.BROADCAST_BATTER_LOW_ELEC)
            addAction(ParamCts.BROADCAST_BATTER_CHARGING)
            addAction(ParamCts.BROADCAST_BATTER_CHARGING_NUM_TIMES)
            // TODO: 10/14/20 SDK更新后修改 ParamCts.BROADCAST_BATTERY_VOLTAGE
            addAction("com.sunmi.rfid.batteryVoltage")
        })
        getData()
    }

    private fun getData() {
        RFIDManager.getInstance().apply {
            if (isConnect()) {
                getHelper()?.apply {
                    when (getScanModel()) {
                        RFIDManager.UHF_R2000, RFIDManager.UHF_S7100 -> {
                            registerReaderCall(optCall)
                            /* SN */
                            getReaderSN()
                            /* UHF 固件版本 */
                            getFirmwareVersion()
                            /* 模块固件版本 */
                            getReaderVersion()
                            /* 模块温度 */
                            getReaderTemperature()
                            /* UHF 电压 */
                            getBatteryVoltage()
                            /* UHF 电量 */
                            getBatteryRemainingPercent()
                            /* UHF 充电循环次数 */
                            getBatteryChargeNumTimes()
                            /* UHF 充电状态 */
                            getBatteryChargeState()
                            vm.isInner.postValue(false)
                        }
                        RFIDManager.INNER -> {
                            registerReaderCall(optCall)
                            /* UHF 固件版本 */
                            getFirmwareVersion()
                            /* 模块类型 */
                            binding.tvModelType.text = getString(R.string.module_type_inner)
                            vm.isInner.postValue(true)
                        }
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

        }
    }

    override fun onResume() {
        super.onResume()
        mainScope.launch(Dispatchers.IO) {
            while (isActive) {
                if (lifecycle.currentState != Lifecycle.State.RESUMED) {
                    LogUtils.d("darren", "refresh data >> stop")
                    break
                }
                getData()
                LogUtils.d("darren", "refresh data ...")
                delay(10000)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        context?.unregisterReceiver(br)
        RFIDManager.getInstance().apply {
            if (isConnect()) getHelper()?.unregisterReaderCall()
        }
    }

    companion object {
        fun newInstance(args: Bundle?) = AboutDeviceFragment()
            .apply { arguments = args }
    }
}