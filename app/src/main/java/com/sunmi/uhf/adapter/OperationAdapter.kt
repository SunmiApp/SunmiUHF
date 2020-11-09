package com.sunmi.uhf.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.sunmi.uhf.R
import com.sunmi.uhf.databinding.LayoutOperationItemBinding
import com.sunmi.uhf.databinding.PopTakeModelItemBinding

/**
 * @ClassName: OperationAdapter
 * @Description: 标签操作  方式
 * @Author: clh
 * @CreateDate: 20-9-11 下午5:27
 * @UpdateDate: 20-9-11 下午5:27
 */
class OperationAdapter(data: MutableList<String>?) :
    BaseQuickAdapter<String, BaseDataBindingHolder<LayoutOperationItemBinding>>(
        R.layout.layout_operation_item, data
    ) {

    var selected: String? = null

    override fun convert(
        holder: BaseDataBindingHolder<LayoutOperationItemBinding>,
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