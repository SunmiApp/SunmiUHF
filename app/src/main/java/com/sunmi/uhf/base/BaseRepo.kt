package com.sunmi.uhf.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

}