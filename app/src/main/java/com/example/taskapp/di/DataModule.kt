package com.example.taskapp.di

import com.example.taskapp.todo.data.RoomTodoDataSource
import com.example.taskapp.todo.domain.TodoLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindTodoLocalDataSource(impl: RoomTodoDataSource): TodoLocalDataSource
}
