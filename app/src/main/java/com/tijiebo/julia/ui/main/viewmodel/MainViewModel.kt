package com.tijiebo.julia.ui.main.viewmodel

import android.graphics.PointF
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.subjects.PublishSubject

class MainViewModel() : ViewModel() {

    val resetJuliaView: PublishSubject<Boolean> = PublishSubject.create()
    val updateJuliaView: MutableLiveData<PointF> = MutableLiveData(PointF(-0.70176f, -0.3842f))
    val zoomJuliaView: MutableLiveData<Float> = MutableLiveData(1f)
    val enableZoomIn: MutableLiveData<Boolean> = MutableLiveData(true)
    val enableZoomOut: MutableLiveData<Boolean> = MutableLiveData(false)

    var zoomLevel = 0

    fun updateJuliaView(c: PointF) {
        updateJuliaView.value = c
        zoomLevel = 0
        observeZoomControls()
    }

    fun zoomJuliaView(zoom: Float) {
        zoomJuliaView.value = zoom
        zoomLevel += if (zoom > 1) 1 else -1
        observeZoomControls()
    }

    fun resetJuliaView() {
        resetJuliaView.onNext(true)
        zoomLevel = 0
        observeZoomControls()
    }

    private fun observeZoomControls() {
        enableZoomIn.postValue(zoomLevel <= 15)
        enableZoomOut.postValue(zoomLevel > 0)
    }

    override fun onCleared() {
        resetJuliaView.onComplete()
        super.onCleared()
    }
}