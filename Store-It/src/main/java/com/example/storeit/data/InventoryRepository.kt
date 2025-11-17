package com.example.storeit.data

import com.example.storeit.data.model.InventoryItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

/**
 * Provides CRUD access to user scoped inventory collections stored in Firestore.
 */
class InventoryRepository(private val firestore: FirebaseFirestore) {

    private fun userItemsCollection(userId: String) =
        firestore.collection("users").document(userId).collection("items")

    fun listenToItems(
        userId: String,
        onItems: (List<InventoryItem>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return userItemsCollection(userId)
            .orderBy("name")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                val items = snapshot?.documents.orEmpty().map { document ->
                    InventoryItem(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        quantity = (document.getLong("quantity") ?: 0L).toInt(),
                        description = document.getString("description") ?: ""
                    )
                }

                onItems(items)
            }
    }

    suspend fun upsertItem(userId: String, item: InventoryItem) {
        val data = mapOf(
            "name" to item.name,
            "quantity" to item.quantity,
            "description" to item.description
        )

        if (item.id.isBlank()) {
            userItemsCollection(userId).add(data).await()
        } else {
            userItemsCollection(userId).document(item.id).set(data).await()
        }
    }

    suspend fun deleteItem(userId: String, itemId: String) {
        if (itemId.isBlank()) return
        userItemsCollection(userId).document(itemId).delete().await()
    }
}
