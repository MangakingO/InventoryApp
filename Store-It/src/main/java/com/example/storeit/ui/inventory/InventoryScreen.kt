package com.example.storeit.ui.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.RemoveCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.storeit.data.model.InventoryItem
import com.example.storeit.ui.components.InventoryEditorDialog

@Composable
fun InventoryScreen(
    state: InventoryUiState,
    snackbarHostState: SnackbarHostState,
    onAddItem: () -> Unit,
    onEditItem: (InventoryItem) -> Unit,
    onDeleteItem: (InventoryItem) -> Unit,
    onDismissEditor: () -> Unit,
    onEditorNameChanged: (String) -> Unit,
    onEditorQuantityChanged: (String) -> Unit,
    onEditorDescriptionChanged: (String) -> Unit,
    onSaveItem: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddItem) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add item")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Store-It Inventory") },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(imageVector = Icons.Outlined.Logout, contentDescription = "Sign out")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.items.isEmpty() -> {
                    EmptyInventoryState(onAddItem = onAddItem, modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    InventoryList(
                        items = state.items,
                        onEditItem = onEditItem,
                        onDeleteItem = onDeleteItem,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    state.editorState?.let { editorState ->
        InventoryEditorDialog(
            title = if (editorState.id == null) "Add item" else "Edit item",
            name = editorState.name,
            quantity = editorState.quantity,
            description = editorState.description,
            isSaving = state.isSaving,
            errorMessage = state.errorMessage,
            onNameChanged = onEditorNameChanged,
            onQuantityChanged = onEditorQuantityChanged,
            onDescriptionChanged = onEditorDescriptionChanged,
            onDismissRequest = onDismissEditor,
            onConfirm = onSaveItem
        )
    }
}

@Composable
private fun EmptyInventoryState(onAddItem: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No items yet",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Tap the add button to create your first inventory item",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        FloatingActionButton(onClick = onAddItem) {
            Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add item")
        }
    }
}

@Composable
private fun InventoryList(
    items: List<InventoryItem>,
    onEditItem: (InventoryItem) -> Unit,
    onDeleteItem: (InventoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.id }) { item ->
            InventoryCard(
                item = item,
                onEdit = { onEditItem(item) },
                onDelete = { onDeleteItem(item) },
            )
        }
    }
}

@Composable
private fun InventoryCard(
    item: InventoryItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Quantity: ${item.quantity}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (item.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            RowActions(onEdit = onEdit, onDelete = onDelete)
        }
    }
}

@Composable
private fun RowActions(onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(onClick = onEdit) {
            Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit item")
        }
        IconButton(onClick = onDelete) {
            Icon(imageVector = Icons.Outlined.RemoveCircle, contentDescription = "Delete item")
        }
    }
}
