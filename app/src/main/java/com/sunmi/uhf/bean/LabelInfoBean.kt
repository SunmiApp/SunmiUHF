package com.sunmi.uhf.bean

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

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
) : Serializable, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(epc)
        parcel.writeString(pc)
        parcel.writeValue(findNum)
        parcel.writeString(rssi)
        parcel.writeString(frequency)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LabelInfoBean> {
        override fun createFromParcel(parcel: Parcel): LabelInfoBean {
            return LabelInfoBean(parcel)
        }

        override fun newArray(size: Int): Array<LabelInfoBean?> {
            return arrayOfNulls(size)
        }
    }
}