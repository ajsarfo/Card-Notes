package com.sarftec.simplenotes.data.repositoryimpl

import android.content.Context
import androidx.room.withTransaction
import com.sarftec.simplenotes.data.AppDatabase
import com.sarftec.simplenotes.data.repository.NoteRepository
import com.sarftec.simplenotes.entity.MetaNote
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

    override suspend fun update(note: MetaNote) {
        appDatabase.withTransaction {
            appDatabase.noteDao().update(note.note)
            appDatabase.metaDao().update(note.meta)
        }
    }

    override suspend fun delete(id: Int) {
        appDatabase.noteDao().delete(id)
    }

    override suspend fun delete(notes: List<MetaNote>) {
      appDatabase.noteDao().delete(
          notes.map { it.note }
      )
    }
}