package com.sunmi.uhf.fragment.takeinventory

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunmi.uhf.R
import com.sunmi.uhf.adapter.LabelInfoAdapter
import com.sunmi.uhf.adapter.TakeModelAdapter
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentTakeInventoryBinding
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.readwrite.ReadWriteFragment

/**
 * @ClassName: TakeInventoryFragment
 * @Description: 盘存 页面
 * @Author: clh
 * @CreateDate: 20-9-9 下午1:38
 * @UpdateDate: 20-9-9 下午1:38
 */
class TakeInventoryFragment : BaseFragment<FragmentTakeInventoryBinding>() {

    lateinit var vm: TakeInventoryModel

    lateinit var adapter: LabelInfoAdapter

    private var takeModelPw: PopupWindow? = null

    private var modelAdapter: TakeModelAdapter? = null

    override fun getLayoutResource() = R.layout.fragment_take_inventory

    override fun initVM() {
        vm = getViewModel(TakeInventoryModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        adapter = LabelInfoAdapter()
        binding.labelRv.layoutManager = LinearLayoutManager(activity)
        binding.labelRv.adapter = adapter
    }

    override fun initData() {
        adapter.setNewInstance(vm.createData())
        vm.selectModel.value = vm.modelList.first()
        vm.editModel.observe(viewLifecycleOwner, Observer {
            adapter?.editable = it
            adapter?.notifyDataSetChanged()
        })
        vm.selectModel.observe(viewLifecycleOwner, Observer {
            modelAdapter?.selected = it
            modelAdapter?.notifyDataSetChanged()
        })
    }

    override fun onSimpleViewEvent(event: SimpleViewEvent) {
        super.onSimpleViewEvent(event)
        when (event.event) {
            EventConstant.EVENT_BACK -> {
                performBackClick()
            }
            EventConstant.EVENT_TAKE_MODEL -> {
                showModelPopupWindow()
            }
            EventConstant.EVENT_TAKE_MODEL_SEARCH -> {
                switchFragment(
                    SearchModelFragment.newInstance(null),
                    addToBackStack = true,
                    clearStack = false
                )
            }
        }

    }

    /**
     * 弹出显示 盘存模式列表
     */
    private fun showModelPopupWindow() {
        if (takeModelPw == null) {
            val root = View.inflate(context, R.layout.pop_take_model, null)
            val recyclerView = root.findViewById<RecyclerView>(R.id.take_mode_rv)
            modelAdapter = TakeModelAdapter()
            takeModelPw = PopupWindow(
                root,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                this.isOutsideTouchable = true
                modelAdapter?.setNewInstance(vm.createModel())
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.adapter = modelAdapter
                modelAdapter?.setOnItemClickListener { _, _, position ->
                    vm.selectModel.value = modelAdapter?.data?.get(position)
                    dismiss()
                }
            }
        }
        takeModelPw?.showAsDropDown(
            binding.filterLl.takeInventoryModelValueTv, 0,
            resources.getDimensionPixelSize(R.dimen.sunmi_8px)
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        takeModelPw?.dismiss()
    }


    companion object {
        fun newInstance(args: Bundle?) = TakeInventoryFragment()
            .apply { arguments = args }
    }
}