package com.sunmi.uhf.base

import com.sunmi.widget.util.ToastUtils
import com.sunmilib.http.BaseResponse
import com.sunmilib.http.Request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.reflect.Type

/**
 * Created by lin on 20-4-16.
 */
abstract class BaseRepo {


    val TAG = javaClass.simpleName

    companion object {
        val EXCLUDE_ERROR_CODES = arrayOf(
            1,
            13308,
            13312,
            13313,
            13318
        )
    }

    protected suspend fun <T> launch(block: () -> T): T = withContext(Dispatchers.IO) {
        block()
    }

    protected suspend fun <T> asyncLaunch(block: suspend CoroutineScope.() -> T): T =
        withContext(Dispatchers.IO) {
            block()
        }

    /**
     * 同步请求时有可能抛出异常
     * 如果在使用同步请求时，尽量调用此扩展方法代替execute()
     */
    protected fun <T> Request.safeExecute(type: Type): BaseResponse<T>? {
        return try {
            var response: BaseResponse<T> = synExecute(type)

            /**
             * token失效并且上一次token失效流程已经完成才会处理，防止一直重复起intent
             */
            if (response.code == 30000) {

            }
            if (response.code !in EXCLUDE_ERROR_CODES) {
                ToastUtils.showShort("" + response.code + "  " + response.msg ?: "msg is null")
            }
            response
        } catch (e: Exception) {
            ToastUtils.showShort("接口请求异常 " + e.message)
            null
        }
    }

}