package com.sarftec.simplenotes.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.sarftec.simplenotes.model.Meta

@Dao
interface MetaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metadata: Meta) : Long

    @Update
    suspend fun update(meta: Meta)
}