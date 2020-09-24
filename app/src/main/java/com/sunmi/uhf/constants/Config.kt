package com.sunmi.uhf.constants;

/**
 * @author darren by Darren1009@qq.com - 2020/09/24
 */
object Config {


    /** 操作标签类型 0：6C，1：6B */
    const val KEY_LABEL = "key_label"
    const val DEF_LABEL = 0

    /** 按键操作方式 0：长按，1：点按 */
    const val KEY_HANDLE_TYPE = "key_handle_type"
    const val DEF_HANDLE_TYPE = 0

    /** 提示音 */
    const val KEY_TIP_VOICE = "key_tip_voice"
    const val DEF_TIP_VOICE = true

    /** 提示灯 */
    const val KEY_TIP_LIGHT = "key_tip_light"
    const val DEF_TIP_LIGHT = true
}
