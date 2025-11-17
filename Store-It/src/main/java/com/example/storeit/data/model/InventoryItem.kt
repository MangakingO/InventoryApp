package com.example.storeit.data.model

/**
 * Represents a single inventory entry that is stored in Firestore.
 */
data class InventoryItem(
    val id: String = "",
    val name: String = "",
    val quantity: Int = 0,
    val description: String = ""
)
