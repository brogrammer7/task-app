package com.example.taskapp.todo.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskapp.core.domain.onSuccess
import com.example.taskapp.todo.domain.Todo
import com.example.taskapp.todo.domain.TodoLocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TodoDetailState(
    val title: String = "",
    val description: String = "",
    val isDone: Boolean = false,
    val isNewTodo: Boolean = false
)

sealed interface TodoDetailAction {
    data class OnTitleChange(val title: String) : TodoDetailAction
    data class OnDescriptionChange(val description: String) : TodoDetailAction
    data object OnBack : TodoDetailAction
}

sealed interface TodoDetailEvent {
    data object NavigateBack : TodoDetailEvent
}

@HiltViewModel
class TodoDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val dataSource: TodoLocalDataSource
) : ViewModel() {

    private val todoId: Int = savedStateHandle.get<Int>("todoId") ?: -1

    private val _state = MutableStateFlow(
        TodoDetailState(
            title = savedStateHandle["title"] ?: "",
            description = savedStateHandle["description"] ?: "",
            isNewTodo = todoId == -1
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<TodoDetailEvent>()
    val events = _events.receiveAsFlow()

    init {
        if (todoId != -1 && savedStateHandle.get<String>("title") == null) {
            loadTodo()
        }
    }

    private fun loadTodo() {
        viewModelScope.launch {
            dataSource.getTodoById(todoId).onSuccess { todo ->
                savedStateHandle["title"] = todo.title
                savedStateHandle["description"] = todo.description
                _state.update {
                    it.copy(title = todo.title, description = todo.description, isDone = todo.isDone)
                }
            }
        }
    }

    fun onAction(action: TodoDetailAction) {
        when (action) {
            is TodoDetailAction.OnTitleChange -> {
                savedStateHandle["title"] = action.title
                _state.update { it.copy(title = action.title) }
            }
            is TodoDetailAction.OnDescriptionChange -> {
                savedStateHandle["description"] = action.description
                _state.update { it.copy(description = action.description) }
            }
            is TodoDetailAction.OnBack -> saveAndNavigateBack()
        }
    }

    private fun saveAndNavigateBack() {
        viewModelScope.launch {
            val current = _state.value
            if (todoId == -1) {
                if (current.title.isNotBlank()) {
                    dataSource.upsertTodo(
                        Todo(id = 0, title = current.title.trim(), description = current.description.trim(), isDone = false)
                    )
                }
            } else {
                dataSource.upsertTodo(
                    Todo(id = todoId, title = current.title.trim(), description = current.description.trim(), isDone = current.isDone)
                )
            }
            _events.send(TodoDetailEvent.NavigateBack)
        }
    }
}
