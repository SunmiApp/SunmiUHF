package com.sunmi.uhf.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.sunmi.uhf.R
import com.sunmi.uhf.bean.LabelInfoBean
import com.sunmi.uhf.databinding.LayoutTakeInventoryItemInfoBinding

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

    var editable: Boolean? = false

    override fun convert(
        holder: BaseDataBindingHolder<LayoutTakeInventoryItemInfoBinding>,
        item: LabelInfoBean
    ) {
        var binding = holder.dataBinding
        binding?.let {
            it.bean = item
            it.editAble = editable ?: false
            it.index = getItemPosition(item)
            it.executePendingBindings()
        }
    }
}