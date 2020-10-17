package com.sunmi.uhf.fragment.takeinventory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunmi.uhf.R
import com.sunmi.uhf.adapter.LabelInfoAdapter
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.bean.LabelInfoBean
import com.sunmi.uhf.constants.Constant
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentSearchBinding
import com.sunmi.uhf.event.SimpleViewEvent
import kotlinx.coroutines.launch

/**
 * @ClassName: SearchModelFragment
 * @Description: 搜索模式选择
 * @Author: clh
 * @CreateDate: 20-9-10 下午1:57
 * @UpdateDate: 20-9-10 下午1:57
 */
class SearchModelFragment : BaseFragment<FragmentSearchBinding>() {
    lateinit var vm: TakeInventoryModel
    lateinit var adapter: LabelInfoAdapter
    private val allList = mutableListOf<LabelInfoBean>()
    private val curList = mutableListOf<LabelInfoBean>()

    override fun getLayoutResource() = R.layout.fragment_search

    override fun initVM() {
        vm = getViewModel(TakeInventoryModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        adapter = LabelInfoAdapter()
        binding.labelRv.layoutManager = LinearLayoutManager(activity)
        binding.labelRv.adapter = adapter
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                mainScope.launch {
                    binding.searchEt.text?.toString()?.toUpperCase()?.let {
                        if (it.isNotEmpty()) {
                            curList.clear()
                            for (bean in allList) {
                                if (bean.epc?.contains(it) == true) {
                                    curList.add(bean)
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    override fun initData() {
        arguments?.let {
            it.getParcelableArrayList<LabelInfoBean>(Constant.KEY_TAG_LIST)
                ?.let { al ->
                    allList.addAll(al)
                    curList.addAll(al)
                }
        }
        vm.filterLabelList.value = curList
        vm.filterLabelList.observe(viewLifecycleOwner, Observer {
            adapter.setNewInstance(it)
        })
        vm.editModel.observe(viewLifecycleOwner, Observer {
            adapter?.editable = it
            adapter?.notifyDataSetChanged()
        })
        vm.selectAll.observe(viewLifecycleOwner, Observer {
            if (adapter.selectAll == it) return@Observer
            adapter.selectAll = it
            if (it) {
                if (curList.size != adapter.selectData.size) {
                    for (b in curList) {
                        adapter.selectData[b.epc!!] = b
                    }
                }
            } else {
                adapter.selectData.clear()
            }
            adapter.notifyDataSetChanged()
        })
        adapter.selectAllCall = object : ((Boolean) -> Unit) {
            override fun invoke(en: Boolean) {
                if (isVisible) {
                    vm.selectAll.value = en
                }
            }
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
        fun newInstance(args: Bundle?) = SearchModelFragment()
            .apply { arguments = args }
    }
}