package com.sarftec.simplenotes.data.repository

import com.sarftec.simplenotes.entity.MetaNote

interface NoteRepository {
    suspend fun notes() : List<MetaNote>
    suspend fun create(note: MetaNote) : Pair<Int, Int>
    suspend fun update(note: MetaNote)
    suspend fun delete(id: Int)
    suspend fun delete(ids: List<MetaNote>)
}