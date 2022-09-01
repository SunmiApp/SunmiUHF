package com.sunmi.uhf.constants

/**
 * @ClassName: EventConstant
 * @Description: java类作用描述
 * @Author: clh
 * @CreateDate: 20-9-8 下午4:36
 * @UpdateDate: 20-9-8 下午4:36
 */
object EventConstant {
    /** back 返回 */
    const val EVENT_BACK = 9999

    /** 快速点击 */
    const val EVENT_FAST_READ_WRITE = 1000

    /** 盘存 */
    const val EVENT_TAKE_INVENTORY = 1001

    /** 标签操作 */
    const val EVENT_LABEL_OPERATION = 1002

    /** 标签定位 */
    const val EVENT_LABEL_LOCATION = 1003

    /** 标签过滤 */
    const val EVENT_LABEL_FILTER = 1004

    /** 设置 */
    const val EVENT_SETTING = 1005


    /** 点击盘存模式 */
    const val EVENT_TAKE_MODEL = 1006

    /** 点击盘存模式 */
    const val EVENT_TAKE_MODEL_SEARCH = 1007


    /** 点击操作区域 */
    const val EVENT_OPERATION_AREA = 1008

    /** 点击操作类型 */
    const val EVENT_OPERATION_TYPE = 1009

    /** 读取数据 */
    const val EVENT_OPERATION_READ_DATA = 1010

    /** 写入数据 */
    const val EVENT_OPERATION_WRITE_DATA = 1011

    /** 锁标签 */
    const val EVENT_OPERATION_LOCK_TAG = 1012

    /** 销毁标签 */
    const val EVENT_OPERATION_DESTROY_TAG = 1013

    /** area name */
    const val LABEL_OPERATION_AREA = "label_operation_area"

    /** area type */
    const val LABEL_OPERATION_TYPE = "label_operation_type"

    /** rule type */
    const val LABEL_RULE_INDEX = "label_rule_index"

    /** session type */
    const val LABEL_SESSION = "label_session"

    /** label name */
    const val LABEL_NAME = "label_name"

    /** 过滤数据 */
    const val LABEL_FILTER_DATA = "label_filter_data"

    /** list select 选中 label */
    const val LABEL_SELECT = "label_select"

    /** 过滤器 读取区域 点击 */
    const val EVENT_BLOCK_CLICK = 1010

    /** 过滤器 过滤规则 点击 */
    const val EVENT_FILTER_RULE = 1011


    /** 过滤器 目标 点击 */
    const val EVENT_TARGET_CLICK = 1012


    /** 设置 选择标签 点击 */
    const val EVENT_SELECT_LABEL = 1013


    /** 设置 选择手柄 点击 */
    const val EVENT_SELECT_HANDLE = 1014

    /** 设置 盘存模式 点击 */
    const val EVENT_INVENTORY_MODE = 1015


    /** 设置 区域选择 点击 */
    const val EVENT_AREA_SETTING = 1016

    /** 设置 常规设置 点击 */
    const val EVENT_COMMON_SETTING = 1017

    /** 设置 关于设备 点击 */
    const val EVENT_ABOUT_DEVICE = 1018

    /** 设置 固件升级 点击 */
    const val EVENT_FIRMWARE_UPDATE = 1019

    /** 常规设置 手柄触发方式 点击 */
    const val EVENT_HANDLE_TYPE = 1020

    /** 常规设置 重启手柄 点击 */
    const val EVENT_HANDLE_REBOOT = 1021

    /** 固件升级 选择文件 点击 */
    const val EVENT_CHOICE_FILE = 1022

    /** 盘存模式 射频link 点击 */
    const val EVENT_LINK_URL = 1023

    /** 盘存模式 射频link 点击 */
    const val EVENT_SESSION_SELECT = 1024


    /** 区域设置 - 国家设置 */
    const val EVENT_AREA_COUNTRY = 1025

    /** 区域设置 - 开始频率 */
    const val EVENT_AREA_RF_START = 1026

    /** 区域设置 - 结束频率 */
    const val EVENT_AREA_RF_END = 1027

    /** 固件升级 点击升级 */
    const val EVENT_FIRMWARE_UPDATE_UPGRADE = 1028

    /** 盘存 复制EPC */
    const val EVENT_INVENTORY_COPY_EPC = 1029

    /** 盘存 分享 */
    const val EVENT_INVENTORY_SHARE = 1030

    /** 盘存 导出Excel */
    const val EVENT_INVENTORY_EXPORT_EXCEL = 1031

    /** 盘存 导出All Excel */
    const val EVENT_INVENTORY_EXPORT_EXCEL_ALL = 1032

    /** 盘存 标签信息 点击选择 */
    const val EVENT_TAKE_LABEL_INFO = 1033

    /** 盘存 标签信息 点击选择 */
    const val EVENT_TAKE_BABEL_FLAG = 1034

    /** 标签定位 点击 */
    const val EVENT_LABEL_LOCATION_CLICK = 1035

    /** 频率设置 - 频点间隔 */
    const val EVENT_FQ_INTERVAL = 1036

    /** 频率设置 - 频点数量 */
    const val EVENT_FQ_QUANTITY = 1037

    /**功率设置 */
    const val EVENT_POWER_CLICK = 1038

    /** 盘存底座 KeyEvent 事件 */
    const val UHF_KEY_EVENT = "uhf_key_event"

    /** 盘存底座 KeyEvent UP 事件 */
    const val EVENT_UHF_KEY_EVENT_UP = 9001

    /** 盘存底座 KeyEvent DOWN 事件 */
    const val EVENT_UHF_KEY_EVENT_DOWN = 9002

    /** 盘存底座 UHF 连接状态事件 */
    const val UHF_DEVICE_STATUS = "uhf_device_status"

    /** 盘存底座 UHF 连接状态 */
    const val EVENT_UHF_DEVICE_CONNECT = 9003

    /** 盘存底座 UHF 连接断开状态 */
    const val EVENT_UHF_DEVICE_DISCONNECT = 9004
}