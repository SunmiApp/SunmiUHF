package com.sunmi.uhf.utils

import androidx.annotation.StringRes
import com.sunmi.widget.util.ToastUtils
import java.util.*
import java.util.regex.Pattern

object StrUtils {
    /**
     * strings to Hexadecimal array,seperate string by space
     *
     * @param strHexValue Hexadecimal string
     * @return byte array
     */
    fun stringToByteArray(strHexValue: String): ByteArray {
        val strAryHex = strHexValue.split(" ".toRegex()).toTypedArray()
        val btAryHex = ByteArray(strAryHex.size)
        try {
            for ((nIndex, strTemp) in strAryHex.withIndex()) {
                btAryHex[nIndex] = strTemp.toInt(16).toByte()
            }
        } catch (e: NumberFormatException) {
        }
        return btAryHex
    }

    /**
     * string array to Hexadecimal array
     *
     * @param strAryHex string array needs to be transfered
     * @param len      length
     * @return byte array
     */
    fun stringArrayToByteArray(strAryHex: Array<String?>?, len: Int): ByteArray? {
        var nLen = len
        if (strAryHex == null) {
            return null
        }
        if (strAryHex.size < nLen) {
            nLen = strAryHex.size
        }
        val btAryHex = ByteArray(nLen)
        for (i in 0 until nLen) {
            try {
                btAryHex[i] = strAryHex[i]?.toInt(16)?.toByte() ?: 0x00
            } catch (e: Exception) {
            }
        }
        return btAryHex
    }

    /**
     * Hexadecimal character array to strings
     *
     * @param btAryHex string array needs to be transfered
     * @param nIndex   start position
     * @param len     transfered length
     * @return transfered strings
     */
    fun byteArrayToString(btAryHex: ByteArray, nIndex: Int, len: Int): String {
        var nLen = len
        if (nIndex + nLen > btAryHex.size) {
            nLen = btAryHex.size - nIndex
        }
        var strResult = String.format("%02X", btAryHex[nIndex])
        (nIndex + 1 until nIndex + nLen).forEach {
            val strTemp = String.format(" %02X", btAryHex[it])
            strResult += strTemp
        }
        return strResult
    }

    /**
     * intercept string to the specified length and shift into character array
     *
     * @param strValue input strings
     * @return byte array
     */
    fun stringToStringArray(strValue: String?, nLen: Int): Array<String?>? {
        var strAryResult: Array<String?>? = null
        if (strValue != null && strValue != "") {
            val strListResult = ArrayList<String>()
            var strTemp = ""
            var nTemp = 0
            strValue.indices.forEach {
                if (strValue[it] == ' ') {
                    return@forEach
                } else {
                    nTemp++
                    if (!Pattern.compile("^(([A-F])*([a-f])*(\\d)*)$")
                            .matcher(strValue.substring(it, it + 1))
                            .matches()
                    ) {
                        return strAryResult
                    }
                    strTemp += strValue.substring(it, it + 1)
                    if (nTemp == nLen || it == strValue.length - 1 && strTemp != "") {
                        strListResult.add(strTemp)
                        nTemp = 0
                        strTemp = ""
                    }
                }
            }
            if (strListResult.size > 0) {
                strAryResult = arrayOfNulls(strListResult.size)
                for (i in strAryResult.indices) {
                    strAryResult[i] = strListResult[i]
                }
            }
        }
        return strAryResult
    }

    // ui
    fun strToByteArray(v: String?, @StringRes res: Int, len: Int = 0): ByteArray? {
        if (v.isNullOrEmpty()) {
            ToastUtils.showShort(res)
            return null
        }
        val strArr = stringToStringArray(v.toUpperCase(), 2)
        if (strArr.isNullOrEmpty()) {
            ToastUtils.showShort(res)
            return null
        }
        val btArr = stringArrayToByteArray(strArr, if (len <= 0) strArr.size else len)
        if (btArr == null || btArr.isEmpty()) {
            ToastUtils.showShort(res)
            return null
        }
        return btArr
    }
}