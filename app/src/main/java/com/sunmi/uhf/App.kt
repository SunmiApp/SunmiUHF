package com.sunmi.uhf

import android.app.Application

/**
 * @ClassName: App
 * @Description: java类作用描述
 * @Author: clh
 * @CreateDate: 20-9-7 下午1:47
 * @UpdateDate: 20-9-7 下午1:47
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        mContext = this
    }

    companion object {
        lateinit var mContext: Application
    }

}