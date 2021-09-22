package com.sarftec.cardnotes.data

import com.sarftec.cardnotes.data.repository.NoteRepository
import com.sarftec.cardnotes.data.repository.TodoNotifyRepository
import com.sarftec.cardnotes.data.repository.TodoRepository
import com.sarftec.cardnotes.data.repositoryimpl.DiskNoteRepository
import com.sarftec.cardnotes.data.repositoryimpl.DiskNotifyRepository
import com.sarftec.cardnotes.data.repositoryimpl.DiskTodoRepository
import com.sarftec.cardnotes.data.testrepositoryimpl.TestNoteRepository
import com.sarftec.cardnotes.data.testrepositoryimpl.TestTodoNotifyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoInjection {

    @Binds
    abstract fun noteRepository(repository: DiskNoteRepository) : NoteRepository

    @Singleton
    @Binds
    abstract fun todoRepository(repository: DiskTodoRepository) : TodoRepository

    @Singleton
    @Binds
    abstract fun todoNotifyRepository(repository: DiskNotifyRepository) : TodoNotifyRepository
}