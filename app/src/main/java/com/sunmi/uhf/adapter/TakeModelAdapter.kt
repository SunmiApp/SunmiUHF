package com.sunmi.uhf.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.sunmi.uhf.R
import com.sunmi.uhf.bean.LabelInfoBean
import com.sunmi.uhf.databinding.LayoutTakeInventoryItemInfoBinding
import com.sunmi.uhf.databinding.PopTakeModelItemBinding

/**
 * @ClassName: TakeModelAdapter
 * @Description: java类作用描述
 * @Author: clh
 * @CreateDate: 20-9-10 上午11:07
 * @UpdateDate: 20-9-10 上午11:07
 */
class TakeModelAdapter :
    BaseQuickAdapter<String, BaseDataBindingHolder<PopTakeModelItemBinding>>(
        R.layout.pop_take_model_item
    ) {

    var selected: String? = null

    override fun convert(
        holder: BaseDataBindingHolder<PopTakeModelItemBinding>,
        item: String
    ) {
        var binding = holder.dataBinding
        binding?.let {
            it.item = item
            it.selected = selected
            it.executePendingBindings()
        }
    }
}