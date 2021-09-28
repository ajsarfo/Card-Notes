package com.sarftec.simplenotes.data.dao

import androidx.room.*
import com.sarftec.simplenotes.entity.MetaNote
import com.sarftec.simplenotes.model.NOTE_TABLE
import com.sarftec.simplenotes.model.Note

@Dao
interface NoteDao {

    @Insert
    suspend fun insert(note: Note) : Long

    @Query("delete from $NOTE_TABLE where id = :id")
    suspend fun delete(id: Int)

    @Delete
    suspend fun delete(ids: List<Note>)

    @Update
    suspend fun update(note: Note)

    @Transaction
    @Query("select * from $NOTE_TABLE")
    suspend fun allNotesAndMeta() : List<MetaNote>
}