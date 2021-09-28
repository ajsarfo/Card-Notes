package com.sarftec.simplenotes.presentation.viewmodel

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sarftec.simplenotes.presentation.notification.note.MainToNote
import com.sarftec.simplenotes.presentation.notification.todo.MainToTodo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(

) : ViewModel() {

    private val _parcel = MutableLiveData<Parcelable>()
    val parcel: LiveData<Parcelable>
    get() = _parcel

    private val _noteNotification = MutableLiveData<MainToNote>()
    val noteNotification: LiveData<MainToNote>
    get() = _noteNotification

    private val _todoNotification = MutableLiveData<MainToTodo>()
    val todoNotification: LiveData<MainToTodo>
    get() = _todoNotification


    fun setResultParcel(parcel: Parcelable) {
        _parcel.value = parcel
    }

    fun setTodoNotification(notification: MainToTodo) {
        _todoNotification.value = notification
    }

    fun setNoteNotification(notification: MainToNote) {
        _noteNotification.value = notification
    }
}