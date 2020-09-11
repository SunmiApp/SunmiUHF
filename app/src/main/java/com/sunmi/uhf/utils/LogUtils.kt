package com.sunmi.uhf.utils

import android.text.format.DateFormat
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


/**
 * 打印工具类
 * isStatistics -- 是否发送到服务端
 */
object LogUtils {

    fun v(tag: String, msg: String, isStatistics: Boolean = false) {
        if (isTagLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, msg)
        }
        if (isStatistics) {
            onCommitEvent(tag, msg)
        }
    }

    fun i(tag: String, msg: String, isStatistics: Boolean = false) {
        if (isTagLoggable(tag, Log.INFO)) {
            Log.i(tag, msg)
        }
        if (isStatistics) {
            onCommitEvent(tag, msg)
        }
    }

    fun e(tag: String, msg: String, isStatistics: Boolean = false) {
        if (isTagLoggable(tag, Log.ERROR)) {
            Log.e(tag, msg)
        }
        if (isStatistics) {
            onCommitEvent(tag, msg)
        }
    }

    fun d(tag: String, msg: String, isStatistics: Boolean = false) {
        if (isTagLoggable(tag, Log.DEBUG)) {
            Log.d(tag, msg)
        }
        if (isStatistics) {
            onCommitEvent(tag, msg)
        }
    }

    private fun onCommitEvent(tag: String, msg: String) {
//        if (BaseApplication.debug) {
//            FileLogUtils.dumpLog("$tag,$msg")
//        }
//
//        GlobalScope.launch(Dispatchers.IO) {
//            StaticsMaster.onEvent(DeviceUtils.getSN(), tag, getCurTimeStr() + "  " + msg)
//        }
    }

    /**
     * 判断当前log是否允许输出
     *
     * @param tag   官方：the tag to check
     * @param level 官方：the level to check
     * @return true 表示允许输出，false表示不允许输出
     */
    private fun isTagLoggable(tag: String, level: Int): Boolean {
        return Log.isLoggable(tag, level)
    }

    fun getCurTimeStr(): String? {
        return DateFormat.format("yyyy-MM-dd HH:mm:ss", Date())
            .toString()
    }

}