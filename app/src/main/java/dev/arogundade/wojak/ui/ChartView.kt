package dev.arogundade.wojak.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import dev.arogundade.wojak.R
import dev.arogundade.wojak.models.ChartData
import dev.arogundade.wojak.utils.Extensions
import dev.arogundade.wojak.utils.Extensions.toPositive

class ChartView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private val oneDp = context.resources.getDimension(R.dimen.dp1)
    private val halfDp = oneDp / 2

    private val radius = (10 * oneDp)
    private val smRadius = (6 * oneDp)
    private val thinRadius = (3 * oneDp)

    private val chartFont = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.resources.getFont(R.font.quicksand_semibold)
    } else {
        Typeface.DEFAULT
    }

    private val chartData = mutableListOf<ChartData>()

    private var chartHeight = 0F
    private var chartWidth = 0F
    private var yAxisSpace = 0F

    private var highestData = Double.MIN_VALUE
    private var lowestData = Double.MAX_VALUE
    private var activeYAxis = -1

    private val strokeColor =
        ContextCompat.getColor(context, R.color.md_theme_light_onSecondaryContainer)

    private val strokePaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = oneDp
        color = strokeColor
        strokeJoin = Paint.Join.ROUND
        pathEffect = CornerPathEffect(10 * oneDp)
    }

    private val strokePaintDashed = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = oneDp
        color = strokeColor
        strokeJoin = Paint.Join.ROUND
        pathEffect = DashPathEffect(floatArrayOf(4 * oneDp, 16 * oneDp), 0f)
    }

    private val toolTipTextPaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        textSize = 12 * oneDp
        typeface = Typeface.create(chartFont, Typeface.NORMAL)
    }
    private val toolTipPaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = strokeColor
    }

    private val strokePath = Path()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        chartHeight = height.toFloat()
        chartWidth = width.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (index in chartData.indices) {
            val data = chartData[index]

            val priceChange = data.price

            // get data coordinates
            val pointY = getYAxis(priceChange)
            val pointX = getXAxis(index)

            when (index) {
                0 -> {
                    // a little offset from start
                    strokePath.moveTo(-1 * (10 * oneDp), pointY)
                    strokePath.lineTo(pointX, pointY)
                }
                chartData.indices.last -> {
                    strokePath.lineTo(pointX, pointY)
                    // a little offset on end
                    strokePath.lineTo(pointX + (10 * oneDp), pointY)
                }
                else -> strokePath.lineTo(pointX, pointY)
            }
        }

        canvas.drawPath(strokePath, strokePaint)

        if (activeYAxis != -1 && activeYAxis < chartData.size) {
            canvas.showToolTip(activeYAxis)
        }
    }

    private fun getXAxis(index: Int): Float {
        return (index * yAxisSpace)
    }

    fun setData(chartData: List<ChartData>) {
        this.chartData.clear()
        this.chartData.addAll(chartData)
        highestData = Double.MIN_VALUE
        lowestData = Double.MAX_VALUE
        activeYAxis = -1

        this.chartData.forEach { data ->
            val priceChange = data.price
            if (highestData < priceChange) {
                highestData = priceChange
            }
            if (lowestData > priceChange) {
                lowestData = priceChange
            }
        }

        yAxisSpace = (chartWidth / (this.chartData.size - 1))
        strokePath.reset()

        invalidate()
    }

    private fun getAbsolutePrice(priceChange: Double): Double {
        return if (lowestData < 0) {
            priceChange + lowestData.toPositive()
        } else {
            priceChange - lowestData.toPositive()
        }
    }

    private fun getYAxis(priceChange: Double): Float {
        val perc = (getAbsolutePrice(priceChange) / getAbsolutePrice(highestData))
        val onChartPerc = (perc * chartHeight)

        return when (val position = (chartHeight - onChartPerc).toFloat()) {
            in 0f..0.0f -> position + 2f
            chartHeight -> position - 2f
            else -> position
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun applyTouch(onData: (touchTarget: ClosedFloatingPointRange<Float>, view: ChartView) -> Unit) {
        this.setOnTouchListener { _, motionEvent ->
            val xPosition = motionEvent.x

            val touchPadding = (yAxisSpace / 2)
            val touchRange = (xPosition - touchPadding)..(xPosition + touchPadding)

            onData(touchRange, this)
            true
        }
    }

    private fun Canvas?.showToolTip(index: Int) {
        val data = chartData[index]
        val priceChange = data.price

        val text = when {
            priceChange > 0.0 -> "+$${Extensions.smartRound(priceChange)}"
            priceChange < 0.0 -> "-$${Extensions.smartRound(priceChange, true)}"
            else -> "$${Extensions.smartRound(priceChange)}"
        }

        val pointX = getXAxis(activeYAxis)

        val textBounds = Rect()
        toolTipTextPaint.getTextBounds(text, 0, text.length, textBounds)

        this@showToolTip?.drawLine(
            pointX - halfDp,
            0f,
            pointX + oneDp,
            chartHeight,
            strokePaintDashed
        )

        if (index < 3) {
            this?.drawCircle(
                pointX,
                (chartHeight / 2) - oneDp,
                smRadius,
                toolTipPaint
            )

            val diff = (textBounds.height() / 2) + oneDp

            this?.drawRoundRect(
                RectF(
                    pointX - thinRadius,
                    (chartHeight / 2) - thinRadius - diff,
                    textBounds.width() + pointX + radius,
                    (chartHeight / 2) + textBounds.height() + thinRadius - diff
                ),
                radius, radius,
                toolTipPaint
            )

            this?.drawText(
                text,
                pointX + thinRadius,
                (chartHeight / 2) + radius - diff,
                toolTipTextPaint
            )

            return
        }

        val lasts = chartData.indices.last - 3
        if (lasts > 0 && index in lasts..chartData.indices.last) {

            this?.drawCircle(
                pointX,
                (chartHeight / 2) - oneDp,
                smRadius,
                toolTipPaint
            )

            val diff = (textBounds.height() / 2) + oneDp
            val leftDiff = textBounds.width() + radius

            this?.drawRoundRect(
                RectF(
                    pointX - leftDiff,
                    (chartHeight / 2) - thinRadius - diff,
                    textBounds.width() + pointX + radius + thinRadius - leftDiff,
                    (chartHeight / 2) + textBounds.height() + thinRadius - diff
                ),
                radius, radius,
                toolTipPaint
            )

            this?.drawText(
                text,
                pointX + smRadius - leftDiff,
                (chartHeight / 2) + radius - diff,
                toolTipTextPaint
            )

            return
        }

        this@showToolTip?.drawCircle(
            pointX,
            (chartHeight / 2) - oneDp,
            smRadius,
            toolTipPaint
        )

        this@showToolTip?.drawRoundRect(
            RectF(
                pointX - textBounds.exactCenterX(),
                (chartHeight / 2) - thinRadius,
                pointX + textBounds.exactCenterX() + radius + thinRadius,
                (chartHeight / 2) + textBounds.height() + thinRadius
            ),
            radius, radius,
            toolTipPaint
        )

        this@showToolTip?.drawText(
            text,
            pointX - textBounds.exactCenterX() + smRadius,
            (chartHeight / 2) + radius,
            toolTipTextPaint
        )
    }

    fun openLabelFor(range: ClosedFloatingPointRange<Float>) {
        for (i in chartData.indices) {
            if (i * yAxisSpace > range.start &&
                i * yAxisSpace < range.endInclusive
            ) {
                activeYAxis = i
                invalidate()
            }
        }
    }

}