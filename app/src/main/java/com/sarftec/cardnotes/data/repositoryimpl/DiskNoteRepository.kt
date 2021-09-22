package com.sarftec.cardnotes.data.repositoryimpl

import android.content.Context
import androidx.room.withTransaction
import com.sarftec.cardnotes.data.AppDatabase
import com.sarftec.cardnotes.data.repository.NoteRepository
import com.sarftec.cardnotes.entity.MetaNote
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DiskNoteRepository @Inject constructor(
    @ApplicationContext  val context: Context,
    private val appDatabase: AppDatabase
): NoteRepository {

    override suspend fun notes(): List<MetaNote> {
        return appDatabase.noteDao().allNotesAndMeta()
    }

    override suspend fun create(note: MetaNote): Pair<Int, Int> {
        return appDatabase.withTransaction {
            val noteId = appDatabase.noteDao().insert(note.note).toInt()
            val metaId = appDatabase.metaDao().insert(
                note.meta.also { it.noteId = noteId }
            )
            noteId to metaId.toInt()
        }
    }

    override suspend fun delete(id: Int) {
        appDatabase.noteDao().delete(id)
    }

    override suspend fun delete(ids: List<Int>) {
      appDatabase.noteDao().delete(ids)
    }
}