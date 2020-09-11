package com.sunmi.uhf.bean

/**
 * @ClassName: LabelInfoBean
 * @Description: java类作用描述
 * @Author: clh
 * @CreateDate: 20-9-9 下午6:17
 * @UpdateDate: 20-9-9 下午6:17
 */
data class LabelInfoBean(
    var epc: String?,
    var pc: String?,
    var findNum: Int?,
    var rssi: String?,
    var frequency: String?
) {}