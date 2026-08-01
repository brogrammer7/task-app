package com.example.taskapp.todo.domain

data class Todo(
    val id: Int,
    val title: String,
    val description: String,
    val isDone: Boolean
)