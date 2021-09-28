package com.sarftec.simplenotes.data.dao

import androidx.room.*
import com.sarftec.simplenotes.entity.NOTIFY_TABLE
import com.sarftec.simplenotes.entity.TodoNotify

@Dao
interface NotifyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNotify(notify: TodoNotify)

    @Query("delete from $NOTIFY_TABLE where todoId = :todoId")
    suspend fun removeNotify(todoId: Int)

    @Query("select * from $NOTIFY_TABLE where todoId = :todoId limit 1")
    suspend fun findNotify(todoId: Int) : TodoNotify?

    @Query("select * from $NOTIFY_TABLE")
    suspend fun notifies() : List<TodoNotify>

    @Update
    suspend fun updateNotify(todoNotify: TodoNotify)
}