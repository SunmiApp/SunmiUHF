package com.sunmi.uhf.fragment.list

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunmi.uhf.App
import com.sunmi.uhf.R
import com.sunmi.uhf.adapter.OperationAdapter
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.bean.CommonListBean
import com.sunmi.uhf.constants.Constant
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentListBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.utils.LiveDataBusEvent
import com.sunmi.uhf.view.RecycleDivider

/**
 * @ClassName: ListFragment
 * @Description: java类作用描述
 * @Author: clh
 * @CreateDate: 20-9-15 下午1:43
 * @UpdateDate: 20-9-15 下午1:43
 */
class ListFragment : BaseFragment<FragmentListBinding>() {
    lateinit var vm: ListViewModel
    private lateinit var adapter: OperationAdapter
    private lateinit var selectBean: CommonListBean
    override fun getLayoutResource() = R.layout.fragment_list

    override fun initVM() {
        vm = getViewModel(ListViewModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        val title = arguments?.getString(Constant.KEY_TITLE, "") ?: ""
        val list = arguments?.getStringArrayList(Constant.KEY_LIST)
        selectBean = arguments?.getParcelable(Constant.KEY_SELECT) ?: CommonListBean(type = 0, select = "")
        vm.title.value = title
        val session = arguments?.getString(Constant.KEY_TARGET, "")
        adapter = OperationAdapter(list)
        adapter.selected = session
        binding.dataRv.layoutManager = LinearLayoutManager(activity)
        binding.dataRv.addItemDecoration(
            RecycleDivider(
                activity,
                RecycleDivider.HORIZONTAL_LIST,
                resources
                    .getDimensionPixelSize(R.dimen.sunmi_1px),
                ContextCompat.getColor(App.mContext, R.color.dividerColor)
            )
        )
        binding.dataRv.adapter = adapter
    }

    override fun initData() {
        adapter.setOnItemClickListener { _, _, position ->
            val select = adapter.data[position]
            adapter.selected = select
            selectBean.index = position
            selectBean.select = select
            adapter.notifyDataSetChanged()
            activity?.supportFragmentManager?.popBackStackImmediate()
            LiveDataBusEvent.get().with(EventConstant.LABEL_SELECT, CommonListBean::class.java)
                .postValue(selectBean)
        }
    }

    override fun onSimpleViewEvent(event: SimpleViewEvent) {
        super.onSimpleViewEvent(event)
        when (event.event) {
            EventConstant.EVENT_BACK -> {
                performBackClick()
            }
        }

    }

    companion object {
        fun newInstance(args: Bundle?) = ListFragment()
            .apply { arguments = args }
    }
}