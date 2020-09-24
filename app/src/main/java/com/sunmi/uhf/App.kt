package com.sunmi.uhf

import android.app.Application
import com.sunmi.rfid.RFIDManager
import com.sunmi.uhf.utils.SharedPreference
import com.sunmi.widget.util.ToastUtils

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
        mPreference = SharedPreference.get()
        ToastUtils.init(this)
        RFIDManager.getInstance().setPrintLog(true)
        RFIDManager.getInstance().connect(mContext)
    }

    override fun onTerminate() {
        super.onTerminate()
        RFIDManager.getInstance().disconnect()
    }

    companion object {
        lateinit var mContext: Application
        private lateinit var mPreference: SharedPreference
        fun getPref() = mPreference
    }

}