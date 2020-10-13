package com.sunmi.uhf.fragment.setting.child

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.Nullable
import com.sunmi.uhf.App
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentFirmwareUpdateBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.setting.SettingModel
import com.sunmi.uhf.utils.ContentUriUtil.getPath


/**
 * @ClassName: FirmwareUpdateFragment
 * @Description: 设置  固件升级
 * @Author: clh
 * @CreateDate: 20-9-14 下午5:47
 * @UpdateDate: 20-9-14 下午5:47
 */
class FirmwareUpdateFragment : BaseFragment<FragmentFirmwareUpdateBinding>() {
    lateinit var vm: SettingModel
    override fun getLayoutResource() = R.layout.fragment_firmware_update

    override fun initVM() {
        vm = getViewModel(SettingModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        vm.title.value = resources.getString(R.string.setting_about_device_text)
    }

    override fun initData() {
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

        }
    }

    // 打开系统的文件选择器
    // 调用系统文件管理器
    private fun chooseFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("application/bin").addCategory(Intent.CATEGORY_OPENABLE)
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


    companion object {
        fun newInstance(args: Bundle?) = FirmwareUpdateFragment()
            .apply { arguments = args }

        const val CHOOSE_FILE_CODE = 100
    }
}