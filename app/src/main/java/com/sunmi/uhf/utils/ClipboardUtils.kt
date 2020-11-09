package com.sunmi.uhf.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/**
 * @author darren by Darren1009@qq.com - 2020/10/16
 * //控件加上属性
 * android:textIsSelectable="true"
 * //普通字符型
 * ClipData mClipData =ClipData.newPlainText("Label", "Content");
 * //url型
 * ClipData.newRawUri("Label",Uri.parse("http://www.baidu.com"));
 * //intent型
 * ClipData.newIntent("Label", intent);
 * //获取剪切板数据
 * ClipboardManager.getPrimaryClip();
 */
object ClipboardUtils {
    private var cm: ClipboardManager? = null
    fun copyStrToClipboard(context: Context?, clip: String?) {
        if (cm == null && context != null) {
            cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        }
        if (cm != null) {
            cm!!.setPrimaryClip(ClipData.newPlainText(clip, clip))
        }
    }

    fun getStrForClipboard(context: Context?): CharSequence {
        if (cm == null && context != null) {
            cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        }
        return if (cm != null && cm!!.primaryClip != null && cm!!.primaryClip!!.itemCount > 0) {
            cm!!.primaryClip!!.getItemAt(0).text
        } else ""
    }
}