package com.sunmi.uhf.fragment.operation

import androidx.lifecycle.MutableLiveData
import com.sunmi.uhf.base.BaseViewModel
import com.sunmi.uhf.constants.EventConstant

/**
 * @ClassName: LabelOperationModel
 * @Description: java类作用描述
 * @Author: clh
 * @CreateDate: 20-9-11 下午3:14
 * @UpdateDate: 20-9-11 下午3:14
 */
class LabelOperationModel : BaseViewModel() {
    /*  epc 信息 */
    val epc = MutableLiveData<String>()

    /*  密码 信息 */
    val pwd = MutableLiveData<String>()

    /*  起始位置 信息 */
    val startLocation = MutableLiveData<String>()

    /*  长度 信息 */
    val dataLength = MutableLiveData<String>()

    /*  data 信息 */
    val dataInfo = MutableLiveData<String>()

    /*  当前 是否 锁定 状态 */
    val lockFlag = MutableLiveData<Boolean>()

    /*  操作区域 */
    val areaData = MutableLiveData<String>()

    /*  操作类型 */
    val typeData = MutableLiveData<String>()


    /**
     * 返回点击事件
     */
    fun onBackClick() {
        EventConstant.EVENT_BACK.publish()
    }


    /**
     * 点击操作区域列表
     */
    fun onOperationAreaClick() {
        EventConstant.EVENT_OPERATION_AREA.publish()
    }

    /**
     * 点击操作类型列表
     */
    fun onOperationTypeClick() {
        EventConstant.EVENT_OPERATION_TYPE.publish()
    }


    /**
     * 点击 读取事件
     */
    fun onReadClick() {
        EventConstant.EVENT_OPERATION_READ_DATA.publish()
    }

    /**
     * 点击 写入事件
     */
    fun onWriteClick() {
        EventConstant.EVENT_OPERATION_WRITE_DATA.publish()
    }

    /**
     * 锁定 获取 解锁 事件
     */
    fun onLockClick() {
        EventConstant.EVENT_OPERATION_LOCK_TAG.publish()
    }

    /**
     * 销毁事件
     */
    fun onDestroyClick() {
        EventConstant.EVENT_OPERATION_DESTROY_TAG.publish()
    }
}