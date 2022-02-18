package com.sunmi.uhf.fragment.operation.tab

import android.os.Bundle
import androidx.lifecycle.Observer
import com.sunmi.rfid.RFIDManager
import com.sunmi.rfid.ReaderCall
import com.sunmi.rfid.constant.CMD
import com.sunmi.rfid.constant.ParamCts
import com.sunmi.rfid.entity.DataParameter
import com.sunmi.uhf.BuildConfig
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.bean.CommonListBean
import com.sunmi.uhf.constants.Constant
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.TabReadWriteBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.list.ListFragment
import com.sunmi.uhf.fragment.operation.LabelOperationModel
import com.sunmi.uhf.utils.LiveDataBusEvent
import com.sunmi.uhf.utils.LogUtils
import com.sunmi.uhf.utils.StrUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @ClassName: TabReadFragment
 * @Description: 标签操作页面 第一个 tab 读取写入
 * @Author: clh
 * @CreateDate: 20-9-11 下午3:48
 * @UpdateDate: 20-9-11 下午3:48
 */
class TabReadFragment : BaseFragment<TabReadWriteBinding>() {
    lateinit var vm: LabelOperationModel
    private var isRead = false
    private var optArea: Byte = 0x01
    private var address: Byte = 0x00
    private var len: Byte = 0x00
    private lateinit var pwd: ByteArray
    private lateinit var optEpc: ByteArray
    private lateinit var data: ByteArray
    private val clearEpcCall = object : ReaderCall() {
        override fun onSuccess(cmd: Byte, params: DataParameter?) {
            LogUtils.i(
                "darren",
                String.format("CMD: 0x%02X, params info: %s", cmd, params?.toString() ?: "")
            )
            mainScope.launch(Dispatchers.IO) {
                RFIDManager.getInstance().getHelper()?.apply {
                    registerReaderCall(optCall)
                    setAccessEpcMatch(optEpc.size.toByte(), optEpc)
                }
            }
        }

        override fun onTag(cmd: Byte, state: Byte, tag: DataParameter?) {
            // nope
        }

        override fun onFailed(cmd: Byte, errorCode: Byte, msg: String?) {
            LogUtils.e(
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
                        RFIDManager.getInstance().getHelper()?.apply {
                            if (isRead) {
                                readTag(optArea, address, len, pwd)
                            } else {
                                writeTag(pwd, optArea, address, len, data)
                            }
                        }
                    }
                }
                CMD.READ_TAG -> {
                    RFIDManager.getInstance().getHelper()?.unregisterReaderCall()
                    mainScope.launch {
                        vm.dataInfo.value = params?.getString(ParamCts.TAG_DATA, "")
                    }
                }
                CMD.WRITE_TAG -> {
                    RFIDManager.getInstance().getHelper()?.unregisterReaderCall()
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
                    CMD.READ_TAG -> {
                        showShort(
                            resources.getString(R.string.hint_tag_read_error, errorCode, msg)
                        )
                    }
                    CMD.WRITE_TAG -> {
                        showShort(
                            resources.getString(R.string.hint_tag_write_error, errorCode, msg)
                        )
                    }
                    else -> {
                        showShort(R.string.hint_unknow_error)
                    }
                }
            }
        }
    }

    private fun handleReadWriteData(isRead: Boolean) {
        this.isRead = isRead
        // epc data
        val epc = StrUtils.strToByteArray(vm.epc.value, tip = object : (() -> Unit) {
            override fun invoke() {
                showShort(R.string.edit_epc_text)
            }
        })
        if (epc == null || epc.isEmpty()) return
        this.optEpc = epc
        // optArea
        if (optArea < 0 || optArea > 0x03) {
            showShort(R.string.hint_please_select_opt_area)
            return
        }
        // pwd data
        val pwd = StrUtils.strToByteArray(vm.pwd.value, 4, object : (() -> Unit) {
            override fun invoke() {
                showShort(R.string.edit_pwd_text)
            }
        })
        if (pwd == null || pwd.isEmpty()) return
        this.pwd = pwd
        // address
        val address = if (vm.startLocation.value.isNullOrEmpty()) -1 else vm.startLocation.value!!.toInt()
        if (address < 0) {
            showShort(R.string.param_start_address_error)
            return
        }
        this.address = address.toByte()
        // data len
        val len = if (vm.dataLength.value.isNullOrEmpty()) -1 else vm.dataLength.value!!.toInt()
        if (len < 0) {
            showShort(R.string.param_data_len_error)
            return
        }
        this.len = len.toByte()
        // data
        var data: ByteArray?
        if (!isRead) {
            data = StrUtils.strToByteArray(str = vm.dataInfo.value, tip = object : (() -> Unit) {
                override fun invoke() {
                    showShort(R.string.param_data_error)
                }
            })
            if (data == null || data.isEmpty()) return
            this.data = data
        }
        // operation
        RFIDManager.getInstance().apply {
            if (isConnect() && getHelper()?.getScanModel() != RFIDManager.NONE) {
                getHelper()?.registerReaderCall(clearEpcCall)
                getHelper()?.cancelAccessEpcMatch()
            }
        }
    }

    override fun getLayoutResource() = R.layout.tab_read_write

    override fun initVM() {
        vm = getViewModel(LabelOperationModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        if (vm.areaData.value == null) {
            vm.areaData.value = "EPC"
            optArea = 0x01
        }
    }

    override fun initData() {
        LiveDataBusEvent.get().with(EventConstant.LABEL_SELECT, CommonListBean::class.java)
            .observe(viewLifecycleOwner, Observer {
                if (isVisible) {
                    optArea = it.index?.toByte() ?: 0x01
                    vm.areaData.value = it.select
                }
            })
    }

    override fun onSimpleViewEvent(event: SimpleViewEvent) {
        super.onSimpleViewEvent(event)
        when (event.event) {
            EventConstant.EVENT_OPERATION_AREA -> {
                //操作区域
                val args = Bundle().apply {
                    putString(
                        Constant.KEY_TITLE,
                        resources.getString(R.string.select_operation_area_text)
                    )
                    putStringArrayList(
                        Constant.KEY_LIST,
                        resources.getStringArray(R.array.area_read_write_array)
                            .toList() as ArrayList<String>
                    )
                    putParcelable(
                        Constant.KEY_SELECT,
                        CommonListBean(
                            type = EventConstant.EVENT_OPERATION_AREA,
                            select = vm.areaData.value
                        )
                    )
                }
                switchFragment(
                    ListFragment.newInstance(args),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_OPERATION_READ_DATA -> {
                handleReadWriteData(true)
            }
            EventConstant.EVENT_OPERATION_WRITE_DATA -> {
                handleReadWriteData(false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        fun newInstance(args: Bundle?) = TabReadFragment()
            .apply { arguments = args }
    }
}