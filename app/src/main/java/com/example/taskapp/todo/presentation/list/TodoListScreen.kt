package com.example.taskapp.todo.presentation.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskapp.core.presentation.HudHeader
import com.example.taskapp.core.presentation.ObserveAsEvents
import com.example.taskapp.core.presentation.bloom
import com.example.taskapp.todo.presentation.TodoUi
import com.example.taskapp.ui.theme.BevelShape
import com.example.taskapp.ui.theme.TaskAppTheme

@Composable
fun TodoListRoot(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is TodoListEvent.NavigateToDetail -> onNavigateToDetail(event.todoId)
        }
    }

    TodoListScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun TodoListScreen(
    state: TodoListState,
    onAction: (TodoListAction) -> Unit
) {
    val active = state.todos.count { !it.isDone }
    val total = state.todos.size

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HudHeader(
                title = "Task App",
                // Null rather than an empty lambda, so the header doesn't reserve
                // vertical space for a readout that has nothing to report yet.
                readout = if (!state.isLoading && total > 0) {
                    {
                        Readout(value = active, label = "ACTIVE", accent = true)
                        Text(
                            text = "/",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Readout(value = total, label = "TOTAL", accent = false)
                    }
                } else null
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(TodoListAction.OnAddTodoClick) },
                shape = BevelShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.bloom(MaterialTheme.colorScheme.primary, spread = 14.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        }
    ) { padding ->
        when {
            state.isLoading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }

            state.todos.isEmpty() -> EmptyState(Modifier.fillMaxSize().padding(padding))

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
                // The 1dp gap lets the unlit background show through as a seam
                // between panels, so no explicit divider is needed.
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(state.todos, key = { it.id }) { todo ->
                    SwipeToDeleteItem(onDelete = { onAction(TodoListAction.OnDeleteTodo(todo.id)) }) {
                        TodoListItem(
                            todo = todo,
                            onToggle = { onAction(TodoListAction.OnToggleDone(todo.id)) },
                            onClick = { onAction(TodoListAction.OnTodoClick(todo.id)) }
                        )
                    }
                }
            }
        }
    }
}

/** A single telemetry figure: zero-padded count plus its unit label. */
@Composable
private fun Readout(
    value: Int,
    label: String,
    accent: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.labelLarge,
            color = if (accent) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteItem(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )
    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val armed = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart
            val wellColor by animateColorAsState(
                targetValue = if (armed) MaterialTheme.colorScheme.errorContainer
                              else Color.Transparent,
                label = "delete_well"
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(wellColor)
                    .padding(end = 20.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (armed) {
                    Text(
                        text = "DELETE",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.width(10.dp))
                }
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete task",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    ) {
        content()
    }
}

@Composable
private fun TodoListItem(
    todo: TodoUi,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // The signature element: a lit status edge running the full height of the panel.
    // Pending tasks emit; completed ones go cold and keep only the bare strip.
    val edge by animateColorAsState(
        targetValue = if (todo.isDone) MaterialTheme.colorScheme.onSurfaceVariant
                      else MaterialTheme.colorScheme.primary,
        label = "status_edge"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .drawBehind {
                val barWidth = 3.dp.toPx()
                if (!todo.isDone) {
                    for (i in 3 downTo 1) {
                        drawRect(
                            color = edge.copy(alpha = 0.10f / i),
                            size = Size(barWidth * (1f + i * 1.7f), size.height)
                        )
                    }
                }
                drawRect(color = edge, size = Size(barWidth, size.height))
            }
            .clickable(onClick = onClick)
            .padding(start = 14.dp, end = 16.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusNode(isDone = todo.isDone, onToggle = onToggle)
        Spacer(Modifier.width(6.dp))
        Text(
            text = todo.title,
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = if (todo.isDone) TextDecoration.LineThrough else TextDecoration.None,
            color = if (todo.isDone) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Replaces Material's [androidx.compose.material3.Checkbox], whose rounded fill and
 * ripple belong to a different visual language. Drawn as a hard square outline, with
 * the check struck through it by hand.
 */
@Composable
private fun StatusNode(
    isDone: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint by animateColorAsState(
        targetValue = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant
                      else MaterialTheme.colorScheme.primary,
        label = "status_node"
    )
    Box(
        modifier = modifier
            .size(48.dp) // keeps the touch target at the 48dp minimum
            .toggleable(
                value = isDone,
                onValueChange = { onToggle() },
                role = Role.Checkbox
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(22.dp)) {
            val stroke = 1.5.dp.toPx()
            drawRect(color = tint, style = Stroke(width = stroke))
            if (isDone) {
                val inset = size.width * 0.28f
                val pivotX = size.width * 0.44f
                val pivotY = size.height - inset
                drawLine(
                    color = tint,
                    start = Offset(inset, size.height * 0.52f),
                    end = Offset(pivotX, pivotY),
                    strokeWidth = stroke,
                    cap = StrokeCap.Square
                )
                drawLine(
                    color = tint,
                    start = Offset(pivotX, pivotY),
                    end = Offset(size.width - inset, inset),
                    strokeWidth = stroke,
                    cap = StrokeCap.Square
                )
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            Text(
                text = "NO TASKS YET",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.size(12.dp))
            Text(
                text = "Tap + to add your first task.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
private fun TodoListScreenPreview() {
    TaskAppTheme {
        TodoListScreen(
            state = TodoListState(
                todos = listOf(
                    TodoUi(1, "Buy groceries", false),
                    TodoUi(2, "Walk the dog", true),
                    TodoUi(3, "Call dentist", false)
                ),
                isLoading = false
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun TodoListEmptyPreview() {
    TaskAppTheme {
        TodoListScreen(
            state = TodoListState(todos = emptyList(), isLoading = false),
            onAction = {}
        )
    }
}
