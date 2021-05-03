package com.tijiebo.julia.ui.main

import android.graphics.PointF
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tijiebo.julia.R
import com.tijiebo.julia.ui.julia.models.JuliaHighlights

class HighlightsAdapter(
    private val list: List<JuliaHighlights>,
    private val onClick: (PointF) -> Unit
) :
    RecyclerView.Adapter<HighlightsAdapter.HighlightsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HighlightsViewHolder {
        return HighlightsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.highlights_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HighlightsViewHolder, position: Int) {
        holder.bind(list[position], onClick)
    }

    override fun getItemCount() = list.size

    inner class HighlightsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val button = itemView as? MaterialButton
        fun bind(item: JuliaHighlights, onClick: (PointF) -> Unit) {
            button?.apply {
                text = item.name
                setOnClickListener { onClick(item.c) }
            }
        }
    }
}