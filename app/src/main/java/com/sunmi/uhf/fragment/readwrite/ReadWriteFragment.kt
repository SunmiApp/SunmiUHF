package com.sunmi.uhf.fragment.readwrite

import android.os.Bundle
import android.os.SystemClock
import androidx.lifecycle.Observer
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.FragmentReadWriteBinding
import com.sunmi.uhf.dialog.SureBackDialog
import com.sunmi.uhf.event.SimpleViewEvent
import com.sunmi.uhf.fragment.home.HomeFragment

/**
 * @ClassName: ReadWriteFragment
 * @Description: 快读读取 页面
 * @Author: clh
 * @CreateDate: 20-9-8 下午4:30
 * @UpdateDate: 20-9-8 下午4:30
 */
class ReadWriteFragment : BaseFragment<FragmentReadWriteBinding>() {
    var dialog: SureBackDialog? = null
    lateinit var vm: ReadWriteViewModel
    override fun getLayoutResource() = R.layout.fragment_read_write

    override fun initVM() {
        vm = getViewModel(ReadWriteViewModel::class.java)
        binding.vm = vm
    }

    override fun initView() {
        binding.chronometerView.base = SystemClock.elapsedRealtime()
    }

    override fun initData() {
        vm.start.observe(viewLifecycleOwner, Observer {
            vm.labelNum.value = 0
            vm.totalLabelNum.value = 0
            vm.speed.value = 0f
            binding.chronometerView.base = SystemClock.elapsedRealtime()
            if (it) {
                binding.chronometerView.start()
            } else {
                binding.chronometerView.stop()
            }
        })
    }


    override fun onSimpleViewEvent(event: SimpleViewEvent) {
        super.onSimpleViewEvent(event)
        when (event.event) {
            EventConstant.EVENT_BACK -> {
                showSureDialog()
            }
        }

    }

    /**
     * 点击返回健后，弹出二次确认弹窗
     * 点击退出 ，退出页面
     */
    private fun showSureDialog() {
        val showing = (dialog?.isAdded ?: false || dialog?.dialog?.isShowing ?: false)
        if (showing) dialog?.dismiss()
        if (dialog == null) {
            dialog = SureBackDialog.newInstance(null)
        }
        dialog?.listener = object : (() -> Unit) {
            override fun invoke() {
                dialog?.dismiss()
                performBackClick()
            }
        }
        dialog?.show(parentFragmentManager, SureBackDialog::class.java.name)
    }


    companion object {
        fun newInstance(args: Bundle?) = ReadWriteFragment()
            .apply { arguments = args }
    }
}