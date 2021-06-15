package com.example.customfancontroller

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


private enum class FanSpeed(val label: Int) {
    OFF(R.string.fan_off),
    LOW(R.string.fan_low),
    MEDIUM(R.string.fan_medium),
    HIGH(R.string.fan_high);

    fun next() = when (this) {
        OFF -> LOW
        LOW -> MEDIUM
        MEDIUM -> HIGH
        HIGH -> OFF
    }
}

private const val RADIUS_OFFSET_LABEL = 30
private const val RADIUS_OFFSET_INDICATOR = -35

/**
 * The @JvmOverloads annotation instructs the Kotlin compiler to
 * generate overloads for this function that substitute default parameter values
 *
 * **/
class DialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttributes: Int = 0
) : View(context, attrs, defStyleAttributes) {

    // value is set when the view is drawn on the screen
    private var radius = 0.0f
    private var fanSpeed = FanSpeed.OFF

    // position variable which will be used to draw label and indicator circle position
    private val pointPosition: PointF = PointF(0.0f, 0.0f)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private var fanSpeedLowColor = 0
    private var fanSpeedMediumColor = 0
    private var fanSeedMaxColor = 0

    init {
        isClickable = true
        // custom attribute are stored in an AttributeSet, that is handed to your class upon creation
        context.withStyledAttributes(attrs, R.styleable.DialView) {
            fanSpeedLowColor = getColor(R.styleable.DialView_fanColor1, 0)
            fanSpeedMediumColor = getColor(R.styleable.DialView_fanColor2, 0)
            fanSeedMaxColor = getColor(R.styleable.DialView_fanColor3, 0)
        }
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true
        fanSpeed = fanSpeed.next()
        contentDescription = resources.getString(fanSpeed.label)
        invalidate()
        return true
    }

    // to calculate the size of custom view's dial and called once
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = (min(width, height) / 2.0 * 0.8).toFloat()
    }

    /**
     *  calculates the X, Y coordinates on the screen
     *  for the text label and current indicator (0, 1, 2, or 3)
     * **/
    private fun PointF.computeXYForSpeed(pos: FanSpeed, radius: Float) {
        val startAngle = Math.PI * (9 / 8.0)
        val angle = startAngle + pos.ordinal * (Math.PI / 4)
        x = (radius * cos(angle)).toFloat() + width / 2
        y = (radius * sin(angle)).toFloat() + height / 2
    }

    // to render the view on the screen with the Canvas and Paint classes
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // draw a dial and set dial background color to green if selection not off
        paint.color = when (fanSpeed) {
            FanSpeed.OFF -> Color.GRAY
            FanSpeed.LOW -> fanSpeedLowColor
            FanSpeed.MEDIUM -> fanSpeedMediumColor
            FanSpeed.HIGH -> fanSeedMaxColor
        }
        canvas?.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)

        // draw an indicator
        val markerRadius = radius + RADIUS_OFFSET_INDICATOR
        pointPosition.computeXYForSpeed(fanSpeed, markerRadius)
        paint.color = Color.BLACK
        canvas?.drawCircle(pointPosition.x, pointPosition.y, radius / 12, paint)

        // Draw the text labels
        val labelRadius = radius + RADIUS_OFFSET_LABEL
        for (i in FanSpeed.values()) {
            pointPosition.computeXYForSpeed(i, labelRadius)
            val label = resources.getString(i.label)
            canvas?.drawText(label, pointPosition.x, pointPosition.y, paint)
        }
    }

}