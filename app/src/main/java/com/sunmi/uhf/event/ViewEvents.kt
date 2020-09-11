package com.sunmi.uhf.event


/**
 * Created by lin on 19-12-26.
 */

/**
 * Class for passing events from ViewModels to Activities/Fragments
 * Variable [handled] used so each event is handled only once
 * (see https://medium.com/google-developers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150)
 */
abstract class ViewEvent {

    var handled = false
}

/**
 * 等待层显示隐藏事件
 */
class LoadingDialogEvent(var loading: Boolean) : ViewEvent()

/**
 * 基础事件
 */
class SimpleViewEvent(
    val event: Int
) : ViewEvent()




