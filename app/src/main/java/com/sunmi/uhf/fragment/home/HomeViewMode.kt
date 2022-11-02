package com.sunmi.uhf.fragment.home


import androidx.lifecycle.MutableLiveData
import com.sunmi.uhf.base.BaseViewModel
import com.sunmi.uhf.constants.EventConstant

/**
 * @ClassName: HomeViewMode
 * @Description: java类作用描述
 * @Author: clh
 * @CreateDate: 20-9-8 下午4:31
 * @UpdateDate: 20-9-8 下午4:31
 */
class HomeViewMode : BaseViewModel() {

    // 是否内置RFID适配判断
    val isInner = MutableLiveData<Boolean>(true)

    /**
     * 快速读取的点击事件
     */
    fun onFastReadWriteClick() {
        EventConstant.EVENT_FAST_READ_WRITE.publish()
    }

    /**
     * 盘存的点击事件
     */
    fun onTakeInventoryClick() {
        EventConstant.EVENT_TAKE_INVENTORY.publish()
    }

    /**
     * 标签操作的点击事件
     */
    fun onLabelOperationClick() {
        EventConstant.EVENT_LABEL_OPERATION.publish()
    }

    /**
     * 标签定位的点击事件
     */
    fun onLabelLocationClick() {
        EventConstant.EVENT_LABEL_LOCATION.publish()
    }

    /**
     * 标签过滤的点击事件
     */
    fun onLabelFilterClick() {
        EventConstant.EVENT_LABEL_FILTER.publish()
    }

    /**
     * 标签定位的点击事件
     */
    fun onSettingClick() {
        EventConstant.EVENT_SETTING.publish()
    }

}