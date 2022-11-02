package com.sunmi.uhf.fragment.setting.child

import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Bundle
import androidx.annotation.Nullable
import com.sunmi.rfid.FirmwareUpdateCall
import com.sunmi.rfid.RFIDManager
import com.sunmi.rfid.constant.ParamCts
import com.sunmi.uhf.App
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.Config
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentFirmwareUpdateBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.setting.SettingModel
import com.sunmi.uhf.utils.ContentUriUtil.getPath
import com.sunmi.uhf.utils.LogUtils
import kotlinx.coroutines.launch


/**
 * @ClassName: FirmwareUpdateFragment
 * @Description: 设置  固件升级
 * @Author: clh
 * @CreateDate: 20-9-14 下午5:47
 * @UpdateDate: 20-9-14 下午5:47
 */
class FirmwareUpdateFragment : BaseFragment<FragmentFirmwareUpdateBinding>() {
    lateinit var vm: SettingModel
    var elec = 100
    val br = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ParamCts.BROADCAST_BATTERY_REMAINING_PERCENTAGE,
                ParamCts.BROADCAST_BATTER_LOW_ELEC -> {
                    elec = intent.getIntExtra(ParamCts.BATTERY_REMAINING_PERCENT, 100)
                    LogUtils.d("darren", "BroadcastReceiver battery-remaining-percent:$elec%")
                    if (elec <= Config.LOW_ELEC) {
                        showShort(getString(R.string.hint_please_charge, elec))
                    }
                }
            }
        }

    }

    override fun getLayoutResource() = R.layout.fragment_firmware_update

    override fun initVM() {
        vm = getViewModel(SettingModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        vm.title.value = resources.getString(R.string.setting_firmware_update_text)
    }

    override fun initData() {
        RFIDManager.getInstance().apply {
            if (isConnect()) {
                getHelper()?.getBatteryRemainingPercent()
            }
        }
    }

    override fun onSimpleViewEvent(event: SimpleViewEvent) {
        super.onSimpleViewEvent(event)
        when (event.event) {
            EventConstant.EVENT_BACK -> {
                performBackClick()
            }
            EventConstant.EVENT_CHOICE_FILE -> {
                chooseFile()
            }
            EventConstant.EVENT_FIRMWARE_UPDATE_UPGRADE -> {
                upgrade()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        context?.registerReceiver(br, IntentFilter().apply {
            addAction(ParamCts.BROADCAST_BATTERY_REMAINING_PERCENTAGE)
            addAction(ParamCts.BROADCAST_BATTER_LOW_ELEC)
        })
    }

    override fun onStop() {
        super.onStop()
        context?.unregisterReceiver(br)
    }

    // 打开系统的文件选择器
    // 调用系统文件管理器
    private fun chooseFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("application/*").addCategory(Intent.CATEGORY_OPENABLE)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            startActivityForResult(Intent.createChooser(intent, "Choose File"), CHOOSE_FILE_CODE)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }


    // 获取文件的真实路径
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null || data.data == null) return
        if (resultCode == Activity.RESULT_OK && requestCode == CHOOSE_FILE_CODE) {
            val uri: Uri = data.data!!
            vm.mBinPath.value = getPath(App.mContext, uri)
        }
    }

    private fun upgrade() {
        if (vm.updating.value == true) return
        LogUtils.i("darren", "update file: ${vm.mBinPath.value}")
        if (elec <= 20) {
            showShort(resources.getString(R.string.hint_please_charge, elec))
            return
        }
        RFIDManager.getInstance().apply {
            if (isConnect() && getHelper()?.getScanModel().run { this == RFIDManager.UHF_R2000 || this == RFIDManager.UHF_S7100 }) {
                vm.updating.value = true
                vm.updateProgress.value = 0
                getHelper()?.firmwareUpdate(vm.mBinPath.value, object : FirmwareUpdateCall() {
                    override fun onSuccess() {
                        LogUtils.d("darren", "onSuccess")
                        mainScope.launch {
                            showShort(R.string.update_success)
                            binding.root.postDelayed({
                                vm.updating.value = false
                            }, 2000)
                        }
                    }

                    override fun onProgress(progress: Int) {
                        LogUtils.d("darren", "onProgress: $progress")
                        mainScope.launch {
                            vm.updateProgress.value = progress
                        }
                    }

                    override fun onFailed(code: Int, msg: String?) {
                        LogUtils.d("darren", "onProgress: $code => $msg")
                        mainScope.launch {
                            showShort(getString(R.string.update_failed, code, msg))
                            binding.root.postDelayed({
                                vm.updating.value = false
                            }, 2000)
                        }
                    }
                })
            }
        }
    }

    companion object {
        fun newInstance(args: Bundle?) = FirmwareUpdateFragment()
            .apply { arguments = args }

        const val CHOOSE_FILE_CODE = 100
    }
}