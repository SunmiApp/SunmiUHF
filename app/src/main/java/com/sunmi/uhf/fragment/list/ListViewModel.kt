package com.sunmi.uhf.fragment.list

import androidx.lifecycle.MutableLiveData
import com.sunmi.uhf.base.BaseViewModel
import com.sunmi.uhf.constants.EventConstant

/**
 * @ClassName: ListViewModel
 * @Description: java类作用描述
 * @Author: clh
 * @CreateDate: 20-9-15 下午1:46
 * @UpdateDate: 20-9-15 下午1:46
 */
class ListViewModel : BaseViewModel() {

    /*  title 信息 */
    val title = MutableLiveData<String>()

    /**
     * 返回点击事件
     */
    fun onBackClick() {
        EventConstant.EVENT_BACK.publish()
    }
}