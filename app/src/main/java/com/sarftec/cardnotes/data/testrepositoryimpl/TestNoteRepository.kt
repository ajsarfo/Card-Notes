package com.sarftec.cardnotes.data.testrepositoryimpl

import android.content.Context
import androidx.core.content.ContextCompat
import com.sarftec.cardnotes.R
import com.sarftec.cardnotes.data.repository.NoteRepository
import com.sarftec.cardnotes.entity.MetaNote
import com.sarftec.cardnotes.model.Meta
import com.sarftec.cardnotes.model.Note
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TestNoteRepository @Inject constructor(
    @ApplicationContext context: Context
) : NoteRepository {

    private val defaultColor = ContextCompat.getColor(
        context,
        R.color.white
    )

    private val notes = mutableListOf(
        MetaNote(
            Note(
                id = 0,
                title = "Microsoft Office",
                content = "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ab cupiditate deserunt dolorem doloremque eaque"
            ),
            Meta(noteColor = defaultColor)
        ),
        MetaNote(
            Note(
                id = 1,
                title = "Ali Baba Note",
                content = "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ab cupiditate deserunt dolorem doloremque eaque"
            ),
            Meta(noteColor = defaultColor)
        ),
        MetaNote(
            Note(
                id = 2,
                title = "Oracle App",
                content = "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ab cupiditate deserunt dolorem doloremque eaque"
            ),
            Meta(noteColor = defaultColor)
        ),
        MetaNote(
            Note(
                id = 3,
                title = "Google Account",
                content = "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ab cupiditate deserunt dolorem doloremque eaque"
            ),
            Meta(noteColor = defaultColor)
        ),
        MetaNote(
            Note(
                id = 4,
                title = "Yandex Travel",
                content = "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ab cupiditate deserunt dolorem doloremque eaque"
            ),
            Meta(noteColor = defaultColor)
        ),
        MetaNote(
            Note(
                id = 5,
                title = "Uber Journey",
                content = "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ab cupiditate deserunt dolorem doloremque eaque"
            ),
            Meta(noteColor = defaultColor)
        ),
        MetaNote(
            Note(
                id = 6,
                title = "Tesla Electric",
                content = "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ab cupiditate deserunt dolorem doloremque eaque"
            ),
            Meta(noteColor = defaultColor)
        )
    )

    override suspend fun notes(): List<MetaNote> {
        return notes
    }

    override suspend fun create(note: MetaNote) : Pair<Int, Int> {
        notes.add(note)
        return (0 until 100).random() to (0 until 100).random()
    }

    override suspend fun delete(id: Int) {
        notes.removeAll { it.note.id == id }
    }

    override suspend fun delete(ids: List<Int>) {
        notes.removeAll { ids.contains(it.note.id) }
    }
}