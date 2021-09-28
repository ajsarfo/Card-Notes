package com.sarftec.simplenotes.presentation.viewmodel

import android.os.Parcelable
import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sarftec.simplenotes.entity.MetaNote
import com.sarftec.simplenotes.presentation.message.note.MessageBox
import com.sarftec.simplenotes.presentation.message.note.request.NoteRequest
import com.sarftec.simplenotes.presentation.message.note.response.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

class NoteState(
    val note: MetaNote,
    var isSavable: Boolean = false
) {
    var id = -1

    override fun equals(other: Any?): Boolean {
        if (other !is NoteState) return false
        return other.id == this.id
    }

    fun switch() {
        id = if (id == -1) 0 else -1
    }

    override fun hashCode(): Int {
        var result = note.hashCode()
        result = 31 * result + isSavable.hashCode()
        result = 31 * result + id
        return result
    }
}

@HiltViewModel
class NoteEditViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle
) : ViewModel() {

    private val _noteState = MutableLiveData<NoteState>()
    val noteState: LiveData<NoteState>
        get() = _noteState

    fun getToolbarTitle(): String? {
        return stateHandle.get<Parcelable>(PARCEL)
            .takeIf { it is MessageBox }
            ?.let { it as MessageBox }
            ?.let {
                if (it.type == MessageBox.NEW) "New Notes" else "Update Note"
            }
    }

    fun getState(): NoteState? = _noteState.value

    fun setParcel(parcel: Parcelable) {
        if (parcel !is MessageBox) return
        _noteState.value = NoteState(parcel.note)
        stateHandle.set(PARCEL, parcel)
    }

    fun onTitleChanged(editable: Editable) {
        val state = _noteState.value ?: return
        state.note.note.title = editable.toString()
        state.isSavable = editable.isNotEmpty() && state.note.note.content.isNotEmpty()
        state.switch()
        _noteState.value = state
    }

    fun onContentChanged(editable: Editable) {
        val state = _noteState.value ?: return
        state.note.note.content = editable.toString()
        state.isSavable = editable.isNotEmpty() && state.note.note.content.isNotEmpty()
        state.switch()
        _noteState.value = state
    }

    fun setColor(color: Int) {
        val state = _noteState.value ?: return
        state.note.meta.noteColor = color
        state.switch()
        _noteState.value = state
        stateHandle.set(SAVED, true)
    }

    fun save() {
        val request = stateHandle.get<Parcelable>(PARCEL)
            .takeIf { it is NoteRequest }
            ?.let { it as NoteRequest }
            ?: return
        val state = _noteState.value ?: return
        request.note.note.apply {
            title = state.note.note.title
            content = state.note.note.content
        }
        request.note.meta.noteColor = state.note.meta.noteColor
        stateHandle.set(PARCEL, request)
        stateHandle.set(SAVED, true)
    }

    fun getResponse(): Response? {
        val state = _noteState.value ?: return null
        val request = stateHandle.get<Parcelable>(PARCEL)
            ?.takeIf { it is NoteRequest }
            ?.let { it as NoteRequest }
            ?: return null
        if (
            request.type == MessageBox.NEW &&
            (state.note.note.title.isEmpty() && state.note.note.content.isEmpty())
        ) return null
        return request.toResponse(
            if (stateHandle.get<Boolean>(SAVED) == true) Response.OK else Response.ERROR
        )
    }

    companion object {
        const val PARCEL = "parcel"
        const val SAVED = "saved"
    }
}