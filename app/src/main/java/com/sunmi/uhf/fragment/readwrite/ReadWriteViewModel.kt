package com.sunmi.uhf.fragment.readwrite

import androidx.lifecycle.MutableLiveData
import com.sunmi.uhf.base.BaseViewModel
import com.sunmi.uhf.constants.EventConstant

/**
 * @ClassName: ReadWriteViewModel
 * @Description: java类作用描述
 * @Author: clh
 * @CreateDate: 20-9-9 上午9:57
 * @UpdateDate: 20-9-9 上午9:57
 */
class ReadWriteViewModel : BaseViewModel() {

    /* 标签数 */
    val labelNum = MutableLiveData<Int>(0)

    /* 标签总数  累计 */
    val totalLabelNum = MutableLiveData<Int>(0)

    /* 读取速度 */
    val speed = MutableLiveData<Int>(0)

    /* 是否开始读取 */
    val start = MutableLiveData<Boolean>(false)


    /**
     * 返回点击事件
     */
    fun onBackClick() {
        EventConstant.EVENT_BACK.publish()
    }

    /**
     * 操作按钮
     */
    fun onBtnClick() {
        val flag = start.value ?: false
        start.value = !flag
    }
}