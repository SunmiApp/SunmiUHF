package com.sunmi.uhf.fragment.location

import androidx.lifecycle.MutableLiveData
import com.sunmi.uhf.base.BaseViewModel
import com.sunmi.uhf.constants.EventConstant

/**
 * @ClassName: LabelLocationModel
 * @Description: java类作用描述
 * @Author: clh
 * @CreateDate: 20-9-10 下午2:53
 * @UpdateDate: 20-9-10 下午2:53
 */
class LabelLocationModel : BaseViewModel() {

    /** 是否开始读取 */
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
        EventConstant.EVENT_LABEL_LOCATION_CLICK.publish()
    }
}