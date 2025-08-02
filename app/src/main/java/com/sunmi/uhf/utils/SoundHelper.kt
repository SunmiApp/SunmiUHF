package com.sunmi.uhf.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

/**
 * @author darren by Darren1009@qq.com - 2020/09/24
 */
class SoundHelper(
    private var context: Context?,
    private val soundVolType: Int = MEDIA_SOUND
) {
    /**
     * 声音池
     */
    private val soundPool: SoundPool

    /**
     * 添加的声音资源参数
     */
    private var soundPoolMap: HashMap<Int, Int>


    /**
     *  构造器内初始化
     *
     * @param context      上下文
     * @param soundVolType 声音音量类型，默认为多媒体
     */
    init {
        // 初始化声音池和声音参数map
        val audioAttributes = AudioAttributes.Builder()
        when(soundVolType) {
            // 铃声
            RING_SOUND -> {
                audioAttributes.setUsage(AudioAttributes.USAGE_ALARM)
                audioAttributes.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            }
            MEDIA_SOUND -> {
                audioAttributes.setUsage(AudioAttributes.USAGE_MEDIA)
                audioAttributes.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            }
            else -> {
                audioAttributes.setUsage(AudioAttributes.USAGE_MEDIA)
                audioAttributes.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            }
        }
        soundPool = SoundPool.Builder().setAudioAttributes(audioAttributes.build()).build()
        soundPoolMap = HashMap()
    }

    /**
     * 添加声音文件进声音池
     *
     * @param order    所添加声音的编号，播放的时候指定
     * @param soundRes 添加声音资源的id
     */
    fun putSound(order: Int, soundRes: Int) {
        if (context == null) return
        // 上下文，声音资源id，优先级
        soundPoolMap[order] = soundPool.load(context, soundRes, 1)
    }

    /**
     * 播放声音
     *
     * @param order 所添加声音的编号
     * @param times 循环次数，0无不循环，-1无永远循环
     */
    fun playSound(order: Int, times: Int) {
        if (context == null || !soundPoolMap.containsKey(order)) return
        // // 实例化AudioManager对象
        // val am = context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // // 返回当前AudioManager对象播放所选声音的类型的最大音量值
        // val maxVolumn = am.getStreamMaxVolume(soundVolType).toFloat()
        // // 返回当前AudioManager对象的音量值
        // val currentVolumn = am.getStreamVolume(soundVolType).toFloat()
        // // 比值
        // val volumnRatio = currentVolumn / maxVolumn
        // soundPool.play(soundPoolMap[order]!!, volumnRatio, volumnRatio, 1, times, 1f)
        soundPool.play(soundPoolMap[order]!!, 1F, 1F, 1, times, 1F)
        soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
            LogUtils.d("SoundHelper", "load complete")
        }
    }
    fun destroy() {
        context = null
        soundPool.release()
        soundPoolMap.clear()
    }

    companion object {
        /** 无限循环播放 */
        const val INFINITE_PLAY = -1

        /** 单次播放 */
        const val SINGLE_PLAY = 0

        /** 铃声音量 */
        const val RING_SOUND = 2

        /** 媒体音量 */
        const val MEDIA_SOUND = 3
    }
}