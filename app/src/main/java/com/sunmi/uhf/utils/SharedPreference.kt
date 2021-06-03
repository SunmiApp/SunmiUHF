package com.sunmi.uhf.utils

import android.content.Context
import android.content.SharedPreferences
import com.sunmi.uhf.App
import java.io.*
import kotlin.jvm.Throws


/**
 * shared preferences 工具类
 * @author darren by Darren1009@qq.com - 2020/09/23
 */
class SharedPreference {
    companion object {
        private val instance: SharedPreference by lazy { SharedPreference() }
        fun get() = instance
    }

    /**
     * 保存在手机里面的文件名
     */
    private val FILE_NAME = "uhf_config"
    private val sp: SharedPreferences by lazy {
        App.mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     * @param key
     * @param obj
     */
    fun <A> setParam(key: String?, obj: A) = with(sp.edit()) {
        when (obj) {
            is Long -> putLong(key, obj)
            is String -> putString(key, obj)
            is Byte -> putInt(key, obj.toInt())
            is Int -> putInt(key, obj)
            is Boolean -> putBoolean(key, obj)
            is Float -> putFloat(key, obj)
            else -> putString(key, serialize(obj))
        }.apply()
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     * @param key
     * @param defaultObject
     * @return
     */
    fun <A> getParam(key: String?, defaultObject: A): A = with(sp) {
        return when (defaultObject) {
            is Long -> getLong(key, defaultObject)
            is String -> getString(key, defaultObject) ?: ""
            is Int -> getInt(key, defaultObject)
            is Byte -> getInt(key, defaultObject.toInt())
            is Boolean -> getBoolean(key, defaultObject)
            is Float -> getFloat(key, defaultObject)
            else -> deSerialization(getString(key, serialize(defaultObject)) ?: "")
        } as A
    }

    fun setStringParam(key: String?, obj: String?) {
        sp.edit().putString(key, obj).apply()
    }

    fun getStringParam(key: String?): String? {
        return sp.getString(key, "")
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    fun contains(key: String): Boolean {
        return sp.contains(key)
    }

    /**
     * 返回所有的键值对
     *
     * @return
     */
    fun getAll(): Map<String, *> {
        return sp.all
    }

    /**
     * 删除全部数据
     */
    fun clearPreference() {
        sp.edit().clear().apply()
    }

    /**
     * 根据key删除存储数据
     */
    fun clearPreference(key: String) {
        sp.edit().remove(key).apply()
    }

    /**
     * 序列化对象
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun <A> serialize(obj: A): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(
            byteArrayOutputStream
        )
        objectOutputStream.writeObject(obj)
        var serStr = byteArrayOutputStream.toString("ISO-8859-1")
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
        objectOutputStream.close()
        byteArrayOutputStream.close()
        return serStr
    }

    /**
     * 反序列化对象
     * @param str
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Throws(IOException::class, ClassNotFoundException::class)
    private fun <A> deSerialization(str: String): A {
        val redStr = java.net.URLDecoder.decode(str, "UTF-8")
        val byteArrayInputStream = ByteArrayInputStream(
            redStr.toByteArray(charset("ISO-8859-1"))
        )
        val objectInputStream = ObjectInputStream(
            byteArrayInputStream
        )
        val obj = objectInputStream.readObject() as A
        objectInputStream.close()
        byteArrayInputStream.close()
        return obj
    }
}