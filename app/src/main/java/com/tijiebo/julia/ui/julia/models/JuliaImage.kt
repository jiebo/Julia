package com.tijiebo.julia.ui.julia.models

import android.graphics.Bitmap
import android.graphics.PointF

data class JuliaImage(
    val name: String,
    val bitmap: Bitmap
) {
    companion object {
        fun getNameFrom(c: PointF) = "julia_${c.x}_${c.y}.jpg"
    }
}
