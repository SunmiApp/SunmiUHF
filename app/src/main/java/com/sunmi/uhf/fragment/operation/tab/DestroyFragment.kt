package com.sunmi.uhf.fragment.operation.tab

import android.os.Bundle
import com.sunmi.rfid.RFIDManager
import com.sunmi.rfid.ReaderCall
import com.sunmi.rfid.constant.CMD
import com.sunmi.rfid.entity.DataParameter
import com.sunmi.uhf.BuildConfig
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.TabDestroyBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.operation.LabelOperationModel
import com.sunmi.uhf.utils.LogUtils
import com.sunmi.uhf.utils.StrUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @ClassName: DestoryFragment
 * @Description: 标签操作页面 第三个 tab 销毁
 * @Author: clh
 * @CreateDate: 20-9-11 下午3:52
 * @UpdateDate: 20-9-11 下午3:52
 */
class DestroyFragment : BaseFragment<TabDestroyBinding>() {
    lateinit var vm: LabelOperationModel
    private lateinit var optEpc: ByteArray
    private lateinit var pwd: ByteArray
    private val clearEpcCall = object : ReaderCall() {
        override fun onSuccess(cmd: Byte, params: DataParameter?) {
            if (BuildConfig.DEBUG) LogUtils.i(
                "darren",
                String.format("CMD: 0x%02X, params info: %s", cmd, params?.toString() ?: "")
            )
            mainScope.launch(Dispatchers.IO) {
                RFIDManager.getInstance().apply {
                    getHelper()?.registerReaderCall(optCall)
                    getHelper()?.setAccessEpcMatch(optEpc.size.toByte(), optEpc)
                }
            }
        }

        override fun onTag(cmd: Byte, state: Byte, tag: DataParameter?) {
            // nope
        }

        override fun onFailed(cmd: Byte, errorCode: Byte, msg: String?) {
            if (BuildConfig.DEBUG) LogUtils.e(
                "darren",
                String.format("CMD: 0x%02X, Error Code: 0x%02X, msg info: %s", cmd, errorCode, msg)
            )
            RFIDManager.getInstance().getHelper()?.unregisterReaderCall()
            mainScope.launch {
                showShort(R.string.hint_not_found_tag)
            }
        }
    }
    private val optCall = object : ReaderCall() {
        override fun onSuccess(cmd: Byte, params: DataParameter?) {
            if (BuildConfig.DEBUG) LogUtils.i(
                "darren",
                String.format("CMD: 0x%02X, params info: %s", cmd, params?.toString() ?: "")
            )
            when (cmd) {
                CMD.SET_ACCESS_EPC_MATCH -> {
                    mainScope.launch(Dispatchers.IO) {
                        RFIDManager.getInstance().apply {
                            getHelper()?.killTag(pwd)
                        }
                    }
                }
                CMD.KILL_TAG -> {
                    mainScope.launch {
                        showShort(R.string.hint_tag_operation_success)
                    }
                }
                else -> {
                    RFIDManager.getInstance().getHelper()?.unregisterReaderCall()
                    mainScope.launch {
                        showShort(R.string.hint_unknow_error)
                    }
                }
            }
        }

        override fun onTag(cmd: Byte, state: Byte, tag: DataParameter?) {
            // nope
        }

        override fun onFailed(cmd: Byte, errorCode: Byte, msg: String?) {
            if (BuildConfig.DEBUG) LogUtils.e(
                "darren",
                String.format("CMD: 0x%02X, Error Code: 0x%02X, msg info: %s", cmd, errorCode, msg)
            )
            RFIDManager.getInstance().getHelper()?.unregisterReaderCall()
            mainScope.launch {
                when (cmd) {
                    CMD.SET_ACCESS_EPC_MATCH -> {
                        showShort(R.string.hint_not_found_tag)
                    }
                    CMD.KILL_TAG -> {
                        showShort(
                            getString(
                                R.string.hint_tag_operation_failed,
                                errorCode,
                                msg
                            )
                        )
                    }
                    else -> {
                        showShort(R.string.hint_unknow_error)
                    }
                }
            }
        }
    }

    override fun getLayoutResource() = R.layout.tab_destroy

    override fun initVM() {
        vm = getViewModel(LabelOperationModel::class.java)
        binding.vm = vm
    }

    override fun initView() {

    }

    override fun initData() {

    }

    override fun onSimpleViewEvent(event: SimpleViewEvent) {
        super.onSimpleViewEvent(event)
        when (event.event) {
            EventConstant.EVENT_BACK -> {
                performBackClick()
            }
            EventConstant.EVENT_OPERATION_DESTROY_TAG -> {
                // epc data
                val epc = StrUtils.strToByteArray(str = vm.epc.value, tip = object : (() -> Unit) {
                    override fun invoke() {
                        showShort(R.string.edit_epc_text)
                    }
                })
                if (epc == null || epc.isEmpty()) return
                this.optEpc = epc
                // pwd data
                val pwd = StrUtils.strToByteArray(str = vm.pwd.value, 4, object : (() -> Unit) {
                    override fun invoke() {
                        showShort(R.string.edit_pwd_text)
                    }
                })
                if (pwd == null || pwd.isEmpty()) return
                this.pwd = pwd
                // operation
                RFIDManager.getInstance().apply {
                    if (isConnect() && getHelper()?.getScanModel() != RFIDManager.NONE) {
                        getHelper()?.registerReaderCall(clearEpcCall)
                        getHelper()?.cancelAccessEpcMatch()
                    }
                }
            }
        }

    }


    companion object {
        fun newInstance(args: Bundle?) = DestroyFragment()
            .apply { arguments = args }
    }
}