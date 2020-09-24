package com.sunmi.uhf.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @ClassName: CommonListBean
 * @Description: java类作用描述
 * @Author: clh
 * @CreateDate: 20-9-15 下午1:59
 * @UpdateDate: 20-9-15 下午1:59
 */
@Parcelize
data class CommonListBean(
    var type: Int? = 0,
    var index: Int? = 0,
    var select: String?
) : Parcelable

