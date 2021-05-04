package com.tijiebo.julia.ui.gallery.viewmodel

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tijiebo.julia.ui.julia.models.JuliaImage
import com.tijiebo.network.GalleryRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class GalleryViewModel : ViewModel() {
    private val repo = GalleryRepo()
    private val disposables = CompositeDisposable()
    val uploadProgress: PublishSubject<UploadStatus> = PublishSubject.create()
    val fullView: PublishSubject<Uri> = PublishSubject.create()
    val imagesOnCloud: MutableLiveData<GalleryStatus> = MutableLiveData()

    fun getJuliaImage(view: View, name: String) {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap).apply {
            drawColor(Color.WHITE)
        }
        view.draw(canvas)
        uploadToCloud(JuliaImage(name, bitmap))
    }

    private fun uploadToCloud(image: JuliaImage) {
        disposables.add(
            repo.uploadToCloud(image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    uploadProgress.onNext(it)
                }, {
                })
        )
    }

    fun getFromCloud() {
        disposables.add(
            repo.getFromCloud()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .doOnSubscribe { imagesOnCloud.postValue(GalleryStatus.Pending) }
                .subscribe({
                    imagesOnCloud.postValue(
                        if (it.size == 0) GalleryStatus.Empty
                        else GalleryStatus.Success(it)
                    )
                }, {
                    imagesOnCloud.postValue(GalleryStatus.Empty)
                })
        )
    }

    fun showFullView(uri: Uri) {
        fullView.onNext(uri)
    }

    override fun onCleared() {
        uploadProgress.onComplete()
        fullView.onComplete()
        super.onCleared()
    }

    sealed class UploadStatus {
        object Success : UploadStatus()
        object Failure : UploadStatus()
        class Pending(val progress: Float) : UploadStatus()
    }

    sealed class GalleryStatus {
        object Pending : GalleryStatus()
        object Empty : GalleryStatus()
        class Success(val list: MutableList<Uri>) : GalleryStatus()
    }
}