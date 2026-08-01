package com.example.taskapp.todo.data

import com.example.taskapp.todo.domain.Todo

fun TodoEntity.toTodo(): Todo = Todo(id = id, title = title, description = description, isDone = isDone)

fun Todo.toTodoEntity(): TodoEntity = TodoEntity(id = id, title = title, description = description, isDone = isDone)