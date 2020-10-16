package com.sunmi.uhf.fragment.takeinventory

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunmi.rfid.RFIDManager
import com.sunmi.rfid.constant.CMD
import com.sunmi.rfid.constant.ParamCts
import com.sunmi.rfid.entity.DataParameter
import com.sunmi.uhf.App
import com.sunmi.uhf.R
import com.sunmi.uhf.adapter.LabelInfoAdapter
import com.sunmi.uhf.adapter.TakeModelAdapter
import com.sunmi.uhf.bean.LabelInfoBean
import com.sunmi.uhf.constants.Config
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentTakeInventoryBinding
import com.sunmi.uhf.dialog.SureBackDialog
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.ReadBaseFragment
import com.sunmi.uhf.utils.*
import com.sunmi.widget.dialog.InputDialog
import com.sunmi.widget.util.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @ClassName: TakeInventoryFragment
 * @Description: 盘存 页面
 * @Author: clh
 * @CreateDate: 20-9-9 下午1:38
 * @UpdateDate: 20-9-9 下午1:38
 */
class TakeInventoryFragment : ReadBaseFragment<FragmentTakeInventoryBinding>() {
    private var dialog: SureBackDialog? = null
    lateinit var vm: TakeInventoryModel
    private var isLoop = false
    private var allCount = 0
    private val list = mutableListOf<LabelInfoBean>()
    private lateinit var adapter: LabelInfoAdapter
    private var takeModelPw: PopupWindow? = null
    private var modelAdapter: TakeModelAdapter? = null
    private var exportExcelType = 0

    override fun getLayoutResource() = R.layout.fragment_take_inventory

    override fun initVM() {
        vm = getViewModel(TakeInventoryModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        adapter = LabelInfoAdapter()
        binding.labelRv.layoutManager = LinearLayoutManager(activity)
        binding.labelRv.adapter = adapter
    }

    override fun initData() {
        super.initData()
        adapter.setNewInstance(list)
        vm.selectModel.value = vm.modelList.first()
        vm.start.observe(viewLifecycleOwner, Observer { startStop(it) })
        vm.editModel.observe(viewLifecycleOwner, Observer {
            adapter.editable = it
            adapter.notifyDataSetChanged()
        })
        vm.selectModel.observe(viewLifecycleOwner, Observer {
            modelAdapter?.selected = it
            modelAdapter?.notifyDataSetChanged()
        })
        vm.selectAll.observe(viewLifecycleOwner, Observer {
            if (adapter.selectAll == it) return@Observer
            adapter.selectAll = it
            if (it) {
                if (list.size != adapter.selectData.size) {
                    for (b in list) {
                        adapter.selectData[b.epc!!] = b
                    }
                }
            } else {
                adapter.selectData.clear()
            }
            adapter.notifyDataSetChanged()
        })
        LiveDataBusEvent.get().with(EventConstant.SELECT_ALL_TAG, Boolean::class.java)
            .observe(viewLifecycleOwner, Observer {
                if (isVisible) {
                    vm.selectAll.value = it
                }
            })
    }

    override fun onSimpleViewEvent(event: SimpleViewEvent) {
        super.onSimpleViewEvent(event)
        when (event.event) {
            EventConstant.EVENT_BACK -> {
                if (isLoop) {
                    showSureDialog()
                } else {
                    performBackClick()
                }
            }
            EventConstant.EVENT_TAKE_MODEL -> {
                showModelPopupWindow()
            }
            EventConstant.EVENT_TAKE_MODEL_SEARCH -> {
                switchFragment(
                    SearchModelFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
            EventConstant.EVENT_INVENTORY_COPY_EPC -> {
                copyEpcToClipboard()
            }
            EventConstant.EVENT_INVENTORY_SHARE -> {
                shareToApp()
            }
            EventConstant.EVENT_INVENTORY_EXPORT_EXCEL -> {
                exportExcelType = 1
                exportExcel()
            }
            EventConstant.EVENT_INVENTORY_EXPORT_EXCEL_ALL -> {
                exportExcelType = 0
                exportExcel()
            }
            EventConstant.EVENT_TAKE_LABEL_INFO -> {

            }
        }

    }

    /**
     * 弹出显示 盘存模式列表
     */
    private fun showModelPopupWindow() {
        if (takeModelPw == null) {
            val root = View.inflate(context, R.layout.pop_take_model, null)
            val recyclerView = root.findViewById<RecyclerView>(R.id.take_mode_rv)
            modelAdapter = TakeModelAdapter()
            takeModelPw = PopupWindow(
                root,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                this.isOutsideTouchable = true
                modelAdapter?.setNewInstance(vm.createModel())
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.adapter = modelAdapter
                modelAdapter?.setOnItemClickListener { _, _, position ->
                    vm.selectModel.value = modelAdapter?.data?.get(position)
                    dismiss()
                }
            }
        }
        takeModelPw?.showAsDropDown(
            binding.filterLl.takeInventoryModelValueTv, 0,
            resources.getDimensionPixelSize(R.dimen.sunmi_8px)
        )
    }

    /**
     * 复制EPC到剪贴板
     */
    private fun copyEpcToClipboard() {
        mainScope.launch(Dispatchers.IO) {
            if (adapter.selectData.size == 0) {
                mainScope.launch { ToastUtils.showLong(getString(R.string.please_take_select_before_proceeding)) }
                return@launch
            }
            val info = StringBuffer()
            for (epc in adapter.selectData.keys) {
                if (info.isNotEmpty()) {
                    info.append("\n")
                }
                info.append(epc)
            }
            LogUtils.i("darren", "copy to clipboard: $info")
            mainScope.launch {
                ClipboardUtils.copyStrToClipboard(context, info.toString())
                ToastUtils.showLong(getString(R.string.hint_copy_epc_clipboard))
            }
        }
    }

    /**
     * 分享到其他App
     */
    private fun shareToApp() {
        mainScope.launch(Dispatchers.IO) {
            if (adapter.selectData.size == 0) {
                mainScope.launch { ToastUtils.showLong(getString(R.string.please_take_select_before_proceeding)) }
                return@launch
            }
            var dir = App.mContext.externalCacheDir ?: App.mContext.cacheDir
            val data = ArrayList<LabelInfoBean>(adapter.selectData.values)
            var file = ExcelUtils.writeTagToExcel("${dir.absolutePath}/tagList", data)
            mainScope.launch {
                ShareUtils.shareFile(App.mContext, file)
            }
        }
    }

    /**
     *  导出Excel 权限/文件名
     *
     *  @param type 类型 0：全部，1：选择的
     */
    private fun exportExcel() {
        context?.let {
            if (ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_ID)
                ToastUtils.showShort(R.string.please_allow_read_write_sd_card)
                return
            }
        }
        val dialog = InputDialog.Builder()
            .setTitle(getString(R.string.please_input_file_name))
            .setHint(getString(R.string.please_input_file_name))
            .setEditType(true)
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
                    if (text.isEmpty()) {
                        dialog.inputError()
                        return
                    } else {
                        exportExcel(text, Environment.getExternalStorageDirectory().absolutePath)
                        dialog.dismiss()
                    }
                } else {
                    dialog.inputError()
                }
            }
        })
        dialog.show()
    }

    /**
     * 保存Excel文件到SD卡
     */
    private fun exportExcel(fileName: String, path: String) {
        mainScope.launch(Dispatchers.IO) {
            LogUtils.i("darren", "export Excel dir:$path")
            val file = "$path/$fileName"
            val data = ArrayList<LabelInfoBean>()
            var resTips = R.string.please_take_inventory_before_proceeding
            if (exportExcelType == 0) {
                data.addAll(list)
            } else if (exportExcelType == 1) {
                data.addAll(adapter.selectData.values)
                resTips = R.string.please_take_select_before_proceeding
            }
            if (data.size == 0) {
                mainScope.launch { ToastUtils.showLong(getString(resTips)) }
                return@launch
            }
            ExcelUtils.writeTagToExcel(file, data)
            mainScope.launch {
                ToastUtils.showLong(getString(R.string.hint_excel_save_to_sd))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportExcel()
            } else {
                ToastUtils.showShort(R.string.please_allow_read_write_sd_card)
            }
        }
    }

    /**
     * 点击返回健后，弹出二次确认弹窗
     * 点击退出 ，退出页面
     */
    private fun showSureDialog() {
        val showing = (dialog?.isAdded ?: false || dialog?.dialog?.isShowing ?: false)
        if (showing) dialog?.dismiss()
        if (dialog == null) {
            dialog = SureBackDialog.newInstance(null)
        }
        dialog?.listener = object : (() -> Unit) {
            override fun invoke() {
                dialog?.dismiss()
                stop()
                handler.post(Runnable { performBackClick() })
            }
        }
        dialog?.show(parentFragmentManager, SureBackDialog::class.java.name)
    }

    override fun onBackPress(): Boolean {
        if (isLoop) {
            showSureDialog()
            return true
        }
        return super.onBackPress()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dialog?.dismiss()
        takeModelPw?.dismiss()
    }


    private fun startStop(en: Boolean) {
        if (en) {
            tidList.clear()
            tagList.clear()
            list.clear()
            allCount = 0
            vm.labelNum.value = 0
            vm.totalNum.value = 0
            vm.speed.value = 0f
            binding.basicLl.timeValueTv.base = SystemClock.elapsedRealtime()
            binding.basicLl.timeValueTv.start()
            notifyTagDataChange()
            start()
        } else {
            binding.basicLl.timeValueTv.stop()
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
                            realTimeInventory(20)
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
            CMD.REAL_TIME_INVENTORY -> {
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
        playTips()
        when (cmd) {
            CMD.REAL_TIME_INVENTORY -> {
                // 6C标签盘存
                allCount++
                // ANT_ID、TAG_PC、TAG_EPC、TAG_RSSI、TAG_READ_COUNT、TAG_FREQ、TAG_TIME
                val epc = tag.getString(ParamCts.TAG_EPC) ?: ""
                val pc = tag.getString(ParamCts.TAG_PC) ?: ""
                val rssi = "${(Integer.parseInt(tag.getString(ParamCts.TAG_RSSI, "129")) - 129)}dBm"
                val freq = tag.getString(ParamCts.TAG_FREQ) ?: ""
                LogUtils.i("darren", "found tag:$epc => $tag")
                val index = tidList.indexOf(epc)
                if (index != -1) {
                    val c = tagList[index].getInt(ParamCts.TAG_READ_COUNT, 1) + 1
                    tag.put(ParamCts.TAG_READ_COUNT, c)
                    tagList[index] = tag
                    list[index] = LabelInfoBean(epc, pc, c, rssi, "${freq}MHz")
                } else {
                    tidList.add(0, epc)
                    tagList.add(0, tag)
                    list.add(0, LabelInfoBean(epc, pc, 1, rssi, "${freq}MHz"))
                }
                notifyTagDataChange()
            }
            /*CMD.ISO18000_6B_INVENTORY -> {
                allCount++
                val uid = tag.getString(ParamCts.TAG_UID) ?: ""
                LogUtils.i("darren", "found tag:$uid")
                val index: Int = tidList.indexOf(uid)
                if (index != -1) {
                    tagList[index] = tag
                } else {
                    tidList.add(0, uid)
                    tagList.add(0, tag)
                }
                notifyTagDataChange()
            }*/
            else -> {
                LogUtils.d("darren", "other found tag.")
            }
        }
    }

    override fun onCallFailed(cmd: Byte, errorCode: Byte, msg: String?) {
        when (cmd) {
            CMD.REAL_TIME_INVENTORY -> {
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

    private fun notifyTagDataChange() {
        mainScope.launch {
            vm.labelNum.value = tidList.size
            vm.totalNum.value = allCount
            val time = (SystemClock.elapsedRealtime() - binding.basicLl.timeValueTv.base) / 1000
            if (time < 1) {
                vm.speed.value = allCount.toFloat()
            } else {
                vm.speed.value = allCount * 10 / time / 10f
            }
            adapter.notifyDataSetChanged()
        }
    }

    companion object {
        fun newInstance(args: Bundle?) = TakeInventoryFragment()
            .apply { arguments = args }

        const val CHOOSE_FILE_CODE = 100
        const val REQUEST_PERMISSION_ID = 101
    }
}