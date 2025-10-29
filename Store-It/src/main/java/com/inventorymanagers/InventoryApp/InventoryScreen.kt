package com.inventorymanagers.InventoryApp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector

data class InventoryItem(
    val name: String,
    val price: String,
    val quantity: Int,
    val inStock: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen() {
    val searchQuery = remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    val itemList = remember {
        listOf(
            InventoryItem("Item1", "$13.50", 30, true),
            InventoryItem("Item2", "$2.00", 20, true),
            InventoryItem("Item3", "$100.00", 5, true),
            InventoryItem("Item4", "$10.00", 100, true)
        )
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFFF3EEDF),
                tonalElevation = 4.dp
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    this@BottomAppBar.BottomBarButton(Icons.Default.Menu, "Menu", onClick = { showMenu = true })
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(Color(0xFFE6E1D1))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sync") },
                            onClick = { showMenu = false },
                            leadingIcon = { Icon(Icons.Default.Add, "Sync") })
                        DropdownMenuItem(
                            text = { Text("Export") },
                            onClick = { showMenu = false },
                            leadingIcon = { Icon(Icons.Default.Add, "Export") })
                        DropdownMenuItem(
                            text = { Text("Reports") },
                            onClick = { showMenu = false },
                            leadingIcon = { Icon(Icons.Default.Add, "Reports") })
                        DropdownMenuItem(
                            text = { Text("Dark Mode") },
                            onClick = { showMenu = false },
                            leadingIcon = { Icon(Icons.Default.Add, "Dark Mode") })
                    }
                }
                BottomBarButton(Icons.Default.Edit, "Edit Item")
                BottomBarButton(Icons.Default.Add, "Add Item")
                BottomBarButton(Icons.Default.Delete, "Delete Item")
                BottomBarButton(Icons.Default.Add, "Sync")
            }
        },
        containerColor = Color(0xFFF5F2E9)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F2E9))
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "New Inventory",
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Search bar
            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = { searchQuery.value = it },
                placeholder = { Text("Item Name / SKU Id") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F6F1))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("List item", fontWeight = FontWeight.SemiBold)
                Text("Price", fontWeight = FontWeight.SemiBold)
                Text("Qt.", fontWeight = FontWeight.SemiBold)
                Text("Status", fontWeight = FontWeight.SemiBold)
            }

            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            // Item list
            LazyColumn {
                items(itemList) { item ->
                    InventoryItemRow(item)
                    Divider(color = Color.LightGray.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun InventoryItemRow(item: InventoryItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Item thumbnail",
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFE8E4D9))
                .padding(8.dp),
            tint = Color.Gray
        )

        Text(item.name, modifier = Modifier.weight(1f).padding(start = 8.dp))
        Text(item.price, modifier = Modifier.weight(1f))
        Text(item.quantity.toString(), modifier = Modifier.weight(0.5f))
        if (item.inStock) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "In stock",
                tint = Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
fun RowScope.BottomBarButton(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = Color.Black)
        Text(label, fontSize = 11.sp)
    }
}
