package com.sunmi.uhf.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.sunmi.uhf.R
import com.sunmi.uhf.bean.LabelInfoBean
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.LayoutTakeInventoryItemInfoBinding
import com.sunmi.uhf.fragment.takeinventory.TakeInventoryFragment
import com.sunmi.uhf.utils.LiveDataBusEvent

/**
 * @ClassName: LabelInfoAdapter
 * @Description: java类作用描述
 * @Author: clh
 * @CreateDate: 20-9-9 下午6:19
 * @UpdateDate: 20-9-9 下午6:19
 */
class LabelInfoAdapter :
    BaseQuickAdapter<LabelInfoBean, BaseDataBindingHolder<LayoutTakeInventoryItemInfoBinding>>(
        R.layout.layout_take_inventory_item_info
    ) {

    var editable: Boolean = false
    var selectAll: Boolean = false
    var selectAllCall: ((Boolean) -> Unit)? = null
    var clickCall: (() -> Unit)? = null
    val selectData = HashMap<String, LabelInfoBean>()

    override fun convert(
        holder: BaseDataBindingHolder<LayoutTakeInventoryItemInfoBinding>,
        item: LabelInfoBean
    ) {
        var binding = holder.dataBinding
        binding?.let {
            it.bean = item
            it.editAble = editable
            it.index = getItemPosition(item)
            it.check = selectData.containsKey(item.epc) || selectAll
            it.itemCheckIv.setOnClickListener { _ ->
                if (selectData.containsKey(item.epc)) {
                    selectData.remove(item.epc)
                    it.check = false
                    if (selectAll) {
                        selectAll = false
                        selectAllCall?.invoke(selectAll)
                    }
                } else {
                    selectData[item.epc!!] = item
                    it.check = true
                    if (!selectAll && data.size == selectData.size) {
                        selectAll = true
                        selectAllCall?.invoke(selectAll)
                    }
                }
                it.executePendingBindings()
                clickCall?.invoke()
            }
            it.executePendingBindings()
        }
    }

}