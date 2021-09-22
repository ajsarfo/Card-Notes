package com.sarftec.cardnotes.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.sarftec.cardnotes.model.Meta

@Dao
interface MetaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metadata: Meta) : Long

    @Update
    suspend fun update(meta: Meta)
}