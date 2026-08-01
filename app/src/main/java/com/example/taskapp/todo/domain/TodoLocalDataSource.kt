package com.example.taskapp.todo.domain

import com.example.taskapp.core.domain.DataError
import com.example.taskapp.core.domain.EmptyResult
import com.example.taskapp.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface TodoLocalDataSource {
    fun getTodos(): Flow<List<Todo>>
    suspend fun getTodoById(id: Int): Result<Todo, DataError.Local>
    suspend fun upsertTodo(todo: Todo): EmptyResult<DataError.Local>
    suspend fun deleteTodo(id: Int): EmptyResult<DataError.Local>
}