package com.sunmi.uhf.constants;

/**
 * @author darren by Darren1009@qq.com - 2020/09/24
 */
object Config {

    /** 操作标签类型 0：6B，1：6C */
    const val KEY_LABEL = "key_label"
    const val DEF_LABEL = 1

    /** 按键操作方式 0：长按，1：点按 */
    const val KEY_HANDLE_TYPE = "key_handle_type"
    const val DEF_HANDLE_TYPE = 0

    /** 提示音 */
    const val KEY_TIP_VOICE = "key_tip_voice"
    const val DEF_TIP_VOICE = true

    /** 提示灯 */
    const val KEY_TIP_LIGHT = "key_tip_light"
    const val DEF_TIP_LIGHT = true

    /** 盘存模式 1：平衡模式 (8B+S1+TagFocus)，2：高速模式(89+射频链路)，3：遍历模式(8B+S2)，4：自定义模式(8B) */
    const val KEY_TAKE_MODE = "key_take_mode"
    const val DEF_TAKE_MODE = Constant.INT_SPEED_MODE

    /** 盘存session 00：S0，01：S1，02：S2，03：S3 */
    const val KEY_TAKE_SESSION = "key_take_session"
    const val DEF_TAKE_SESSION = 1

    /** 盘存标志 00：A，01：B */
    const val KEY_TAKE_FLAG = "key_take_flag"
    const val DEF_TAKE_FLAG = 0

    /** 射频频链路设置: 默认为配置1
     * 配置0：Tari 25us; FM0 40K
     * 配置1：Tari 25us; Miller4 250KHz
     * 配置2：Tari 25us; Miller4 300KHz
     * 配置3：Tari 6.25us; FM0 400KHz
     */
    const val KEY_TAKE_LINK = "key_take_link"
    const val DEF_TAKE_LINK = 1

    /** 动态功率 */
    const val KEY_TAKE_AUTO_POWER = "key_take_auto_power"
    const val DEF_TAKE_AUTO_POWER = false

    /** Tag Focus */
    const val KEY_TAKE_TAG_FOCUS = "key_take_tag_focus"
    const val DEF_TAKE_TAG_FOCUS = false

    /** 过滤器 info */
    const val KEY_FILTER_INFO_1 = "key_filter_info_1"
    const val KEY_FILTER_INFO_2 = "key_filter_info_2"
    const val DEF_FILTER_INFO = ""

    /** 过滤器 区域 */
    const val KEY_FILTER_AREA_1 = "key_filter_area_1"
    const val KEY_FILTER_AREA_2 = "key_filter_area_2"
    const val DEF_FILTER_AREA = 1

    /** 过滤器 偏移量 */
    const val KEY_FILTER_START_ADD_1 = "key_offset_index_1"
    const val KEY_FILTER_START_ADD_2 = "key_offset_index_2"
    const val DEF_FILTER_START_ADD = 0

    /** 过滤器 规则 */
    const val KEY_FILTER_RULE_1 = "key_filter_rule_1"
    const val KEY_FILTER_RULE_2 = "key_filter_rule_2"
    const val DEF_FILTER_RULE = 0

    /** 过滤器 目标 */
    const val KEY_FILTER_TARGET_1 = "key_filter_target_1"
    const val KEY_FILTER_TARGET_2 = "key_filter_target_2"
    const val DEF_FILTER_TARGET = 0

    /** 过滤器 启用状态 */
    const val KEY_FILTER_ENABLE_1 = "key_filter_enable_1"
    const val KEY_FILTER_ENABLE_2 = "key_filter_enable_2"
    const val DEF_FILTER_ENABLE = false

    /** 低电量 阈值 */
    const val LOW_ELEC = 10
    const val ELEC_CACHE = "elec_cache"

    /*射频功率*/
    const val KEY_RF_POWER = "key_rf_power"
    const val DEF_INVALID_POWER = -1
    const val DEF_INNER_POWER_MIN = 18
    const val DEF_INNER_POWER_MAX = 26
    const val DEF_UHF_POWER_MIN = 0
    const val DEF_UHF_POWER_MAX = 33

}
