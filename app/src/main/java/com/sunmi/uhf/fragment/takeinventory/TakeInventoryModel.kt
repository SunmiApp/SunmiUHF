package com.sunmi.uhf.fragment.takeinventory

import androidx.lifecycle.MutableLiveData
import com.sunmi.uhf.base.BaseViewModel
import com.sunmi.uhf.bean.LabelInfoBean
import com.sunmi.uhf.constants.EventConstant

/**
 * @ClassName: TakeInventoryModel
 * @Description: java类作用描述
 * @Author: clh
 * @CreateDate: 20-9-9 下午2:05
 * @UpdateDate: 20-9-9 下午2:05
 */
class TakeInventoryModel : BaseViewModel() {

    /* 是否处于编辑模式 */
    val editModel = MutableLiveData<Boolean>(false)

    /* 标签数 */
    val labelNum = MutableLiveData<Int>(0)

    /* 读取速度 */
    val speed = MutableLiveData<Float>(0f)

    /* 总数 */
    val totalNum = MutableLiveData<Int>(0)

    /* 时间 */
    val time = MutableLiveData<Long>(0)

    /* 模式 */
    val model = MutableLiveData<String>()

    /* 标签信息 */
    val labelInfo = MutableLiveData<String>()

    /* 选择的model */
    val selectModel = MutableLiveData<String>()

    /* 筛选的标签信息 */
    val filterLabelList = MutableLiveData<MutableList<LabelInfoBean>>()

    val modelList = createModel()

    /**
     * 返回点击事件
     */
    fun onBackClick() {
        EventConstant.EVENT_BACK.publish()
    }

    /**
     * 盘存点击编辑
     */
    fun onEditAbleClick() {
        editModel.value = true
    }

    /**
     * 盘存取消点击编辑
     */
    fun onCancelEditAbleClick() {
        editModel.value = false
    }

    /**
     * 盘存模式 点击选择
     */
    fun onTakeModelClick() {
        EventConstant.EVENT_TAKE_MODEL.publish()
    }

    /**
     * 盘存 数据搜索
     */
    fun onSearchClick() {
        EventConstant.EVENT_TAKE_MODEL_SEARCH.publish()
    }

    fun createData(): MutableList<LabelInfoBean> {
        val list = mutableListOf<LabelInfoBean>()
        for (i in 1..2) {
            list.add(LabelInfoBean("EXX1001212", "XX", 5, "55db", "55hz"))
        }
        return list
    }

    fun createModel(): MutableList<String> {
        val list = mutableListOf<String>()
        list.add("快速")
        list.add("平衡")
        list.add("自定义")
        return list
    }
}