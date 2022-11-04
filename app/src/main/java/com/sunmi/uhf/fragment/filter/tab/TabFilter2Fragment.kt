package com.sunmi.uhf.fragment.filter.tab

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import com.sunmi.rfid.RFIDManager
import com.sunmi.rfid.ReaderCall
import com.sunmi.rfid.constant.CMD
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
import com.sunmi.uhf.databinding.LayoutTabFilterBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.filter.LabelFilterModel
import com.sunmi.uhf.fragment.filter.select.FilterRuleFragment
import com.sunmi.uhf.fragment.list.ListFragment
import com.sunmi.uhf.utils.LiveDataBusEvent
import com.sunmi.uhf.utils.LogUtils
import com.sunmi.uhf.utils.StrUtils
import kotlinx.coroutines.launch

/**
 * @ClassName: TabFilter1Fragment
 * @Description: 标签过滤 ，过滤器2
 * @Author: clh
 * @CreateDate: 20-9-14 上午9:59
 * @UpdateDate: 20-9-14 上午9:59
 */
class TabFilter2Fragment : BaseFragment<LayoutTabFilterBinding>() {

    lateinit var vm: LabelFilterModel
    private val optCall = object : ReaderCall() {
        override fun onSuccess(cmd: Byte, params: DataParameter?) {
            if (BuildConfig.DEBUG) LogUtils.d("darren", String.format("CMD: 0x%02X, params info: %s", cmd, params?.toString() ?: ""))
            when (cmd) {
                CMD.OPERATE_TAG_MASK -> {
                    mainScope.launch {
                        showShort(getString(R.string.hint_tag_operation_success))
                    }
                }
            }
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
            mainScope.launch {
                when (cmd) {
                    CMD.OPERATE_TAG_MASK -> {
                        showShort(getString(R.string.hint_tag_operation_failed, errorCode, msg))
                    }
                }
            }
        }
    }

    override fun getLayoutResource() = R.layout.layout_tab_filter

    override fun initVM() {
        vm = getViewModel(LabelFilterModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        vm.mEpcData.value = App.getPref().getParam(Config.KEY_FILTER_INFO_2, Config.DEF_FILTER_INFO)
        vm.mBlockData.value = resources.getStringArray(R.array.area_read_write_array)[
                App.getPref().getParam(Config.KEY_FILTER_AREA_2, Config.DEF_FILTER_AREA)]
        vm.mOffSetData.value = App.getPref().getParam(Config.KEY_FILTER_START_ADD_2, Config.DEF_FILTER_START_ADD).toString()
        vm.mFilterRuleData.value = App.getPref().getParam(Config.KEY_FILTER_RULE_2, Config.DEF_FILTER_RULE).toInt().toString()
        val list = resources.getStringArray(R.array.session_array).toList() as ArrayList<String>
        list.add("SL")
        vm.mTargetData.value = list[App.getPref().getParam(Config.KEY_FILTER_TARGET_2, Config.DEF_FILTER_TARGET)]
        vm.isStartFlag.value = App.getPref().getParam(Config.KEY_FILTER_ENABLE_2, Config.DEF_FILTER_ENABLE)
        binding.epcEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                /*val maskValue = StrUtils.stringToByteArray(binding.epcEt.text.toString())
                val maskStr = StrUtils.byteArrayToString(maskValue, 0, maskValue.size)*/
                val content = binding.epcEt.text.toString().replace(" ", "")
                val maskValue = StrUtils.hexToByteArr(content)
                val maskStr = StrUtils.byteArrToHex(maskValue, false)
                App.getPref().setParam(Config.KEY_FILTER_INFO_2, maskStr)
            }
        })
        binding.startAddEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.startAddEt.text.isNullOrEmpty()) {
                    showShort(R.string.hint_filter_offset_len)
                }
                try {
                    val v = binding.startAddEt.text.toString()
                    var startAdd = if (v.isNullOrEmpty()) 0 else v.toInt()
                    if (startAdd > 255) {
                        showShort(R.string.hint_filter_offset_len)
                        startAdd = 255
                    }
                    App.getPref().setParam(Config.KEY_FILTER_START_ADD_2, startAdd)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun initData() {
        LiveDataBusEvent.get().with(EventConstant.LABEL_RULE_INDEX, String::class.java)
            .observe(viewLifecycleOwner, Observer {
                if (isVisible) {
                    vm.mFilterRuleData.value = it
                }
            })
        LiveDataBusEvent.get().with(EventConstant.LABEL_SELECT, CommonListBean::class.java)
            .observe(viewLifecycleOwner, Observer {
                if (isVisible) {
                    when (it.type) {
                        EventConstant.EVENT_BLOCK_CLICK -> vm.mBlockData.value = it.select
                        EventConstant.EVENT_TARGET_CLICK -> vm.mTargetData.value = it.select
                    }
                }
            })
        LiveDataBusEvent.get().with(EventConstant.LABEL_FILTER_DATA, DataParameter::class.java)
            .observe(viewLifecycleOwner, Observer {
                if (it.containsKey(ParamCts.MASK_ID) && it.getByte(ParamCts.MASK_ID) == 0x01.toByte()) {
                    LogUtils.i("darren", "receive-2:${it}")
                    val maskValue = it.getByteArray(ParamCts.MASK_VALUE) ?: byteArrayOf()
                    //val maskStr = StrUtils.byteArrayToString(maskValue, 0, maskValue.size)
                    val maskStr = StrUtils.byteArrToHex(maskValue, false)
                    App.getPref().setParam(Config.KEY_FILTER_INFO_2, maskStr)
                    val area = it.getByte(ParamCts.MASK_MEMBANK, Config.DEF_FILTER_AREA.toByte())
                    App.getPref().setParam(Config.KEY_FILTER_AREA_2, area)
                    val startAdd = it.getByte(ParamCts.MASK_START_ADD)
                    App.getPref().setParam(Config.KEY_FILTER_START_ADD_2, startAdd)
                    val rule = it.getByte(ParamCts.MASK_ACTION)
                    App.getPref().setParam(Config.KEY_FILTER_RULE_2, rule)
                    val target = it.getByte(ParamCts.MASK_TARGET)
                    App.getPref().setParam(Config.KEY_FILTER_TARGET_2, target)
                    initView()
                }
            })

        vm.isStartFlag.observe(viewLifecycleOwner, Observer { setMaskTag(it) })
    }

    override fun onSimpleViewEvent(event: SimpleViewEvent) {
        super.onSimpleViewEvent(event)
        when (event.event) {
            EventConstant.EVENT_BLOCK_CLICK -> {
                //操作区域
                val args = Bundle().apply {
                    putString(
                        Constant.KEY_TITLE,
                        resources.getString(R.string.select_operation_area_text)
                    )
                    putStringArrayList(
                        Constant.KEY_LIST,
                        resources.getStringArray(R.array.area_read_write_array).toList() as ArrayList<String>
                    )
                    putParcelable(
                        Constant.KEY_SELECT,
                        CommonListBean(
                            type = EventConstant.EVENT_BLOCK_CLICK,
                            select = vm.mTargetData.value
                        )
                    )
                }
                switchFragment(
                    ListFragment.newInstance(args),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_FILTER_RULE -> {
                //过滤规则
                val args = Bundle().apply {
                    putInt(Constant.KEY_RULE, vm.mFilterRuleData.value?.toInt() ?: 0)
                }
                switchFragment(
                    FilterRuleFragment.newInstance(args),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_TARGET_CLICK -> {
                //目标 session
                val args = Bundle().apply {
                    val list = resources.getStringArray(R.array.session_array)
                        .toList() as ArrayList<String>
                    list.add("SL")
                    putString(Constant.KEY_TITLE, resources.getString(R.string.select_session_text))
                    putStringArrayList(Constant.KEY_LIST, list)
                    putParcelable(
                        Constant.KEY_SELECT,
                        CommonListBean(
                            type = EventConstant.EVENT_TARGET_CLICK,
                            select = vm.mTargetData.value
                        )
                    )
                }
                switchFragment(
                    ListFragment.newInstance(args),
                    addToBackStack = true,
                    clearStack = false
                )
            }
        }
    }

    private fun setMaskTag(en: Boolean) {
        if (App.getPref().getParam(Config.KEY_FILTER_ENABLE_2, false) == en) return
        App.getPref().setParam(Config.KEY_FILTER_ENABLE_2, en)
        RFIDManager.getInstance().apply {
            if (isConnect()) {
                getHelper()?.registerReaderCall(optCall)
                if (en) {
                    val info = App.getPref().getParam(Config.KEY_FILTER_INFO_2, Config.DEF_FILTER_INFO)
                    val area = App.getPref().getParam(Config.KEY_FILTER_AREA_2, Config.DEF_FILTER_AREA)
                    val startAdd = App.getPref().getParam(Config.KEY_FILTER_START_ADD_2, Config.DEF_FILTER_START_ADD)
                    val rule = App.getPref().getParam(Config.KEY_FILTER_RULE_2, Config.DEF_FILTER_RULE)
                    val target = App.getPref().getParam(Config.KEY_FILTER_TARGET_2, Config.DEF_FILTER_TARGET)
                    val infoList = StrUtils.stringToStringArray(info, 2)
                    val maskValue = StrUtils.stringArrayToByteArray(infoList, infoList?.size ?: 0)
                    getHelper()?.setTagMask(
                        0x02,
                        target.toByte(),
                        rule.toByte(),
                        area.toByte(),
                        startAdd.toByte(),
                        ((maskValue?.size ?: 0) * 8).toByte(),
                        maskValue
                    )
                } else {
                    getHelper()?.clearTagMask(0x02)
                }
            }
        }
    }

    companion object {
        fun newInstance(args: Bundle?) = TabFilter2Fragment()
            .apply { arguments = args }
    }
}