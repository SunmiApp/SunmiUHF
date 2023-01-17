package com.sunmi.uhf.fragment.takeinventory

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunmi.uhf.App
import com.sunmi.uhf.R
import com.sunmi.uhf.adapter.LabelInfoAdapter
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.bean.LabelInfoBean
import com.sunmi.uhf.constants.Constant
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentSearchBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.utils.ClipboardUtils
import com.sunmi.uhf.utils.ExcelUtils
import com.sunmi.uhf.utils.LogUtils
import com.sunmi.uhf.utils.ShareUtils
import com.sunmi.uhf.view.RecycleDivider
import com.sunmi.widget.dialog.InputDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @ClassName: SearchModelFragment
 * @Description: 搜索模式选择
 * @Author: clh
 * @CreateDate: 20-9-10 下午1:57
 * @UpdateDate: 20-9-10 下午1:57
 */
class SearchModelFragment : BaseFragment<FragmentSearchBinding>() {
    lateinit var vm: TakeInventoryModel
    lateinit var adapter: LabelInfoAdapter
    private val allList = mutableListOf<LabelInfoBean>()
    private val curList = mutableListOf<LabelInfoBean>()

    override fun getLayoutResource() = R.layout.fragment_search

    override fun initVM() {
        vm = getViewModel(TakeInventoryModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        adapter = LabelInfoAdapter()
        binding.labelRv.layoutManager = LinearLayoutManager(activity)
        binding.labelRv.adapter = adapter
        binding.labelRv.addItemDecoration(
            RecycleDivider(
                activity,
                RecycleDivider.HORIZONTAL_LIST,
                resources
                    .getDimensionPixelSize(R.dimen.sunmi_1px),
                ContextCompat.getColor(App.mContext, R.color.dividerColor)
            )
        )
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                mainScope.launch {
                    binding.searchEt.text?.toString()?.toUpperCase()?.let {
                        if (it.isNotEmpty()) {
                            curList.clear()
                            for (bean in allList) {
                                if (bean.epc?.replace(" ", "")?.contains(it) == true) {
                                    curList.add(bean)
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    override fun initData() {
        arguments?.let {
            it.getParcelableArrayList<LabelInfoBean>(Constant.KEY_TAG_LIST)
                ?.let { al ->
                    allList.addAll(al)
                    curList.addAll(al)
                }
        }
        vm.filterLabelList.value = curList
        vm.filterLabelList.observe(viewLifecycleOwner, Observer {
            adapter.setNewInstance(it)
        })
        vm.editModel.observe(viewLifecycleOwner, Observer {
            adapter?.editable = it
            adapter?.notifyDataSetChanged()
        })
        vm.selectAll.observe(viewLifecycleOwner, Observer {
            if (adapter.selectAll == it) return@Observer
            adapter.selectAll = it
            if (it) {
                if (curList.size != adapter.selectData.size) {
                    for (b in curList) {
                        adapter.selectData[b.epc!!] = b
                    }
                }
            } else {
                adapter.selectData.clear()
            }
            adapter.notifyDataSetChanged()
            handleBottomStatus()
        })
        adapter.selectAllCall = object : ((Boolean) -> Unit) {
            override fun invoke(en: Boolean) {
                if (isVisible) {
                    vm.selectAll.value = en
                }
            }
        }
        adapter.clickCall = object : (() -> Unit) {
            override fun invoke() {
                if (isVisible) {
                    handleBottomStatus()
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
            EventConstant.EVENT_INVENTORY_COPY_EPC -> {
                copyEpcToClipboard()
            }
            EventConstant.EVENT_INVENTORY_SHARE -> {
                shareToApp()
            }
            EventConstant.EVENT_INVENTORY_EXPORT_EXCEL -> {
                if (adapter.selectData.size == 0) {
                    mainScope.launch { showShort(getString(R.string.please_take_select_before_proceeding)) }
                    return
                }
                exportExcel()
            }
        }
    }


    fun handleBottomStatus() {
        vm.editEnExport.postValue(adapter.selectData.size > 0)
    }

    /**
     * 复制EPC到剪贴板
     */
    private fun copyEpcToClipboard() {
        mainScope.launch(Dispatchers.IO) {
            if (adapter.selectData.size == 0) {
                mainScope.launch { showShort(getString(R.string.please_take_select_before_proceeding)) }
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
                showShort(getString(R.string.hint_copy_epc_clipboard))
            }
        }
    }

    /**
     * 分享到其他App
     */
    private fun shareToApp() {
        mainScope.launch(Dispatchers.IO) {
            if (adapter.selectData.size == 0) {
                mainScope.launch { showShort(getString(R.string.please_take_select_before_proceeding)) }
                return@launch
            }
            var dir = App.mContext.externalCacheDir ?: App.mContext.cacheDir
            val data = ArrayList<LabelInfoBean>(adapter.selectData.values)
            var file = ExcelUtils.writeTagToExcel("${dir.absolutePath}/tagList", data)
            mainScope.launch {
                ShareUtils.shareFile(activity, file)
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
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), TakeInventoryFragment.REQUEST_PERMISSION_ID)
                showShort(R.string.please_allow_read_write_sd_card)
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
                    if (text.trim().isEmpty()) {
                        dialog.inputError()
                        return
                    } else {
                        //exportExcel(text, Environment.getExternalStorageDirectory().absolutePath)
                        exportExcel(text, context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString())
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
            data.addAll(adapter.selectData.values)
            if (data.size == 0) {
                return@launch
            }
            ExcelUtils.writeTagToExcel(file, data)
            mainScope.launch {
                showShort(getString(R.string.hint_excel_save_to_sd))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == TakeInventoryFragment.REQUEST_PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportExcel()
            } else {
                showShort(R.string.please_allow_read_write_sd_card)
            }
        }
    }

    companion object {
        fun newInstance(args: Bundle?) = SearchModelFragment()
            .apply { arguments = args }
    }
}