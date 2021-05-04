package com.tijiebo.network

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.tijiebo.julia.ui.gallery.viewmodel.GalleryViewModel
import com.tijiebo.julia.ui.julia.models.JuliaImage
import io.reactivex.Observable
import java.io.ByteArrayOutputStream

class GalleryRepo {

    private val storage = Firebase.storage

    fun uploadToCloud(image: JuliaImage): Observable<GalleryViewModel.UploadStatus> {
        val imageRef = storage.reference.child("${image.name}_${System.currentTimeMillis()}.jpg")
        val stream = ByteArrayOutputStream()
        image.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        image.bitmap.recycle()
        return Observable.create { emitter ->
            imageRef.putBytes(stream.toByteArray())
                .addOnProgressListener { emitter.onNext(GalleryViewModel.UploadStatus.Pending((it.bytesTransferred / it.totalByteCount).toFloat())) }
                .addOnSuccessListener { emitter.onNext(GalleryViewModel.UploadStatus.Success) }
                .addOnFailureListener { emitter.onNext(GalleryViewModel.UploadStatus.Failure) }
        }
    }

    fun getFromCloud(): Observable<Uri> {
        return Observable.create { emitter ->
            storage.reference.listAll()
                .addOnSuccessListener {
                    var count = it.items.size
                    if (count == 0) {
                        emitter.onComplete()
                        return@addOnSuccessListener
                    }
                    for (item in it.items) {
                        item.downloadUrl
                            .addOnSuccessListener { uri ->
                                emitter.onNext(uri)
                                if (--count == 0) emitter.onComplete()
                            }
                            .addOnFailureListener {
                                if (--count == 0) emitter.onComplete()
                            }
                    }
                }
        }

    }

}