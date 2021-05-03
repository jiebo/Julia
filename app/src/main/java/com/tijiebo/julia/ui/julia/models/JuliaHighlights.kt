package com.tijiebo.julia.ui.julia.models

import android.graphics.PointF

data class JuliaHighlights(
    val name: String,
    val c: PointF
) {
    companion object {
        fun getAll(): List<JuliaHighlights> = list
        fun get(index: Int) = list[index]

        private val list = listOf(
            JuliaHighlights("-0.4 - 0.6i", PointF(-0.4f, 0.6f)),
            JuliaHighlights("0.285 + 0.01i", PointF(0.285f, 0.01f)),
            JuliaHighlights("−0.70176 − 0.3842i", PointF(-0.70176f, -0.3842f)),
            JuliaHighlights("−0.835 − 0.2321i", PointF(-0.835f, -0.2321f)),
            JuliaHighlights("−0.8 + 0.156i", PointF(-0.8f, 0.156f)),
            JuliaHighlights("−0.7269 + 0.1889i", PointF(-0.7269f, 0.1889f)),
            JuliaHighlights("−0.8i", PointF(0f, -0.8f)),
        )
    }
}
