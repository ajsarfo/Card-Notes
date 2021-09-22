package com.sarftec.cardnotes.presentation.viewmodel

import android.os.Parcelable
import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarftec.cardnotes.data.repository.NoteRepository
import com.sarftec.cardnotes.entity.MetaNote
import com.sarftec.cardnotes.presentation.message.note.MessageBox
import com.sarftec.cardnotes.presentation.message.note.response.NoteResponse
import com.sarftec.cardnotes.presentation.message.note.response.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class Notes(
    var notes: MutableList<MetaNote>
) {
    private var id = -1

    override fun equals(other: Any?): Boolean {
        if (other !is Notes) return false
        return other.id == this.id
    }

    fun switch() {
        id = if (id == -1) 0 else -1
    }
}

sealed class SearchNotification {
    object Notify : SearchNotification() {
        var isVisible = false
        var clearField = false
        var id = -1

        fun switch() {
            id = if (id == -1)  0 else -1
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Notify) return false
            return other.id  == id
        }
    }
    object Neutral : SearchNotification()
}

sealed class Notification {
    class Update(val position: Int) : Notification()
    object Insert : Notification()
    object Neutral : Notification()
}

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private var stored = mutableListOf<MetaNote>()

    private val _notes = MutableLiveData<Notes>()
    val notes: LiveData<Notes>
        get() = _notes

    private val _searchNotification = MutableLiveData<SearchNotification>()
    val searchNotification: LiveData<SearchNotification>
        get() = _searchNotification

    private val _notification = MutableLiveData<Notification>()
    val notification: LiveData<Notification>
        get() = _notification

    fun fetch() {
        viewModelScope.launch {
            stored = repository.notes().toMutableList()
            _notes.value = Notes(stored)
        }
    }

    fun deleteNotes(items: List<MetaNote>) {
        val notes = _notes.value ?: return
        notes.notes.removeAll(items)
        notes.switch()
        _notes.value = notes
    }

    fun setRequestResponse(input: Parcelable) {
        input.takeIf { input is NoteResponse }
            ?.let { it as NoteResponse }
            ?.takeIf { it.code == Response.OK }
            ?.let {
                if (it.type == MessageBox.UPDATE) updateNote(it as NoteResponse.Update)
                else insertNote(it as NoteResponse.New)
            }
    }

    fun neutralizeNotification() {
        _notification.value = Notification.Neutral
    }

    fun neutralizeSearchNotify() {
        _searchNotification.value = SearchNotification.Neutral
    }


    fun onCloseSearchQuery() {
        val notes = _notes.value ?: return
        _searchNotification.value = SearchNotification.Notify.also {
            it.isVisible = false
            it.clearField = true
            it.switch()
        }
        if(notes.notes.size == stored.size) return
        notes.notes = stored
        notes.switch()
        _notes.value = notes
    }

    fun onNoteSearch(query: Editable) {
        val notes = _notes.value ?: return
        if (query.isEmpty()) {
            notes.notes = stored
            _searchNotification.value = SearchNotification.Notify.also {
                it.isVisible = false
                it.clearField = false
                it.switch()
            }
        } else {
            notes.notes = stored.filter {
                it.note.title
                    .lowercase(Locale.ENGLISH)
                    .contains(query.toString().lowercase(Locale.ENGLISH))
            }.toMutableList()
            _searchNotification.value = SearchNotification.Notify.also {
                it.isVisible = true
                it.clearField = false
                it.switch()
            }
        }
        notes.switch()
        _notes.value = notes
    }

    private fun updateNote(response: NoteResponse.Update) {
        val notes = _notes.value ?: return
        stored.firstOrNull { it.note.id == response.note.note.id }
            ?.let { transferContent(response.note, it) }
            ?.let { notes.notes = stored }
            ?.let { _notification.value = Notification.Update(response.position) }
    }

    private fun insertNote(response: NoteResponse.New) {
        val notes = _notes.value ?: return
        viewModelScope.launch {
            val pair = repository.create(response.note)
            response.note.note.id = pair.first
            response.note.meta.id = pair.second
            stored.add(0, response.note)
            notes.notes = stored
            _notification.value = Notification.Insert
        }
    }

    private fun transferContent(from: MetaNote, to: MetaNote) {
        to.note.title = from.note.title
        to.note.content = from.note.content
        to.meta.noteColor = from.meta.noteColor
    }

}