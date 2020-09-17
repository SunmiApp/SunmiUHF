package com.sunmi.uhf.view


import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log

import android.view.View
import androidx.core.content.ContextCompat
import com.sunmi.uhf.R
import kotlin.math.tan


/**
 * @ClassName: SignalIndicatorView
 * @Description: 信号强度view
 * @Author: clh
 * @CreateDate: 20-9-10 下午4:10
 * @UpdateDate: 20-9-10 下午4:10
 */
class SignalIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    //指示器 刻度上的文案
    private val mTextArray: Array<String> = arrayOf(
        context.resources.getString(R.string.strong_text),
        context.resources.getString(R.string.middle_text),
        context.resources.getString(R.string.little_text)
    )

    //线条和文字的颜色
    private val mLineColor: Int = ContextCompat.getColor(context, R.color.signalIndicatorColor)

    //文字的size
    private val mTextSize: Float = context.resources.getDimension(R.dimen.sunmi_28ps)

    //信号区域颜色
    private val mSignalColor: Int = ContextCompat.getColor(context, R.color.colorAccent)

    //默认 宽 高
    private var defaultSize: Int = context.resources.getDimensionPixelSize(R.dimen.sunmi_360px)

    //线条和文字的 paint
    private val mLinePaint = Paint()

    //信号区域 paint
    private val mSignalPaint = Paint()


    private val mBgPaint = Paint()

    //信号值
    private var mSignal: Float = 0f

    private val mDiff: Float = 2f

    private val startAngle: Float = -40f

    private val sweepAngle: Float = -100f

    private var textWidth = 0                            //文字宽度
    private var textHeight = 0                           //文字高度
    private var size = mTextArray.size
    private val avgSignal = 100f / size             //平均 信号高度

    init {
        // 绘制居中文字
        mLinePaint.isAntiAlias = true
        mLinePaint.textSize = mTextSize
        mLinePaint.color = mLineColor
        mLinePaint.style = Paint.Style.STROKE
        // 信号区域
        mSignalPaint.isAntiAlias = true
        mSignalPaint.color = mSignalColor
        //mBgPaint
        mBgPaint.isAntiAlias = true
        mBgPaint.color = ContextCompat.getColor(context, R.color.white)
        //计算 文字高度
        val textBound = Rect()
        mLinePaint.getTextBounds(mTextArray[0], 0, mTextArray[0].length, textBound)
        textWidth = textBound.width()
        textHeight = textBound.height()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var mWidth = getSize(defaultSize, widthMeasureSpec)
        var mHeight = getSize(defaultSize, heightMeasureSpec)
        Log.i("xcfdsaf","1 mWidth=$mWidth,mHeight:$mHeight,defaultSize:$defaultSize")
        if (mHeight > defaultSize) mHeight = defaultSize
        if (mWidth > defaultSize) mWidth = defaultSize
        mWidth = mWidth.coerceAtMost(mHeight)
        mHeight = mWidth
        Log.i("xcfdsaf","2 mWidth=$mWidth,mHeight:$mHeight")
        setMeasuredDimension(mWidth, mHeight);
    }

    private fun getSize(defaultSize: Int, measureSpec: Int): Int {
        var mySize = defaultSize
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        when (mode) {
            MeasureSpec.UNSPECIFIED -> {
                //如果没有指定大小，就设置为默认大小
                mySize = defaultSize
            }
            MeasureSpec.AT_MOST -> {
                //如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                mySize = size
            }
            MeasureSpec.EXACTLY -> {
                //如果是固定的大小，那就不要去改变它
                mySize = size
            }
        }
        return mySize
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val textX = width / 2 - textWidth / 2
        //信号值对应的单位高度
        val unitSignalHeight = (height - paddingTop - textHeight / 2) / 100f
        //平均间距
        val d = (height - paddingTop) / size
        mTextArray.forEachIndexed { index, s ->
            //弧线
            mLinePaint.style = Paint.Style.STROKE
            canvas?.drawArc(
                ((width / (size * 2)) * index + textHeight / 2).toFloat(),
                (paddingTop + d * index + textHeight / 2).toFloat(),
                (width - ((width / (size * 2)) * index + textHeight / 2)).toFloat(),
                (height + (height - (paddingTop + d * index + textHeight / 2))).toFloat(),
                startAngle,
                sweepAngle,
                index == 0,
                mLinePaint
            )
            val indexSignal = (size - index) * avgSignal
            if (mSignal < (indexSignal - mDiff).toInt()) {
                //绘制文字背景
                canvas?.drawRect(
                    ((width / 2) - textWidth).toFloat(),
                    (paddingTop + d * index).toFloat(),
                    ((width / 2) + textWidth).toFloat(),
                    (paddingTop + d * index + textHeight).toFloat(), mBgPaint
                )
                //绘制文字
                mLinePaint.style = Paint.Style.FILL
                canvas?.drawText(
                    s,
                    textX.toFloat(),
                    (paddingTop + d * index + textHeight).toFloat(),
                    mLinePaint
                )
            }
            //绘制信号强度
            if (mSignal > 0) {
                val mSignalHeight = mSignal * unitSignalHeight
                val angle = 42.0
                val mSignalWidth = (tan(Math.toRadians(angle)) * mSignalHeight).toFloat()
                canvas?.drawArc(
                    width / 2f - mSignalWidth,
                    height - mSignalHeight,
                    width / 2f + mSignalWidth,
                    height + mSignalHeight, startAngle, sweepAngle, true, mSignalPaint
                )
            }
        }
    }

    /**
     * 设置 信号强度值  范围 （0~100）
     */
    fun setSignal(signal: Float) {
        if (signal > 100) mSignal = 100f
        if (signal < 0) mSignal = 0f
        invalidate()
    }
}