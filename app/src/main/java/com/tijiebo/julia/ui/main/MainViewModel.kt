package com.tijiebo.julia.ui.main

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.net.Uri
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.tijiebo.julia.ui.julia.models.JuliaImage
import io.reactivex.subjects.PublishSubject
import retrofit2.http.Url
import java.io.ByteArrayOutputStream

class MainViewModel() : ViewModel() {

    private val storage = Firebase.storage

    val uploadProgress: PublishSubject<Float> = PublishSubject.create()
    val resetJuliaView: PublishSubject<Boolean> = PublishSubject.create()
    val updateJuliaView: MutableLiveData<PointF> = MutableLiveData(PointF(0f, -0.8f))
    val zoomJuliaView: MutableLiveData<Float> = MutableLiveData(1f)
    val imagesOnCloud: MutableLiveData<MutableList<StorageReference>> = MutableLiveData()

    fun getJuliaImage(view: View, name: String) {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap).apply {
            drawColor(Color.WHITE)
        }
        view.draw(canvas)
        uploadToCloud(JuliaImage(name, bitmap))
    }

    private fun uploadToCloud(image: JuliaImage) {
        val imageRef = storage.reference.child("${image.name}.jpg")
        val stream = ByteArrayOutputStream()
        image.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        imageRef.putBytes(stream.toByteArray())
            .addOnProgressListener { uploadProgress.onNext((it.bytesTransferred / it.totalByteCount).toFloat()) }
            .addOnSuccessListener { uploadProgress.onNext(1f) }
            .addOnFailureListener { uploadProgress.onNext(-1f) }
        image.bitmap.recycle()
    }

    fun updateJuliaView(c: PointF) {
        updateJuliaView.value = c
    }

    fun zoomJuliaView(zoom: Float) {
        zoomJuliaView.value = zoom
    }

    fun resetJuliaView() {
        resetJuliaView.onNext(true)
    }

    fun getFromCloud() {
        val listRef = storage.reference
        listRef.listAll()
            .addOnSuccessListener { imagesOnCloud.postValue(it.items) }
    }

    override fun onCleared() {
        resetJuliaView.onComplete()
        uploadProgress.onComplete()
        super.onCleared()
    }
}