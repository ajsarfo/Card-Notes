package com.sarftec.simplenotes.data

import com.sarftec.simplenotes.data.repository.NoteRepository
import com.sarftec.simplenotes.data.repository.TodoNotifyRepository
import com.sarftec.simplenotes.data.repository.TodoRepository
import com.sarftec.simplenotes.data.repositoryimpl.DiskNoteRepository
import com.sarftec.simplenotes.data.repositoryimpl.DiskNotifyRepository
import com.sarftec.simplenotes.data.repositoryimpl.DiskTodoRepository
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