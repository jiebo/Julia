package com.tijiebo.julia.ui.julia

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.tijiebo.julia.R
import kotlin.math.abs
import kotlin.math.max

class JuliaView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val painter = Paint().apply {
        color = context.getColor(R.color.purple_500)
        style = Paint.Style.STROKE
    }

    private val textPainter = Paint().apply {
        color = ContextCompat.getColor(context, R.color.grey)
        typeface = Typeface.create(context.resources.getFont(R.font.math), Typeface.ITALIC)
        textAlign = Paint.Align.RIGHT
        textSize = 64f
    }

    private val originPainter = Paint().apply {
        color = ContextCompat.getColor(context, android.R.color.black)
        strokeWidth = 5f
    }
    private var constantX = 0f
    private var constantY = -0.8f
    private var zoom = ZOOM
    private var panX = PAN_X
    private var panY = PAN_Y

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            println("Zoom is $zoom, Pan X is $panX, Pan Y is $panY")
            for (w in 0..width) {
                for (h in 0..height) {
                    val x = centerX() + panX - w
                    val y = h - centerY() + panY
                    if (isValid(x, y)) drawCircle(w.toFloat(), h.toFloat(), 1f, painter)
                }
            }
            drawLine(
                centerX() + panX - 50f,
                centerY() - panY,
                centerX() + panX + 50f,
                centerY() - panY,
                originPainter
            )
            drawLine(
                centerX() + panX,
                centerY() - panY - 50f,
                centerX() + panX,
                centerY() - panY + 50f,
                originPainter
            )
            drawText(
                if (constantY < 0) "$constantX \u2013 ${abs(constantY)}\u03AF"
                else "$constantX + ${constantY}ί",
                width.toFloat() - 10f,
                height.toFloat() - 10f,
                textPainter
            )
        }
    }

    private fun isValid(x: Float, y: Float): Boolean {
        var tmpX = x / zoom
        var tmpY = y / zoom
        var counter = 50
        while (counter-- > 0 && (tmpX * tmpX + tmpY * tmpY) < 4) {
            // Essentially a quadratic equation. Just need to get the coefficients
            val tmp = tmpX * tmpX - tmpY * tmpY + constantX
            tmpY = 2 * tmpX * tmpY + constantY
            tmpX = tmp
        }
        return counter <= 0
    }

    private fun centerX() = width / 2f
    private fun centerY() = height / 2f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event) ||
                super.onTouchEvent(event)
    }

    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.OnGestureListener {
            override fun onDown(p0: MotionEvent?) = true
            override fun onShowPress(p0: MotionEvent?) {}
            override fun onSingleTapUp(p0: MotionEvent?) = true
            override fun onLongPress(p0: MotionEvent?) {}
            override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float) = true

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                updateView(distanceX.toInt(), distanceY.toInt())
                return false
            }
        })

    fun updateConstant(c: PointF) {
        this.constantX = c.x
        this.constantY = c.y
        reset()
    }

    fun updateView(panX: Int? = null, panY: Int? = null, zoom: Float? = null) {
        zoom?.let {
            this.zoom = max(250, (this.zoom * it).toInt())
            this.panX = (this.panX * it).toInt()
            this.panY = (this.panY * it).toInt()
        }
        panX?.let { this.panX -= it }
        panY?.let { this.panY += it }
        invalidate()
    }

    fun reset() {
        this.zoom = ZOOM
        this.panX = PAN_X
        this.panY = PAN_Y
        invalidate()
    }

    fun getC() = PointF(constantX, constantY)

    companion object {
        const val ZOOM = 250
        const val PAN_X = 0
        const val PAN_Y = 0
    }
}