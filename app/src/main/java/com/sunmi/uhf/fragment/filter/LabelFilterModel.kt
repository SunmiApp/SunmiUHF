package com.sunmi.uhf.fragment.filter

import com.sunmi.uhf.base.BaseViewModel
import com.sunmi.uhf.constants.EventConstant

/**
 * @ClassName: LabelFilterModel
 * @Description: java类作用描述
 * @Author: clh
 * @CreateDate: 20-9-11 下午6:33
 * @UpdateDate: 20-9-11 下午6:33
 */
class LabelFilterModel : BaseViewModel() {

    /**
     * 返回点击事件
     */
    fun onBackClick() {
        EventConstant.EVENT_BACK.publish()
    }
}