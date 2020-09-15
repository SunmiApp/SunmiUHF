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


    /* rule type */
    const val LABEL_RULE_INDEX = "label_rule_index"

    /* session type */
    const val LABEL_SESSION = "label_session"

    /* label name */
    const val LABEL_NAME = "label_name"


    /* list select 选中 label */
    const val LABEL_SELECT = "label_select"

    /* 过滤器 读取区域 点击 */
    const val EVENT_BLOCK_CLICK = 1010

    /* 过滤器 过滤规则 点击 */
    const val EVENT_FILTER_RULE = 1011


    /* 过滤器 目标 点击 */
    const val EVENT_TARGET_CLICK = 1012


    /* 设置 选择标签 点击 */
    const val EVENT_SELECT_LABEL = 1013


    /* 设置 选择手柄 点击 */
    const val EVENT_SELECT_HANDLE = 1014

    /* 设置 盘存模式 点击 */
    const val EVENT_INVENTORY_MODE = 1015


    /* 设置 区域选择 点击 */
    const val EVENT_AREA_SETTING = 1016

    /* 设置 常规设置 点击 */
    const val EVENT_COMMON_SETTING = 1017

    /* 设置 关于设备 点击 */
    const val EVENT_ABOUT_DEVICE = 1018

    /* 设置 固件升级 点击 */
    const val EVENT_FIRMWARE_UPDATE = 1019

    /* 常规设置 手柄触发方式 点击 */
    const val EVENT_HANDLE_TYPE = 1020

    /* 常规设置 重启手柄 点击 */
    const val EVENT_HANDLE_REBOOT = 1021

    /* 固件升级 选择文件 点击 */
    const val EVENT_CHOICE_FILE = 1022

    /* 盘存模式 射频link 点击 */
    const val EVENT_LINK_URL = 1023

    /* 盘存模式 射频link 点击 */
    const val EVENT_SESSION_SELECT = 1024
}