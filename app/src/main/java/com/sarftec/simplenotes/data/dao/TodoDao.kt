package com.sarftec.simplenotes.data.dao

import androidx.room.*
import com.sarftec.simplenotes.model.TODO_TABLE
import com.sarftec.simplenotes.model.Todo

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(todo: Todo) : Long

    @Query("delete from $TODO_TABLE where id = :id")
    suspend fun delete(id: Int)

    @Delete
    suspend fun delete(todos: List<Todo>)

    @Update
    suspend fun update(todo: Todo)

    @Query("select * from $TODO_TABLE")
    suspend fun todos() : List<Todo>

    @Query("select * from $TODO_TABLE where id = :id limit 1")
    suspend fun find(id: Int) : Todo?
}