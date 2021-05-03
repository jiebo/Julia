package com.tijiebo.julia.ui.julia

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.tijiebo.julia.R
import kotlin.math.abs

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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            for (w in 0..width) {
                for (h in 0..height) {
                    val x = centerX() - w
                    val y = h - centerY()
                    if (isValid(x, y)) drawCircle(w.toFloat(), h.toFloat(), 1f, painter)
                }
            }
            drawLine(centerX() - 50f, centerY(), centerX() + 50f, centerY(), originPainter)
            drawLine(centerX(), centerY() - 50f, centerX(), centerY() + 50f, originPainter)
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

    fun updateConstant(c: PointF) {
        this.constantX = c.x
        this.constantY = c.y
        invalidate()
    }

    fun getC() = PointF(constantX, constantY)

    companion object {
        const val zoom = 250
    }
}