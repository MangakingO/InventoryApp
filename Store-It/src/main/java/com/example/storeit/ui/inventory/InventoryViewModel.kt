package com.example.storeit.ui.inventory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.storeit.data.InventoryRepository
import com.example.storeit.data.model.InventoryItem
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface InventoryEvent {
    data class Message(val text: String) : InventoryEvent
    data class Error(val text: String) : InventoryEvent
}

data class ItemEditorState(
    val id: String? = null,
    val name: String = "",
    val quantity: String = "",
    val description: String = ""
)

data class InventoryUiState(
    val items: List<InventoryItem> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val editorState: ItemEditorState? = null
)

class InventoryViewModel(private val repository: InventoryRepository) : ViewModel() {

    var uiState by mutableStateOf(InventoryUiState())
        private set

    private val eventsChannel = Channel<InventoryEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    private var listenerRegistration: ListenerRegistration? = null
    private var activeUserId: String? = null

    fun startListening(userId: String) {
        if (activeUserId == userId) return
        listenerRegistration?.remove()
        activeUserId = userId
        uiState = uiState.copy(isLoading = true)
        listenerRegistration = repository.listenToItems(
            userId = userId,
            onItems = { items ->
                uiState = uiState.copy(items = items, isLoading = false)
            },
            onError = { error ->
                uiState = uiState.copy(isLoading = false)
                emitEvent(InventoryEvent.Error(error.localizedMessage ?: "Unable to load inventory"))
            }
        )
    }

    fun stopListening() {
        listenerRegistration?.remove()
        listenerRegistration = null
        activeUserId = null
        uiState = InventoryUiState()
    }

    private fun emitEvent(event: InventoryEvent) {
        viewModelScope.launch { eventsChannel.send(event) }
    }

    fun openNewItem() {
        uiState = uiState.copy(
            editorState = ItemEditorState(),
            errorMessage = null
        )
    }

    fun editItem(item: InventoryItem) {
        uiState = uiState.copy(
            editorState = ItemEditorState(
                id = item.id,
                name = item.name,
                quantity = item.quantity.toString(),
                description = item.description
            ),
            errorMessage = null
        )
    }

    fun updateEditorName(value: String) {
        updateEditor { it.copy(name = value) }
    }

    fun updateEditorQuantity(value: String) {
        updateEditor { it.copy(quantity = value) }
    }

    fun updateEditorDescription(value: String) {
        updateEditor { it.copy(description = value) }
    }

    private fun updateEditor(transform: (ItemEditorState) -> ItemEditorState) {
        val editor = uiState.editorState ?: return
        uiState = uiState.copy(editorState = transform(editor), errorMessage = null)
    }

    fun dismissEditor() {
        uiState = uiState.copy(editorState = null, errorMessage = null)
    }

    fun saveEditor(userId: String) {
        val editor = uiState.editorState ?: return
        val quantity = editor.quantity.toIntOrNull()
        if (editor.name.isBlank()) {
            uiState = uiState.copy(errorMessage = "Item name is required")
            return
        }
        if (quantity == null) {
            uiState = uiState.copy(errorMessage = "Quantity must be a number")
            return
        }

        uiState = uiState.copy(isSaving = true, errorMessage = null)
        viewModelScope.launch {
            try {
                repository.upsertItem(
                    userId = userId,
                    item = InventoryItem(
                        id = editor.id.orEmpty(),
                        name = editor.name.trim(),
                        quantity = quantity,
                        description = editor.description.trim()
                    )
                )
                emitEvent(InventoryEvent.Message("Item saved"))
                uiState = uiState.copy(isSaving = false, editorState = null)
            } catch (error: Exception) {
                uiState = uiState.copy(isSaving = false)
                emitEvent(InventoryEvent.Error(error.localizedMessage ?: "Unable to save item"))
            }
        }
    }

    fun deleteItem(userId: String, item: InventoryItem) {
        viewModelScope.launch {
            try {
                repository.deleteItem(userId, item.id)
                emitEvent(InventoryEvent.Message("Item deleted"))
            } catch (error: Exception) {
                emitEvent(InventoryEvent.Error(error.localizedMessage ?: "Unable to delete item"))
            }
        }
    }

    override fun onCleared() {
        listenerRegistration?.remove()
        super.onCleared()
    }

    class Factory(private val repository: InventoryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return InventoryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
