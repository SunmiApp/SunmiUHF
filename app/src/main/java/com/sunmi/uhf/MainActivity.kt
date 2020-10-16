package com.sunmi.uhf

import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import com.sunmi.uhf.base.BaseActivity
import com.sunmi.uhf.constants.EventConstant
import com.sunmi.uhf.databinding.ActivityMainBinding
import com.sunmi.uhf.fragment.home.HomeFragment
import com.sunmi.uhf.utils.LiveDataBusEvent


/**
 * @ClassName: MainActivity
 * @Description: java类作用描述
 * @Author: clh
 * @CreateDate: 20-9-7 下午2:30
 * @UpdateDate: 20-9-7 下午2:30
 */
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private var isLoop = false

    override fun getLayoutResource() = R.layout.activity_main

    override fun getContainId() = R.id.main_content

    override fun initVM() {
    }

    override fun initView() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val density = dm.density // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）

        val densityDPI = dm.densityDpi // 屏幕密度（每寸像素：120/160/240/320）

//        val xdpi = dm.xdpi
//        val ydpi = dm.ydpi
//
//        Log.e(
//            "  DisplayMetrics",
//            "xdpi=" + xdpi.toString() + "; ydpi=" + ydpi
//        )
        Log.e(
                "  DisplayMetrics",
                "density=" + density.toString() + "; densityDPI=" + densityDPI
        )
        Log.e(
                "  DisplayMetrics",
                "sw=" + dm.widthPixels + "; sh=" + dm.heightPixels
        )
        Log.e(
                "  DisplayMetrics",
                "sw=" + resources.displayMetrics.widthPixels + "; sh=" + resources.displayMetrics.heightPixels
        )


    }

    override fun initData() {
    }

    override fun onPortrait() {
        switchFragment(
                HomeFragment.newInstance(null),
                addToBackStack = true,
                clearStack = true
        )
    }

    override fun onLandScape() {
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == 288) {
            if (!isLoop) {
                isLoop = true
                LiveDataBusEvent.get().with(EventConstant.UHF_KEY_EVENT, Int::class.java)
                        .postValue(EventConstant.EVENT_UHF_KEY_EVENT_UP)
            }
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == 288) {
            if (isLoop) {
                isLoop = false
                LiveDataBusEvent.get().with(EventConstant.UHF_KEY_EVENT, Int::class.java)
                        .postValue(EventConstant.EVENT_UHF_KEY_EVENT_DOWN)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}