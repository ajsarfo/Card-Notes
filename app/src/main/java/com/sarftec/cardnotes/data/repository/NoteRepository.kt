package com.sarftec.cardnotes.data.repository

import com.sarftec.cardnotes.entity.MetaNote

interface NoteRepository {
    suspend fun notes() : List<MetaNote>
    suspend fun create(note: MetaNote) : Pair<Int, Int>
    suspend fun update(note: MetaNote)
    suspend fun delete(id: Int)
    suspend fun delete(ids: List<Int>)
}