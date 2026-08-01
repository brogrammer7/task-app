package com.example.taskapp.todo.data

import com.example.taskapp.core.domain.DataError
import com.example.taskapp.core.domain.EmptyResult
import com.example.taskapp.core.domain.Result
import com.example.taskapp.todo.domain.Todo
import com.example.taskapp.todo.domain.TodoLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomTodoDataSource @Inject constructor(
    private val dao: TodoDao
) : TodoLocalDataSource {

    override fun getTodos(): Flow<List<Todo>> =
        dao.getTodos().map { entities -> entities.map { it.toTodo() } }

    override suspend fun getTodoById(id: Int): Result<Todo, DataError.Local> = try {
        val entity = dao.getTodoById(id)
        if (entity != null) Result.Success(entity.toTodo())
        else Result.Error(DataError.Local.NOT_FOUND)
    } catch (e: Exception) {
        Result.Error(DataError.Local.UNKNOWN)
    }

    override suspend fun upsertTodo(todo: Todo): EmptyResult<DataError.Local> = try {
        dao.upsertTodo(todo.toTodoEntity())
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(DataError.Local.UNKNOWN)
    }

    override suspend fun deleteTodo(id: Int): EmptyResult<DataError.Local> = try {
        dao.deleteTodoById(id)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(DataError.Local.UNKNOWN)
    }
}