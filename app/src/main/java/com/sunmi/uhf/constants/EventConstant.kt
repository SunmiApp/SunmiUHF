package com.sunmi.uhf.constants

/**
 * @ClassName: EventConstant
 * @Description: java类作用描述
 * @Author: clh
 * @CreateDate: 20-9-8 下午4:36
 * @UpdateDate: 20-9-8 下午4:36
 */
object EventConstant {
    /* back 返回 */
    const val EVENT_BACK = 9999


    /* 快速点击 */
    const val EVENT_FAST_READ_WRITE = 1000

    /* 盘存 */
    const val EVENT_TAKE_INVENTORY = 1001

    /* 标签操作 */
    const val EVENT_LABEL_OPERATION = 1002

    /* 标签定位 */
    const val EVENT_LABEL_LOCATION = 1003

    /* 标签过滤 */
    const val EVENT_LABEL_FILTER = 1004

    /* 设置 */
    const val EVENT_SETTING = 1005


    /* 点击盘存模式 */
    const val EVENT_TAKE_MODEL = 1006

    /* 点击盘存模式 */
    const val EVENT_TAKE_MODEL_SEARCH = 1007


    /* 点击操作区域 */
    const val EVENT_OPERATION_AREA = 1008

    /* 点击操作类型 */
    const val EVENT_OPERATION_TYPE = 1009


    /* area name */
    const val LABEL_OPERATION_AREA = "label_operation_area"


    /* area type */
    const val LABEL_OPERATION_TYPE = "label_operation_type"


}