package com.sunmi.uhf.fragment.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.TypedValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import com.sunmi.rfid.RFIDManager
import com.sunmi.rfid.constant.ParamCts
import com.sunmi.uhf.App
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.Config
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentHomeBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.filter.LabelFilterFragment
import com.sunmi.uhf.fragment.location.LabelLocationFragment
import com.sunmi.uhf.fragment.operation.LabelOperationFragment
import com.sunmi.uhf.fragment.readwrite.ReadWriteFragment
import com.sunmi.uhf.fragment.setting.SettingFragment
import com.sunmi.uhf.fragment.takeinventory.TakeInventoryFragment
import com.sunmi.uhf.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * @ClassName: HomeFragment
 * @Description: 首页
 * @Author: clh
 * @CreateDate: 20-9-7 下午3:27
 * @UpdateDate: 20-9-7 下午3:27
 */
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    lateinit var vm: HomeViewMode
    private var chargingState: Byte = 0
    private val br = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            LogUtils.d("darren", "BroadcastReceiver HomeFragment-receiver:${intent.action ?: ""}")
            when (intent.action) {
                ParamCts.BROADCAST_ON_LOST_CONNECT -> {
                    showShort(R.string.hint_please_check_device_connect)
                }
                ParamCts.BROADCAST_BATTER_LOW_ELEC,
                ParamCts.BROADCAST_BATTERY_REMAINING_PERCENTAGE -> {
                    val elec = intent.getIntExtra(ParamCts.BATTERY_REMAINING_PERCENT, 100)
                    LogUtils.d(
                        "darren",
                        "BroadcastReceiver HomeFragment-battery-remaining-percent:$elec%"
                    )
                    App.getPref().setParam(Config.ELEC_CACHE, elec)
                    setCalculateLevel(elec)
                    if (elec <= Config.LOW_ELEC && chargingState == 0x00.toByte()) {
                        showShort(getString(R.string.hint_please_charge, elec))
                    }
                }
                ParamCts.BROADCAST_BATTER_CHARGING -> {
                    chargingState = intent.getByteExtra(ParamCts.BATTERY_CHARGING, 0.toByte())
                    val chargingState =
                        when (chargingState) {
                            0x00.toByte() -> getString(R.string.un_charge)
                            0x01.toByte() -> getString(R.string.pre_charge)
                            0x02.toByte() -> getString(R.string.fast_charge)
                            0x03.toByte() -> getString(R.string.charge_done)
                            else -> ""
                        }
                    LogUtils.d(
                        "darren",
                        "BroadcastReceiver HomeFragment-chargingState:$chargingState"
                    )
                }
                else -> {
                    LogUtils.d("darren", "BroadcastReceiver HomeFragment-receiver:${intent.action}")
                }
            }
        }
    }

    override fun getLayoutResource() = R.layout.fragment_home

    override fun initVM() {
        vm = getViewModel(HomeViewMode::class.java)
    }

    override fun initView() {
        binding.vm = vm
    }

    override fun initData() {
        setCalculateLevel(App.getPref().getParam(Config.ELEC_CACHE, 0))
        vm.isInner.observe(viewLifecycleOwner, Observer {
            if (Config.DEF_INVALID_POWER == App.getPref().getParam(Config.KEY_RF_POWER, Config.DEF_INVALID_POWER)) {
                if (it) {
                    App.getPref().setParam(Config.KEY_RF_POWER, Config.DEF_INNER_POWER_MAX)
                } else {
                    App.getPref().setParam(Config.KEY_RF_POWER, Config.DEF_UHF_POWER_MAX)
                }
            }
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
            EventConstant.EVENT_FAST_READ_WRITE -> {
                //快速读取
                switchFragment(
                    ReadWriteFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_TAKE_INVENTORY -> {
                //盘存
                switchFragment(
                    TakeInventoryFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_LABEL_OPERATION -> {
                //标签操作存页
                switchFragment(
                    LabelOperationFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_LABEL_LOCATION -> {
                //标签定位
                switchFragment(
                    LabelLocationFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_LABEL_FILTER -> {
                //标签过滤
                switchFragment(
                    LabelFilterFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_SETTING -> {
                //setting
                switchFragment(
                    SettingFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
        }
    }


    override fun onResume() {
        super.onResume()
        val filter = IntentFilter().apply {
            addAction(ParamCts.BROADCAST_ON_LOST_CONNECT)
            addAction(ParamCts.BROADCAST_BATTERY_REMAINING_PERCENTAGE)
            addAction(ParamCts.BROADCAST_BATTER_CHARGING)
            addAction(ParamCts.BROADCAST_BATTER_LOW_ELEC)
        }
        context?.registerReceiver(br, filter)
        getBatteryRemainingPercent()
    }

    override fun onPause() {
        super.onPause()
        context?.unregisterReceiver(br)
    }

    private fun getBatteryRemainingPercent() {
        mainScope.launch(Dispatchers.IO) {
            var flag: Boolean
            var callNext = true
            while (isActive) {
                flag = false
                if (lifecycle.currentState != Lifecycle.State.RESUMED || !callNext) {
                    LogUtils.d("darren", "get battery Remaining Percent >> stop, callNext=$callNext")
                    break
                }
                RFIDManager.getInstance().apply {
                    try {
                        if (isConnect()) {
                            when (getHelper()?.getScanModel()) {
                                RFIDManager.UHF_R2000, RFIDManager.UHF_S7100 -> {
                                    getHelper()?.getBatteryRemainingPercent()
                                    getHelper()?.getBatteryChargeState()
                                }
                                RFIDManager.INNER -> {
                                    callNext = false
                                }
                                RFIDManager.NONE -> {
                                    setCalculateLevel(0)
                                }
                            }
                            flag = true
                        }
                    } catch (e: Exception) {
                        LogUtils.e("darren", "get battery info error")
                        e.printStackTrace()
                    }
                }
                LogUtils.d("darren", "get battery Remaining Percent")
                if (flag) {
                    delay(30000)
                } else {
                    delay(500)
                }
            }
        }
    }

    private fun setCalculateLevel(elec: Int) {
        mainScope.launch {
            binding.powerStatusTv.text = "${getString(R.string.home_power_text)}$elec%"
            val calculateLevel = calculateLevel(elec)
            val layerDrawable = binding.powerStatusIv.drawable as LayerDrawable
            val clipDrawable = layerDrawable.findDrawableByLayerId(R.id.clip_drawable) as ClipDrawable
            clipDrawable.level = calculateLevel.toInt()
        }
    }

    private fun calculateLevel(progress: Int): Float {
        val leftOffset =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics)
        val powerLength =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 37.2f, resources.displayMetrics)
        val totalLength =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 52.5f, resources.displayMetrics)
        return (leftOffset + powerLength * progress / 100) * 10000 / totalLength
    }


    companion object {
        fun newInstance(args: Bundle?) = HomeFragment()
            .apply { arguments = args }
    }
}