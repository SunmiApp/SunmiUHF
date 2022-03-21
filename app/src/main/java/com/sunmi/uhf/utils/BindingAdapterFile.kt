package com.sunmi.uhf.utils

import android.view.View
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.databinding.BindingAdapter

/**
 * Copyright (C), 2018-2019, 商米科技有限公司
 * @author dean
 * Date 2020/4/15
 * Description
 */


///**  统一图片加载，glide
// *  imgRes - 图片id/url
// *  defaultId - 默认图片id
// *  errorId - 错误图片id
// *  optionType -  0--原始普通图片
// *                1--圆形图片
// *                2--椭圆角图片
// */
//@BindingAdapter(
//    "sunmi:imgRes",
//    "sunmi:defaultId",
//    "sunmi:errorId",
//    "sunmi:optionType",
//    requireAll = false
//)
//fun setImgResource(
//    view: ImageView?, res: Any?, defaultId: Drawable?, errorId: Drawable?,
//    optionType: Int = 0
//) {
////    LogUtils.d("IMG_LOAD", "setImg===v:$view;res:$res;def:$defaultId;" +
////            "err:$errorId;opT:$optionType")
//    if (view == null) return
//    var builder = Glide.with(App.context).load(res)
//    defaultId?.let { builder.placeholder(it) }
//    errorId?.let { builder.error(it) }
//    when (optionType) {
//        1 -> builder.apply(RequestOptions.bitmapTransform(CircleCrop()))
//        2 -> builder.apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
//        3 -> builder.apply(RequestOptions().transform(CenterCrop(), RoundedCorners(10)))
//
//    }
//    builder.into(view)
//}

@BindingAdapter("android:src")
fun setSrc(view: ImageView?, resId: Int) {
    view?.setImageResource(resId)
}

@BindingAdapter("outline")
fun setOutline(@Nullable view: View?, @Nullable size: String) {
    view?.clipToOutline = true
    view?.outlineProvider = when (size) {
        "half" -> OutlineProviderHelper.getOutlineProviderHalfHeight()
        "oval" -> OutlineProviderHelper.getOutlineProviderOval()
        else -> OutlineProviderHelper.getOutlineProvider(size.toFloat())
    }
}

//@BindingAdapter("sunmi:refresh", "sunmi:refreshing", requireAll = false)
//fun setRefresh(view: SwipeRefreshLayout?, block: () -> Unit, refreshing: Boolean = false) {
//    view?.setOnRefreshListener {
//        block()
//    }
//    view?.isRefreshing = refreshing
//    if (refreshing) block()
//}

//@BindingAdapter("sunmi:schemeColors")
//fun setSchemeColors(view: SwipeRefreshLayout?, primaryColor: Int) {
//    view?.setColorSchemeColors(primaryColor)
//}

/*@SuppressLint("ClickableViewAccessibility")
@BindingAdapter("sunmi:pressAlpha")
fun setPressAlpha(@Nullable view: View?,@Nullable flag: Boolean) {
    if (flag)
        view?.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN)
                v.alpha = 0.4f
            else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_OUTSIDE || event.action == MotionEvent.ACTION_CANCEL)
                v.alpha = 1f
            false
        }
}*/
