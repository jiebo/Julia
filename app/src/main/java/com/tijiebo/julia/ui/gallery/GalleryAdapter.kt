package com.tijiebo.julia.ui.gallery

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.StorageReference
import com.tijiebo.julia.R
import com.tijiebo.julia.ui.gallery.viewmodel.GalleryViewModel

class GalleryAdapter(private val list: MutableList<Uri>, private val vm: GalleryViewModel) :
    RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_item, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    fun updateData(newList: MutableList<Uri>) {
        this.list.clear()
        this.list.addAll(newList)
        this.notifyDataSetChanged()
    }

    inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.image)
        fun bind(uri: Uri) {
            Glide.with(image)
                .load(uri)
                .apply(
                    RequestOptions()
                        .error(R.drawable.ic_error_24)
                        .placeholder(R.drawable.ic_image_24)
                )
                .into(image)
            image.setOnClickListener { vm.showFullView(uri) }
        }
    }
}