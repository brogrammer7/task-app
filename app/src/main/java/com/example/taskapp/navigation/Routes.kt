package com.example.taskapp.navigation

import kotlinx.serialization.Serializable

@Serializable
data object TodoListRoute

@Serializable
data class TodoDetailRoute(val todoId: Int)
