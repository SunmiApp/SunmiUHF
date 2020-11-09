package com.sunmi.uhf.fragment.filter

import androidx.lifecycle.MutableLiveData
import com.sunmi.uhf.App
import com.sunmi.uhf.R
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
    /*  title 信息 */
    val title = MutableLiveData<String>()

    /** epc 信息 */
    val mEpcData = MutableLiveData<String>()

    /** 读取区域 */
    val mBlockData = MutableLiveData<String>()

    /** 偏移位置 */
    val mOffSetData = MutableLiveData<String>()


    /** 过滤规则 */
    val mFilterRuleData = MutableLiveData<String>()

    /** 目标 */
    val mTargetData = MutableLiveData<String>()

    /** 是否启用过滤器 */
    val isStartFlag = MutableLiveData<Boolean>()

    /** 规则list */
    val mRuleList = MutableLiveData<MutableList<String>>()

    /** session list */
    val mSessionList = MutableLiveData<MutableList<String>>()

    /**
     * 返回点击事件
     */
    fun onBackClick() {
        EventConstant.EVENT_BACK.publish()
    }

    /**
     * 读取区域点击事件
     */
    fun onReadWriteBlockClick() {
        EventConstant.EVENT_BLOCK_CLICK.publish()
    }

    /**
     * 过滤规则点击事件
     */
    fun onFilterRuleClick() {
        EventConstant.EVENT_FILTER_RULE.publish()
    }


    /**
     * 目标点击事件
     */
    fun onTargetClick() {
        EventConstant.EVENT_TARGET_CLICK.publish()
    }


    /**
     * 启用或者停用 过滤器
     */
    fun onOperationClick() {
        val flag = isStartFlag.value ?: false
        isStartFlag.value = !flag
    }

    /**
     * 过滤规则内容
     */
    fun createRuleList() {
        val list = mutableListOf<String>()
        list.addAll(App.mContext.resources.getStringArray(R.array.filter_rules_array).toList())
        mRuleList.value = list
    }
}