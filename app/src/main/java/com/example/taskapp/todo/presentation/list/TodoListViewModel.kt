package com.example.taskapp.todo.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskapp.core.domain.onSuccess
import com.example.taskapp.todo.domain.TodoLocalDataSource
import com.example.taskapp.todo.presentation.TodoUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TodoListState(
    val todos: List<TodoUi> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface TodoListAction {
    data object OnAddTodoClick : TodoListAction
    data class OnTodoClick(val todoId: Int) : TodoListAction
    data class OnToggleDone(val todoId: Int) : TodoListAction
    data class OnDeleteTodo(val todoId: Int) : TodoListAction
}

sealed interface TodoListEvent {
    data class NavigateToDetail(val todoId: Int) : TodoListEvent
}

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val dataSource: TodoLocalDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(TodoListState())
    val state = _state.asStateFlow()

    private val _events = Channel<TodoListEvent>()
    val events = _events.receiveAsFlow()

    init {
        dataSource.getTodos()
            .onEach { todos ->
                _state.update {
                    it.copy(
                        todos = todos.map { todo -> TodoUi(id = todo.id, title = todo.title, isDone = todo.isDone) },
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: TodoListAction) {
        when (action) {
            is TodoListAction.OnAddTodoClick -> viewModelScope.launch {
                _events.send(TodoListEvent.NavigateToDetail(-1))
            }
            is TodoListAction.OnTodoClick -> viewModelScope.launch {
                _events.send(TodoListEvent.NavigateToDetail(action.todoId))
            }
            is TodoListAction.OnToggleDone -> toggleDone(action.todoId)
            is TodoListAction.OnDeleteTodo -> viewModelScope.launch {
                dataSource.deleteTodo(action.todoId)
            }
        }
    }

    private fun toggleDone(todoId: Int) {
        viewModelScope.launch {
            dataSource.getTodoById(todoId).onSuccess { todo ->
                dataSource.upsertTodo(todo.copy(isDone = !todo.isDone))
            }
        }
    }
}
