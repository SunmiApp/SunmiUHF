package com.sunmi.uhf.fragment

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.sunmi.rfid.ReaderCall
import com.sunmi.rfid.entity.DataParameter
import com.sunmi.uhf.App
import com.sunmi.uhf.R
import com.sunmi.uhf.base.BaseFragment
import com.sunmi.uhf.constants.Config
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.utils.LiveDataBusEvent
import com.sunmi.uhf.utils.LogUtils
import com.sunmi.uhf.utils.SoundHelper

/**
 * @author darren by Darren1009@qq.com - 2020/09/25
 */
abstract class ReadBaseFragment<T : ViewDataBinding> : BaseFragment<T>() {
    protected var state = false
    protected val tidList = ArrayList<String>()
    protected val tagList = ArrayList<DataParameter>()
    private lateinit var soundHelper: SoundHelper
    protected val call: ReaderCall = object : ReaderCall() {
        override fun onSuccess(cmd: Byte, params: DataParameter?) {
            LogUtils.d(javaClass.simpleName, "success cmd:" + String.format("%%02X", cmd))
            onCallSuccess(cmd, params)
        }

        override fun onTag(cmd: Byte, state: Byte, tag: DataParameter?) {
            LogUtils.d(javaClass.simpleName, "found tag cmd:" + String.format("%%02X", cmd) + ", state: " + String.format("%%02X", state))
            onCallTag(cmd, state, tag)
        }

        override fun onFiled(cmd: Byte, errorCode: Byte, msg: String?) {
            LogUtils.d(javaClass.simpleName, "failed cmd: ${String.format("%02X", cmd)}, errorCode: ${String.format("%02X", errorCode)}, msg: $msg")
            onCallFailed(cmd, errorCode, msg)
        }
    }

    override fun initData() {
        soundHelper = SoundHelper(context)
        soundHelper.putSound(0, R.raw.beep)
        LiveDataBusEvent.get().with(EventConstant.UHF_KEY_EVENT, Int::class.java)
                .observe(viewLifecycleOwner, Observer {
                    when (it) {
                        EventConstant.EVENT_UHF_KEY_EVENT_DOWN -> {
                            LogUtils.d(EventConstant.UHF_KEY_EVENT, "key down event.")
                            when (val t = getHandleType()) {
                                0 -> {
                                    handleBottomStart()
                                }
                                1 -> {
                                    if (state) {
                                        handleBottomStop()
                                    } else {
                                        handleBottomStart()
                                    }
                                }
                                else -> {
                                    LogUtils.i(javaClass.simpleName, "key down event handle type $t")
                                }
                            }
                        }
                        EventConstant.EVENT_UHF_KEY_EVENT_UP -> {
                            LogUtils.d(EventConstant.UHF_KEY_EVENT, "key up event.")
                            when (val t = getHandleType()) {
                                0 -> {
                                    handleBottomStop()
                                }
                                else -> {
                                    LogUtils.i(javaClass.simpleName, "key up event handle type $t")
                                }
                            }

                        }
                        else -> {
                            LogUtils.d(EventConstant.UHF_KEY_EVENT, "other key event.")
                        }
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        soundHelper.destroy()
    }

    open fun start() {
        state = true
    }

    open fun stop() {
        state = false
    }

    fun playTips() {
        if (App.getPref().getParam(Config.KEY_TIP_VOICE, Config.DEF_TIP_VOICE)) {
            soundHelper.playSound(0, 0)
        }
    }

    /** 按键操作方式 0：长按，1：点按 */
    private fun getHandleType() =
            App.getPref().getParam(Config.KEY_HANDLE_TYPE, Config.DEF_HANDLE_TYPE)

    abstract fun handleBottomStart()
    abstract fun handleBottomStop()
    abstract fun onCallSuccess(cmd: Byte, params: DataParameter?)
    abstract fun onCallTag(cmd: Byte, state: Byte, tag: DataParameter?)
    abstract fun onCallFailed(cmd: Byte, errorCode: Byte, msg: String?)
}