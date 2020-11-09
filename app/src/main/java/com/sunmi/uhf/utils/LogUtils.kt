package com.sunmi.uhf.utils

import android.text.format.DateFormat
import android.util.Log
import com.sunmi.uhf.BuildConfig
import java.util.*


/**
 * 打印工具类
 * isStatistics -- 是否发送到服务端
 */
object LogUtils {

    fun v(tag: String, msg: String, isStatistics: Boolean = false) {
        if (isTagLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, createLog(msg))
        }
        if (isStatistics) {
            onCommitEvent(tag, createLog(msg))
        }
    }

    fun i(tag: String, msg: String, isStatistics: Boolean = false) {
        if (isTagLoggable(tag, Log.INFO)) {
            Log.i(tag, createLog(msg))
        }
        if (isStatistics) {
            onCommitEvent(tag, createLog(msg))
        }
    }

    fun w(tag: String, msg: String, isStatistics: Boolean = false) {
        if (isTagLoggable(tag, Log.WARN)) {
            Log.w(tag, createLog(msg))
        }
        if (isStatistics) {
            onCommitEvent(tag, createLog(msg))
        }
    }

    fun e(tag: String, msg: String, isStatistics: Boolean = false) {
        if (isTagLoggable(tag, Log.ERROR)) {
            Log.e(tag, createLog(msg))
        }
        if (isStatistics) {
            onCommitEvent(tag, createLog(msg))
        }
    }

    fun d(tag: String, msg: String, isStatistics: Boolean = false) {
        if (isTagLoggable(tag, Log.DEBUG)) {
            Log.d(tag, createLog(msg))
        }
        if (isStatistics) {
            onCommitEvent(tag, createLog(msg))
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
     * 命令: adb shell setprop log.tag.<YOUR_LOG_TAG>  <LEVEL>
     * 例如: adb shell setprop log.tag.darren VERBOSE 只打印我们定义的tag为volley且level是VERBOSE的log
     * LEVEL 的值可以是VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT, 或者SUPPRESS，SUPPRESS会禁止打印所有日志。
     */
    private fun isTagLoggable(tag: String, level: Int): Boolean {
        return BuildConfig.DEBUG || Log.isLoggable(tag, level)
    }

    fun getCurTimeStr(): String? {
        return DateFormat.format("yyyy-MM-dd HH:mm:ss", Date()).toString()
    }

    private fun createLog(msg: String): String {
        var targetStackTraceElement: StackTraceElement? = null
        val stackTrace = Thread.currentThread().stackTrace
        var isTrace = false
        for (stackTraceElement in stackTrace) {
            val isLogMethod = stackTraceElement.className == LogUtils::class.java.name
            if (isTrace && !isLogMethod) {
                targetStackTraceElement = stackTraceElement
            }
            isTrace = isLogMethod
        }

        //使用StringBuffer因为是线程安全的
        val stringBuffer = StringBuffer()
        stringBuffer
            .append("[(")
            .append(targetStackTraceElement?.fileName ?: "unknow")
            .append(":")
            .append(targetStackTraceElement?.lineNumber ?: 0)
            .append(")#")
            .append(targetStackTraceElement?.methodName ?: "unknow")
            .append("]")
            .append(msg)
        return stringBuffer.toString()
    }

}