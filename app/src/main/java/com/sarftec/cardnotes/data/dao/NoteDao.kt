package com.sarftec.cardnotes.data.dao

import androidx.room.*
import com.sarftec.cardnotes.entity.MetaNote
import com.sarftec.cardnotes.model.NOTE_TABLE
import com.sarftec.cardnotes.model.Note

@Dao
interface NoteDao {

    @Insert
    suspend fun insert(note: Note) : Long

    @Query("delete from $NOTE_TABLE where id = :id")
    suspend fun delete(id: Int)

    @Query("delete from $NOTE_TABLE where id in (:ids)")
    suspend fun delete(ids: List<Int>)

    @Update
    suspend fun update(note: Note)

    @Transaction
    @Query("select * from $NOTE_TABLE")
    suspend fun allNotesAndMeta() : List<MetaNote>
}