package com.example.taskapp.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.taskapp.todo.presentation.detail.TodoDetailRoot
import com.example.taskapp.todo.presentation.list.TodoListRoot

fun NavGraphBuilder.todoGraph(navController: NavController) {
    composable<TodoListRoute> {
        TodoListRoot(
            onNavigateToDetail = { todoId -> navController.navigate(TodoDetailRoute(todoId)) }
        )
    }
    composable<TodoDetailRoute> {
        TodoDetailRoot(
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
