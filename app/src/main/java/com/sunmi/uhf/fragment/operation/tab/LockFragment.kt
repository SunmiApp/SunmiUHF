package com.sunmi.uhf.fragment.operation.tab

import android.os.Bundle
import androidx.lifecycle.Observer
import com.sunmi.rfid.RFIDManager
import com.sunmi.rfid.ReaderCall
import com.sunmi.rfid.constant.CMD
import com.sunmi.rfid.entity.DataParameter
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.bean.CommonListBean
import com.sunmi.uhf.constants.Constant
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.TabLockBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.list.ListFragment
import com.sunmi.uhf.fragment.operation.LabelOperationModel
import com.sunmi.uhf.utils.LiveDataBusEvent
import com.sunmi.uhf.utils.LogUtils
import com.sunmi.uhf.utils.StrUtils
import com.sunmi.widget.util.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @ClassName: LockFragment
 * @Description: 标签操作页面 第二个 tab 锁定
 * @Author: clh
 * @CreateDate: 20-9-11 下午3:52
 * @UpdateDate: 20-9-11 下午3:52
 */
class LockFragment : BaseFragment<TabLockBinding>() {
    lateinit var vm: LabelOperationModel
    private var optArea: Byte = 0x01
    private var optType: Byte = 0x00
    private lateinit var optEpc: ByteArray
    private lateinit var pwd: ByteArray
    private val clearEpcCall = object : ReaderCall() {
        override fun onSuccess(cmd: Byte, params: DataParameter?) {
            LogUtils.i(
                javaClass.simpleName,
                String.format("CMD: 0x%02X, params info:", cmd, params?.toString() ?: "")
            )
            mainScope.launch(Dispatchers.IO) {
                RFIDManager.getInstance().apply {
                    helper.registerReaderCall(optCall)
                    helper.setAccessEpcMatch(optEpc.size.toByte(), optEpc)
                }
            }
        }

        override fun onTag(cmd: Byte, state: Byte, tag: DataParameter?) {
            // nope
        }

        override fun onFiled(cmd: Byte, errorCode: Byte, msg: String?) {
            LogUtils.e(
                javaClass.simpleName,
                String.format("CMD: 0x%02X, Error Code: 0x%02X, msg info: %s", cmd, errorCode, msg)
            )
            RFIDManager.getInstance().helper.unregisterReaderCall()
            mainScope.launch {
                ToastUtils.showShort(R.string.hint_not_found_tag)
            }
        }
    }
    private val optCall = object : ReaderCall() {
        override fun onSuccess(cmd: Byte, params: DataParameter?) {
            LogUtils.i(
                javaClass.simpleName,
                String.format("CMD: 0x%02X, params info:", cmd, params?.toString() ?: "")
            )
            when (cmd) {
                CMD.SET_ACCESS_EPC_MATCH -> {
                    mainScope.launch(Dispatchers.IO) {
                        RFIDManager.getInstance().apply {
                            helper.lockTag(pwd, optArea, optType)
                        }
                    }
                }
                CMD.LOCK_TAG -> {
                    mainScope.launch {
                        ToastUtils.showShort(R.string.hint_tag_operation_success)
                    }
                }
                else -> {
                    RFIDManager.getInstance().helper.unregisterReaderCall()
                    mainScope.launch {
                        ToastUtils.showShort(R.string.hint_unknow_error)
                    }
                }
            }
        }

        override fun onTag(cmd: Byte, state: Byte, tag: DataParameter?) {
            // nope
        }

        override fun onFiled(cmd: Byte, errorCode: Byte, msg: String?) {
            LogUtils.e(
                javaClass.simpleName,
                String.format("CMD: 0x%02X, Error Code: 0x%02X, msg info: %s", cmd, errorCode, msg)
            )
            RFIDManager.getInstance().helper.unregisterReaderCall()
            mainScope.launch {
                when (cmd) {
                    CMD.SET_ACCESS_EPC_MATCH -> {
                        ToastUtils.showShort(R.string.hint_not_found_tag)
                    }
                    CMD.LOCK_TAG -> {
                        ToastUtils.showShort(
                            getString(
                                R.string.hint_tag_operation_failed,
                                errorCode,
                                msg
                            )
                        )
                    }
                    else -> {
                        ToastUtils.showShort(R.string.hint_unknow_error)
                    }
                }
            }
        }
    }

    override fun getLayoutResource() = R.layout.tab_lock

    override fun initVM() {
        vm = getViewModel(LabelOperationModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        if (vm.areaData.value == null) {
            vm.areaData.value = "User"
            optArea = 0x01
        }
        if (vm.typeData.value == null) {
            vm.typeData.value = "开放"
            optType = 0x00
        }
    }

    override fun initData() {
        LiveDataBusEvent.get().with(EventConstant.LABEL_SELECT, CommonListBean::class.java)
            .observe(viewLifecycleOwner, Observer {
                if (isVisible) {
                    when (it.type) {
                        EventConstant.EVENT_OPERATION_AREA -> {
                            optArea = (it.index?.plus(0x01))?.toByte() ?: 0x01
                            vm.areaData.value = it.select
                        }
                        EventConstant.EVENT_OPERATION_TYPE -> {
                            optType = it.index?.toByte() ?: 0x00
                            vm.typeData.value = it.select
                        }
                    }
                }
            })
    }

    override fun onSimpleViewEvent(event: SimpleViewEvent) {
        super.onSimpleViewEvent(event)
        when (event.event) {
            EventConstant.EVENT_OPERATION_AREA -> {
                // 操作区域
                val args = Bundle().apply {
                    putString(
                        Constant.KEY_TITLE,
                        resources.getString(R.string.select_operation_area_text)
                    )
                    putStringArrayList(
                        Constant.KEY_LIST,
                        resources.getStringArray(R.array.area_lock_array)
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
            EventConstant.EVENT_OPERATION_TYPE -> {
                // 操作类型
                val args = Bundle().apply {
                    putString(
                        Constant.KEY_TITLE,
                        resources.getString(R.string.set_operation_type_text)
                    )
                    putStringArrayList(
                        Constant.KEY_LIST,
                        resources.getStringArray(R.array.type_array).toList() as ArrayList<String>
                    )
                    putParcelable(
                        Constant.KEY_SELECT,
                        CommonListBean(
                            type = EventConstant.EVENT_OPERATION_TYPE,
                            select = vm.typeData.value
                        )
                    )
                }
                switchFragment(
                    ListFragment.newInstance(args),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_OPERATION_LOCK_TAG -> {
                // epc data
                val epc = StrUtils.strToByteArray(vm.epc.value, R.string.edit_epc_text)
                if (epc == null || epc.isEmpty()) return
                this.optEpc = epc
                // optArea
                if (optArea < 0x01 || optArea > 0x05) {
                    ToastUtils.showShort(R.string.hint_please_select_opt_area)
                    return
                }
                // pwd data
                val pwd = StrUtils.strToByteArray(vm.pwd.value, R.string.edit_pwd_text, 4)
                if (pwd == null || pwd.isEmpty()) return
                this.pwd = pwd
                // optType
                if (optType < 0x00 || optType > 0x03) {
                    ToastUtils.showShort(R.string.hint_please_select_opt_type)
                    return
                }
                // operation
                RFIDManager.getInstance().apply {
                    if (isConnect) {
                        helper.registerReaderCall(clearEpcCall)
                        helper.cancelAccessEpcMatch()
                    }
                }
            }
        }
    }


    companion object {
        fun newInstance(args: Bundle?) = LockFragment()
            .apply { arguments = args }
    }
}