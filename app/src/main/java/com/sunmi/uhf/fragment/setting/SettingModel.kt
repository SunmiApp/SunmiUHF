package com.sunmi.uhf.fragment.setting

import androidx.lifecycle.MutableLiveData
import com.sunmi.uhf.base.BaseViewModel
import com.sunmi.uhf.constants.EventConstant

/**
 * @ClassName: LabelFilterModel
 * @Description: java类作用描述
 * @Author: clh
 * @CreateDate: 20-9-11 下午6:33
 * @UpdateDate: 20-9-11 下午6:33
 */
class SettingModel : BaseViewModel() {

    /*  title 信息 */
    val title = MutableLiveData<String>()

    /* 标签名字 */
    val labelName = MutableLiveData<String>()

    /* 手柄触发方式 */
    val mHandleType = MutableLiveData<String>()

    /* 固件路劲 */
    val mBinPath = MutableLiveData<String>()

    /* 选择的 盘存模式 */
    val mInventoryMode = MutableLiveData<Int>()

    /* 盘存模式  自定义 射频link */
    val mRFLink = MutableLiveData<String>()

    /* 盘存模式  自定义 模式 */
    val mSession = MutableLiveData<String>()


    /* 固件更新中 */
    val updating = MutableLiveData<Boolean>(false)

    /* 固件更新进度 */
    val updateProgress = MutableLiveData<Int>(0)

    /**
     * 返回点击事件
     */
    fun onBackClick() {
        EventConstant.EVENT_BACK.publish()
    }

    /**
     * 标签选择点击事件
     */
    fun onSelectLabelClick() {
        EventConstant.EVENT_SELECT_LABEL.publish()
    }

    /**
     * 手柄选择点击事件
     */
    fun onSelectHandleClick() {
        EventConstant.EVENT_SELECT_HANDLE.publish()
    }

    /**
     * 盘存模式点击事件
     */
    fun onInventoryModeClick() {
        EventConstant.EVENT_INVENTORY_MODE.publish()
    }

    /**
     * 区域设置点击事件
     */
    fun onAreaSettingClick() {
        EventConstant.EVENT_AREA_SETTING.publish()
    }

    /**
     * 区域设置 - 国家
     */
    fun onAreaCountryClick() {
        EventConstant.EVENT_AREA_COUNTRY.publish()
    }

    /**
     * 区域设置 - 开始频率
     */
    fun onRFStartClick() {
        EventConstant.EVENT_AREA_RF_START.publish()
    }

    /**
     * 区域设置 - 结束频率
     */
    fun onRFEndClick() {
        EventConstant.EVENT_AREA_RF_END.publish()
    }

    /**
     * 常规设置点击事件
     */
    fun onCommonSettingClick() {
        EventConstant.EVENT_COMMON_SETTING.publish()
    }

    /**
     * 关于设备点击事件
     */
    fun onAboutDeviceClick() {
        EventConstant.EVENT_ABOUT_DEVICE.publish()
    }

    /**
     * 固件升级点击事件
     */
    fun onFirmwareUpdateClick() {
        EventConstant.EVENT_FIRMWARE_UPDATE.publish()
    }

    /**
     * 手柄按钮触发方式
     */
    fun onHandleTypeClick() {
        EventConstant.EVENT_HANDLE_TYPE.publish()
    }

    /**
     * 手柄按钮触发方式
     */
    fun onHandleRebootClick() {
        EventConstant.EVENT_HANDLE_REBOOT.publish()
    }


    /**
     * 选择文件
     */
    fun onChoiceFileClick() {
        EventConstant.EVENT_CHOICE_FILE.publish()
    }


    /**
     * 盘存模式 选择
     */
    fun onInventoryItemModeClick(type: Int) {
        mInventoryMode.value = type
    }

    /**
     * 盘存设置 自定义射频link 选择
     */
    fun onLinkUrlClick() {
        EventConstant.EVENT_LINK_URL.publish()
    }

    /**
     * 盘存设置 自定义 模式选择
     */
    fun onCustomSessionClick() {
        EventConstant.EVENT_SESSION_SELECT.publish()
    }

    /**
     * 固件 升级
     */
    fun onFileUpdateClick() {

    }

    fun getFileName(path: String?) = path?.let {
        it.substring(it.lastIndexOf("/") + 1, it.length)
    } ?: ""

}