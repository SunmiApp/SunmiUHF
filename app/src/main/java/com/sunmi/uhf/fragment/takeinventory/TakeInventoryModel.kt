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

    /* 是否开始读取 */
    val start = MutableLiveData<Boolean>(false)

    /* 选择所有 */
    val selectAll = MutableLiveData<Boolean>(false)

    /* 是否处于编辑模式 */
    val editModel = MutableLiveData<Boolean>(false)

    /* 是否可以点击导出 */
    val editEnExport = MutableLiveData<Boolean>(false)

    /* 标签数 */
    val labelNum = MutableLiveData<Int>(0)

    /* 读取速度 */
    val speed = MutableLiveData<Int>(0)

    /* 总数 */
    val totalNum = MutableLiveData<Int>(0)

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
     * 开始/停止按钮
     */
    fun onBtnClick() {
        val flag = start.value ?: false
        start.value = !flag
    }

    /**
     * 选择 全选
     */
    fun onSelectAllClick() {
        val flag = selectAll.value ?: false
        selectAll.value = !flag
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
     * 标签信息 点击选择
     */
    fun onTakeInfoClick() {
        EventConstant.EVENT_TAKE_LABEL_INFO.publish()
    }

    /**
     * 盘存 数据搜索
     */
    fun onSearchClick() {
        EventConstant.EVENT_TAKE_MODEL_SEARCH.publish()
    }

    /**
     * 复制 EPC
     */
    fun onCopyEpcClick() {
        EventConstant.EVENT_INVENTORY_COPY_EPC.publish()
    }

    /**
     * 分享
     */
    fun onShareClick() {
        EventConstant.EVENT_INVENTORY_SHARE.publish()
    }

    /**
     * 导出Excel
     */
    fun onExportExcelClick() {
        EventConstant.EVENT_INVENTORY_EXPORT_EXCEL.publish()
    }

    /**
     * 导出 All Excel
     */
    fun onExportAllExcelClick() {
        EventConstant.EVENT_INVENTORY_EXPORT_EXCEL_ALL.publish()
    }

    fun createModel(): MutableList<String> {
        val list = mutableListOf<String>()
        list.add("平衡")
        list.add("快速")
        list.add("遍历")
        list.add("自定义")
        return list
    }
}