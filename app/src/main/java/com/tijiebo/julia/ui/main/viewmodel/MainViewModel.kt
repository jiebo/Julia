package com.tijiebo.julia.ui.main.viewmodel

import android.graphics.PointF
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.subjects.PublishSubject

class MainViewModel() : ViewModel() {

    val resetJuliaView: PublishSubject<Boolean> = PublishSubject.create()
    val updateJuliaView: MutableLiveData<PointF> = MutableLiveData(PointF(0f, -0.8f))
    val zoomJuliaView: MutableLiveData<Float> = MutableLiveData(1f)

    fun updateJuliaView(c: PointF) {
        updateJuliaView.value = c
    }

    fun zoomJuliaView(zoom: Float) {
        zoomJuliaView.value = zoom
    }

    fun resetJuliaView() {
        resetJuliaView.onNext(true)
    }

    override fun onCleared() {
        resetJuliaView.onComplete()
        super.onCleared()
    }
}