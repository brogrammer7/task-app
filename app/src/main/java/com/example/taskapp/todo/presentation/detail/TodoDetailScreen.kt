package com.example.taskapp.todo.presentation.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskapp.core.presentation.HudHeader
import com.example.taskapp.core.presentation.ObserveAsEvents
import com.example.taskapp.ui.theme.TaskAppTheme

@Composable
fun TodoDetailRoot(
    onNavigateBack: () -> Unit,
    viewModel: TodoDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            TodoDetailEvent.NavigateBack -> onNavigateBack()
        }
    }

    BackHandler { viewModel.onAction(TodoDetailAction.OnBack) }

    TodoDetailScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun TodoDetailScreen(
    state: TodoDetailState,
    onAction: (TodoDetailAction) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HudHeader(
                title = if (state.isNewTodo) "New Task" else "Edit Task",
                leading = {
                    IconButton(onClick = { onAction(TodoDetailAction.OnBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Save and go back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            FieldLabel("Title")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.title,
                onValueChange = { onAction(TodoDetailAction.OnTitleChange(it)) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = true,
                shape = RectangleShape,
                colors = hudFieldColors()
            )

            Spacer(Modifier.height(28.dp))

            FieldLabel("Description")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.description,
                onValueChange = { onAction(TodoDetailAction.OnDescriptionChange(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = MaterialTheme.typography.bodyLarge,
                maxLines = Int.MAX_VALUE,
                shape = RectangleShape,
                colors = hudFieldColors()
            )
        }
    }
}

/**
 * Labels sit above their field rather than floating inside it. Material's floating
 * label animates through the border on focus, which fights the fixed-panel feel —
 * and a static label reads more like an instrument marking anyway.
 */
@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun hudFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface
)

@Preview
@Composable
private fun TodoDetailScreenPreview() {
    TaskAppTheme {
        TodoDetailScreen(
            state = TodoDetailState(
                title = "Buy groceries",
                description = "Milk, eggs, bread",
                isNewTodo = false
            ),
            onAction = {}
        )
    }
}
