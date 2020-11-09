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
import kotlin.math.cos
import kotlin.math.sin
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

    //指示器 刻度上的文案
    private val mTextAnger: Array<Float> = arrayOf(
        4f,
        6f,
        10f
    )

    //线条和文字的颜色
    private val mLineColor: Int = ContextCompat.getColor(context, R.color.signalIndicatorColor)

    //文字的size
    private val mTextSize: Float = context.resources.getDimension(R.dimen.sunmi_28ps)

    private val mSignalColorArray: Array<Int> = arrayOf(
        ContextCompat.getColor(context, R.color.colorAccent20),
        ContextCompat.getColor(context, R.color.colorAccent60),
        ContextCompat.getColor(context, R.color.colorAccent),
        ContextCompat.getColor(context, R.color.colorAccent)
    )

    //默认 宽 高
    private var defaultSize: Int = context.resources.getDimensionPixelSize(R.dimen.sunmi_360px)

    //线条和文字的 paint
    private val mLinePaint = Paint()

    //信号区域 paint
    private val mSignalPaint = Paint()


    private val mBgPaint = Paint()

    //信号值
    private var mSignal: Float = 0f

    private val mDiff: Float = 0.02f

    private val leftStartAngle: Float = 220f
    private val rightStartAngle: Float = 270f
    private val sweepAngle: Float = 50f
    private val angle: Double = 40.0                     //90-50=40

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
        if (mHeight > defaultSize) mHeight = defaultSize
        setMeasuredDimension(mWidth, mHeight)
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
        val centerX = width / 2
        val centerY = height - paddingBottom
        var arcRadius = height - textHeight / 2 - paddingTop - paddingBottom
        mLinePaint.style = Paint.Style.STROKE
        mLinePaint.color = mLineColor
        //画边线
        canvas?.drawLine(
            centerX.toFloat(),
            centerY.toFloat(),
            centerX - (cos(Math.toRadians(angle)) * arcRadius).toFloat(),
            centerY - (sin(Math.toRadians(angle)) * arcRadius).toFloat(),
            mLinePaint
        )
        canvas?.drawLine(
            centerX.toFloat(),
            centerY.toFloat(),
            centerX + (cos(Math.toRadians(angle)) * arcRadius).toFloat(),
            centerY - (sin(Math.toRadians(angle)) * arcRadius).toFloat(),
            mLinePaint
        )
        val rate = mSignal / 100f
        val textRate = textHeight / arcRadius + mDiff
        if (mSignal > 0) {
            //绘制信号强度
            mSignalPaint.color = mSignalColorArray[(rate / (1f / size)).toInt()]
            canvas?.drawArc(
                (centerX - arcRadius * rate),
                (centerY - arcRadius * rate),
                (centerX + arcRadius * rate),
                (centerY + arcRadius * rate),
                leftStartAngle,
                sweepAngle * 2,
                true,
                mSignalPaint
            )
        }
        mTextArray.forEachIndexed { index, s ->
            mLinePaint.style = Paint.Style.STROKE
            //画左边弧线
            val m = (size - index).toFloat() / size.toFloat() + textRate

            if (rate >= m) {
                mLinePaint.color = ContextCompat.getColor(context, R.color.white)
            } else {
                mLinePaint.color = mLineColor
            }
            canvas?.drawArc(
                centerX - (arcRadius / size * (size - index)).toFloat(),
                centerY - (arcRadius / size * (size - index)).toFloat(),
                centerX + (arcRadius / size * (size - index)).toFloat(),
                centerY + (arcRadius / size * (size - index)).toFloat(),
                leftStartAngle,
                sweepAngle - mTextAnger[index],
                false,
                mLinePaint
            )
            //画右边弧线
            canvas?.drawArc(
                centerX - (arcRadius / size * (size - index)).toFloat(),
                centerY - (arcRadius / size * (size - index)).toFloat(),
                centerX + (arcRadius / size * (size - index)).toFloat(),
                centerY + (arcRadius / size * (size - index)).toFloat(),
                rightStartAngle + mTextAnger[index],
                sweepAngle - mTextAnger[index],
                false,
                mLinePaint
            )
            //绘制文字
            mLinePaint.style = Paint.Style.FILL
            canvas?.drawText(
                s,
                centerX - (textWidth / 2).toFloat(),
                centerY - (arcRadius / size * (size - index)).toFloat() + textHeight / 2,
                mLinePaint
            )
        }
    }

    /**
     * 设置 信号强度值  范围 （0~100）
     */
    fun setSignal(signal: Float) {
        mSignal = signal
        if (signal > 100) mSignal = 100f
        if (signal < 0) mSignal = 0f
        invalidate()
    }
}