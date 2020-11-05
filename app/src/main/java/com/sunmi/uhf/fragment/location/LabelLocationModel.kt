package com.sunmi.uhf.fragment.location

import androidx.lifecycle.MutableLiveData
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseViewModel
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.widget.util.ToastUtils

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

    /** epc 信息 */
    val epcInfo = MutableLiveData<String>()

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
        if (epcInfo.value.isNullOrEmpty()) {
            ToastUtils.showShort(R.string.input_epc_text)
            return
        }
        val flag = start.value ?: false
        start.value = !flag
    }
}