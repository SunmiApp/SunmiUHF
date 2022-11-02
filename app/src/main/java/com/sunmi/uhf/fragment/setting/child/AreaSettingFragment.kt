package com.sunmi.uhf.fragment.setting.child

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.sunmi.uhf.databinding.FragmentAreaSettingBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.list.ListFragment
import com.sunmi.uhf.fragment.setting.SettingModel
import com.sunmi.uhf.utils.LiveDataBusEvent
import com.sunmi.uhf.utils.LogUtils
import kotlinx.coroutines.launch

/**
 * @ClassName: AreaSettingFragment
 * @Description: 设置，区域设置
 * @Author: clh
 * @CreateDate: 20-9-14 下午5:10
 * @UpdateDate: 20-9-14 下午5:10
 */
class AreaSettingFragment : BaseFragment<FragmentAreaSettingBinding>() {
    lateinit var vm: SettingModel
    private var rfBand: IntArray = intArrayOf(-1, -1, -1, -1)
    private var rfRegion: Int = -1
    private var rfStart: Int = -1
    private var rfEnd: Int = -1
    private var rfInterval: Int = -1
    private var rfQuantity: Int = -1
    private var countryList: ArrayList<String> = ArrayList()
    private var fqIntervalList: ArrayList<String> = ArrayList()
    private var fqQuantityList: ArrayList<String> = ArrayList()
    private var sn: String = ""
    private val br = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null || intent.action == null) return
            LogUtils.i("darren", "receiver: " + intent.action)
            when (intent.action) {
                ParamCts.BROADCAST_SN -> {
                    sn = intent.getStringExtra(ParamCts.SN) ?: ""
                    notifyDataChange()
                }
                ParamCts.BROADCAST_READER_BOOT -> initData()
                else -> {
                }
            }
        }
    }
    private val optCall = object : ReaderCall() {
        override fun onSuccess(cmd: Byte, params: DataParameter?) {
            if (BuildConfig.DEBUG) {
                LogUtils.i(
                    "darren",
                    String.format("CMD: 0x%02X, params info: %s", cmd, params?.toString() ?: "")
                )
            }
            when (cmd) {
                CMD.GET_FREQUENCY_REGION -> {
                    params?.let {
                        rfRegion = it.getByte(ParamCts.FREQUENCY_REGION).toInt()
                        when (rfRegion) {
                            in 1..3 -> {
                                rfStart = it.getByte(ParamCts.FREQUENCY_START).toInt()
                                rfEnd = it.getByte(ParamCts.FREQUENCY_END).toInt()
                            }
                            4 -> {
                                rfStart =
                                    it.getInt(ParamCts.USER_DEFINE_START_FREQUENCY, 865_000) / 1000
                                rfInterval =
                                    it.getByte(ParamCts.USER_DEFINE_FREQUENCY_INTERVAL, 0x01)
                                        .toInt()
                                rfQuantity =
                                    it.getByte(ParamCts.USER_DEFINE_CHANNEL_QUANTITY, 0x01).toInt()
                            }
                        }
                        notifyDataChange()
                    }
                }
                CMD.SET_FREQUENCY_REGION -> {
                    RFIDManager.getInstance().apply {
                        if (isConnect()) {
                            getHelper()?.getFrequencyRegion()
                            showShort(getString(R.string.hint_operation_success))
                        }
                    }
                }
                else -> {

                }
            }
        }

        override fun onTag(cmd: Byte, state: Byte, tag: DataParameter?) {
            // nope
        }

        override fun onFailed(cmd: Byte, errorCode: Byte, msg: String?) {
            if (BuildConfig.DEBUG) {
                LogUtils.e(
                    "darren",
                    String.format(
                        "CMD: 0x%02X, Error Code: 0x%02X, msg info: %s",
                        cmd,
                        errorCode,
                        msg
                    )
                )
            }
            mainScope.launch {
                showShort(getString(R.string.hint_operation_failed, cmd, errorCode, msg))
            }
        }

    }

    override fun getLayoutResource() = R.layout.fragment_area_setting

    override fun initVM() {
        vm = getViewModel(SettingModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
//        vm.title.value = resources.getString(R.string.setting_select_area_text)
    }

    override fun initData() {
        RFIDManager.getInstance().apply {
            if (isConnect() && getHelper()?.getScanModel() != RFIDManager.NONE) {
                getHelper()?.apply {
                    registerReaderCall(optCall)
                    when (getScanModel()) {
                        // UHF R2000
                        RFIDManager.UHF_R2000, RFIDManager.UHF_S7100 -> {
                            vm.title.value = resources.getString(R.string.setting_select_area_text)
                            getReaderSN()
                            getFrequencyRegion()
                            vm.isInner.postValue(false)
                        }
                        RFIDManager.INNER -> {
                            vm.title.value = resources.getString(R.string.setting_frequency_text)
                            binding.moduleNameTv.text = getString(R.string.module_type_inner)
                            fqIntervalList.clear()
                            fqQuantityList.clear()
                            for (i in 1..88) {
                                fqIntervalList.add("$i")
                            }
                            for (i in 1..60) {
                                fqQuantityList.add("$i")
                            }
                            getReaderSN()
                            getFrequencyRegion()
                            vm.isInner.postValue(true)
                        }
                        else -> {
                            binding.moduleNameTv.text = ""
                        }
                    }
                }
            }
        }
        LiveDataBusEvent.get().with(EventConstant.LABEL_SELECT, CommonListBean::class.java)
            .observe(viewLifecycleOwner, Observer {
                if (isVisible) {
                    LogUtils.i("darren", "select ${it.type} -> ${it.select}(${it.index})")
                    when (it.type) {
                        EventConstant.EVENT_AREA_COUNTRY -> {
                            setCountryRF(it.select ?: "")
                        }
                        EventConstant.EVENT_AREA_RF_START,
                        EventConstant.EVENT_AREA_RF_END -> {
                            setSelectRF(it.type, it.select)
                        }
                        EventConstant.EVENT_FQ_INTERVAL,
                        EventConstant.EVENT_FQ_QUANTITY -> {
                            setUserDefineFq(it.type, it.select)
                        }
                    }
                }
            })
    }

    override fun onSimpleViewEvent(event: SimpleViewEvent) {
        super.onSimpleViewEvent(event)
        when (event.event) {
            EventConstant.EVENT_BACK -> {
                performBackClick()
            }
            EventConstant.EVENT_AREA_COUNTRY -> {
                val args = Bundle().apply {
                    putString(
                        Constant.KEY_TITLE,
                        resources.getString(R.string.hint_please_select_opt_area_country)
                    )
                    putStringArrayList(Constant.KEY_LIST, countryList)
                    putParcelable(
                        Constant.KEY_SELECT,
                        CommonListBean(
                            type = EventConstant.EVENT_AREA_COUNTRY,
                            select = binding.areaCountryTv.text.toString()
                        )
                    )
                }
                switchFragment(
                    ListFragment.newInstance(args),
                    addToBackStack = true,
                    clearStack = false
                )
            }

            EventConstant.EVENT_AREA_RF_START,
            EventConstant.EVENT_AREA_RF_END -> {
                showRFSelect(event.event)
            }
            //频点间隔选择
            EventConstant.EVENT_FQ_INTERVAL -> {
                val args = Bundle().apply {
                    putString(
                        Constant.KEY_TITLE,
                        resources.getString(R.string.hint_please_select_fq_interval)
                    )
                    putStringArrayList(Constant.KEY_LIST, fqIntervalList)
                    putParcelable(
                        Constant.KEY_SELECT,
                        CommonListBean(
                            type = EventConstant.EVENT_FQ_INTERVAL,
                            select = binding.tvFqInterval.text.toString()
                        )
                    )
                }
                switchFragment(
                    ListFragment.newInstance(args),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            //频点数量选择
            EventConstant.EVENT_FQ_QUANTITY -> {
                val args = Bundle().apply {
                    putString(
                        Constant.KEY_TITLE,
                        resources.getString(R.string.hint_please_select_fq_quantity)
                    )
                    putStringArrayList(Constant.KEY_LIST, fqQuantityList)
                    putParcelable(
                        Constant.KEY_SELECT,
                        CommonListBean(
                            type = EventConstant.EVENT_FQ_QUANTITY,
                            select = binding.tvFqQuantity.text.toString()
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

    private fun showRFSelect(id: Int) {
        val list = ArrayList<String>()
        var str = ""
        var title = ""
        when (id) {
            EventConstant.EVENT_AREA_RF_START -> {
                title = resources.getString(R.string.hint_please_select_opt_start_rf)
                str = binding.rfStartTv.text.toString()
                RFIDManager.getInstance().apply {
                    if (isConnect()) {
                        getHelper()?.apply {
                            when (getScanModel()) {
                                RFIDManager.UHF_R2000, RFIDManager.UHF_S7100 -> {
                                    val end = ParamCts.getParamsToRf(rfEnd).toInt()
                                    for (i in rfBand[1]..end) {
                                        list.add("$i.0 MHz")
                                        if (i < end) {
                                            list.add("$i.5 MHz")
                                        }
                                    }
                                }
                                RFIDManager.INNER -> {
                                    val band = ParamCts.getRFFrequencyBand(getScanModel(), sn)
                                    val end = band[2]
                                    for (i in band[1]..end) {
                                        list.add("$i.0 MHz")
                                        if (i < end) {
                                            list.add("$i.5 MHz")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            EventConstant.EVENT_AREA_RF_END -> {
                title = resources.getString(R.string.hint_please_select_opt_end_rf)
                str = binding.rfEndTv.text.toString()
                val start = ParamCts.getParamsToRf(rfStart).toInt()
                for (i in start..rfBand[2]) {
                    list.add("$i.0 MHz")
                    if (i < rfBand[2]) {
                        list.add("$i.5 MHz")
                    }
                }
            }
        }
        val args = Bundle().apply {
            putString(Constant.KEY_TITLE, title)
            putStringArrayList(Constant.KEY_LIST, list)
            putParcelable(Constant.KEY_SELECT, CommonListBean(type = id, select = str))
        }
        switchFragment(ListFragment.newInstance(args), addToBackStack = true, clearStack = false)
    }

    private fun setSelectRF(type: Int?, select: String?) {
        if (select?.isNotEmpty() == true && select.contains("MHz")) {
            val v = ParamCts.getRfToParams((select.replace(" MHz", "").toFloat() * 10).toInt())
            when (type) {
                EventConstant.EVENT_AREA_RF_START -> {
                    rfStart = v
                    mainScope.launch { binding.rfStartTv.text = select }
                }
                EventConstant.EVENT_AREA_RF_END -> {
                    rfEnd = v
                    mainScope.launch { binding.rfEndTv.text = select }
                }
            }
            RFIDManager.getInstance().apply {
                if (isConnect()) {
                    when (rfRegion) {
                        in 1..3 -> {
                            getHelper()?.setFrequencyRegion(
                                rfRegion.toByte(),
                                rfStart.toByte(),
                                rfEnd.toByte()
                            )
                        }
                        4 -> {
                            getHelper()?.setUserDefineFrequency(
                                rfInterval.toByte(),
                                rfQuantity.toByte(),
                                (select.replace(" MHz", "").toFloat() * 1000).toInt()
                            )
                        }
                    }
                }
            }
        }
    }

    fun notifyDataChange() {
        mainScope.launch {
            RFIDManager.getInstance().apply {
                if (isConnect()) {
                    LogUtils.d("darren", "show data...")
                    when (getHelper()?.getScanModel()) {
                        RFIDManager.UHF_R2000, RFIDManager.UHF_S7100 -> {
                            if (sn.isNotEmpty()) {
                                rfBand = ParamCts.getRFFrequencyBand(sn)
                            }
                            var res: Int
                            //CHN(中规频段 920~925MHz)0x03 CHN  输出功率：29(默认)
                            //CHN(美规频段 902~928MHz)0x01 FCC  输出功率：28(默认)
                            //CHN(欧规频段 865~868MHz)0x02 ETSI 输出功率：28(默认)
                            when (getRFRegion()) {
                                //FCC
                                0x01 -> {
                                    res = R.array.area_country_america_array
                                    binding.moduleNameTv.text =
                                        getString(R.string.module_type_america)
                                    if (rfBand[0] != 1) {
                                        rfBand[0] = 1
                                        rfBand[1] = 902
                                        rfBand[2] = 928
                                        rfBand[3] = 0x01
                                    }
                                }
                                //ETSI
                                0x02 -> {
                                    res = R.array.area_country_europe_array
                                    binding.moduleNameTv.text =
                                        getString(R.string.module_type_europe)
                                    if (rfBand[0] != 1) {
                                        rfBand[0] = 1
                                        rfBand[1] = 865
                                        rfBand[2] = 868
                                        rfBand[3] = 0x02
                                    }
                                }
                                //CHN
                                0x03 -> {
                                    res = R.array.area_country_china_array
                                    binding.moduleNameTv.text =
                                        getString(R.string.module_type_china)
                                    if (rfBand[0] != 1) {
                                        rfBand[0] = 1
                                        rfBand[1] = 920
                                        rfBand[2] = 925
                                        rfBand[3] = 0x03
                                    }
                                }
                                else -> {
                                    res = R.array.area_country_america_array
                                    binding.moduleNameTv.text = ""
                                    if (rfBand[0] != 1) {
                                        rfBand[0] = 1
                                        rfBand[1] = 902
                                        rfBand[2] = 928
                                        rfBand[3] = 0x01
                                    }
                                }
                            }

                            if (rfRegion != rfBand[3]) rfRegion = rfBand[3]

                            countryList.clear()
                            countryList.addAll(resources.getStringArray(res).toList())

                            if (rfStart == -1 || rfEnd == -1) {
                                binding.rfStartTv.text = ""
                                binding.rfEndTv.text = ""
                            } else {
                                val startRf = ParamCts.getParamsToRf(rfStart)
                                val endRf = ParamCts.getParamsToRf(rfEnd)
                                binding.rfStartTv.text = getString(R.string.x_mhz, startRf.toInt())
                                binding.rfEndTv.text = getString(R.string.x_mhz, endRf.toInt())
                                var isFindCountry = true
                                for (s in countryList) {
                                    val rf = getCountryRF(s)
                                    if (rf?.size == 2 && startRf == rf[0] && endRf == rf[1]) {
                                        binding.areaCountryTv.text = s
                                        isFindCountry = false
                                        break
                                    }
                                }
                                if (isFindCountry) {
                                    binding.areaCountryTv.text =
                                        getString(R.string.hint_please_auto_set)
                                }
                            }

                        }
                        RFIDManager.INNER -> {
                            binding.moduleNameTv.text = getString(R.string.module_type_inner)
                            binding.areaCountryTv.text = ""
                            binding.rfStartTv.text = getString(R.string.x_mhz, rfStart)
                            binding.rfEndTv.text = ""
                            binding.tvFqInterval.text = rfInterval.toString()
                            binding.tvFqQuantity.text = rfQuantity.toString()
                        }
                        else -> {
                            binding.moduleNameTv.text = ""
                            binding.areaCountryTv.text = ""
                            binding.rfStartTv.text = ""
                            binding.rfEndTv.text = ""
                        }
                    }
                }
            }
        }
    }

    private fun getRFRegion() = if (rfBand[0] == 1) rfBand[3] else rfRegion

    private fun setCountryRF(rf: String) {
        mainScope.launch {
            binding.areaCountryTv.text = rf
        }
        RFIDManager.getInstance().apply {
            if (isConnect()) {
                getCountryRF(rf)?.let {
                    val startRf = ParamCts.getRfToParams((it[0] * 10).toInt())
                    val endRf = ParamCts.getRfToParams((it[1] * 10).toInt())
                    getHelper()?.setFrequencyRegion(
                        getRFRegion().toByte(),
                        startRf.toByte(),
                        endRf.toByte()
                    )
                }
            }
        }
    }

    private fun getCountryRF(str: String): DoubleArray? {
        if (str.isNotEmpty() && str.contains("-")) {
            val i = str.indexOf("-")
            val start = str.substring(i - 3, i).toDouble()
            val end = str.substring(i + 1, i + 4).toDouble()
            return doubleArrayOf(start, end)
        }
        return null
    }

    /**
     * 自定义频点设置
     */
    private fun setUserDefineFq(type: Int?, select: String?) {
        select?.let {
            if (it.isNotEmpty()) {
                when (type) {
                    //频点间隔设置
                    EventConstant.EVENT_FQ_INTERVAL -> {
                        binding.tvFqInterval.text = it
                        rfInterval = it.toInt()
                    }
                    //频点数量设置
                    EventConstant.EVENT_FQ_QUANTITY -> {
                        binding.tvFqQuantity.text = it
                        rfQuantity = it.toInt()
                    }
                }
                RFIDManager.getInstance().apply {
                    if (isConnect()) {
                        getHelper()?.setUserDefineFrequency(
                            rfInterval.toByte(),
                            rfQuantity.toByte(),
                            rfStart * 1000
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        context?.registerReceiver(br, IntentFilter().apply {
            addAction(ParamCts.BROADCAST_SN)
            addAction(ParamCts.BROADCAST_READER_BOOT)
        })
    }

    override fun onStop() {
        super.onStop()
        context?.unregisterReceiver(br)
        RFIDManager.getInstance().getHelper()?.unregisterReaderCall()
    }

    companion object {
        fun newInstance(args: Bundle?) = AreaSettingFragment()
            .apply { arguments = args }
    }
}