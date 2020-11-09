package com.sunmi.uhf.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.sunmi.uhf.R
import com.sunmi.uhf.databinding.LayoutFilterRuleItemBinding
import com.sunmi.uhf.databinding.LayoutOperationItemBinding
import com.sunmi.uhf.databinding.PopTakeModelItemBinding

/**
 * @ClassName: OperationAdapter
 * @Description: 标签操作  方式
 * @Author: clh
 * @CreateDate: 20-9-11 下午5:27
 * @UpdateDate: 20-9-11 下午5:27
 */
class FilterRuleAdapter :
    BaseQuickAdapter<String, BaseDataBindingHolder<LayoutFilterRuleItemBinding>>(
        R.layout.layout_filter_rule_item
    ) {

    var selected: Int = 0

    override fun convert(
        holder: BaseDataBindingHolder<LayoutFilterRuleItemBinding>,
        item: String
    ) {
        var binding = holder.dataBinding
        binding?.let {
            it.position = getItemPosition(item)
            it.item = item
            it.selected = selected
            it.executePendingBindings()
        }
    }
}