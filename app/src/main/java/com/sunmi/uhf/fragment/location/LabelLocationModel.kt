package com.sunmi.uhf.fragment.location

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



    /**
     * 返回点击事件
     */
    fun onBackClick() {
        EventConstant.EVENT_BACK.publish()
    }
}