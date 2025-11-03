package com.example.storeit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.storeit.data.InventoryRepository
import com.example.storeit.ui.auth.AuthScreen
import com.example.storeit.ui.auth.AuthViewModel
import com.example.storeit.ui.inventory.InventoryEvent
import com.example.storeit.ui.inventory.InventoryScreen
import com.example.storeit.ui.inventory.InventoryViewModel
import com.example.storeit.ui.theme.StoreitTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val repository by lazy { InventoryRepository(Firebase.firestore) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StoreitTheme {
                StoreItApp(auth = auth, repository = repository)
            }
        }
    }
}

private object Destinations {
    const val Auth = "auth"
    const val Inventory = "inventory"
}

@Composable
private fun StoreItApp(auth: FirebaseAuth, repository: InventoryRepository) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory(auth))
    val inventoryViewModel: InventoryViewModel = viewModel(factory = InventoryViewModel.Factory(repository))

    var currentUser by remember { mutableStateOf(auth.currentUser) }

    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            currentUser = user
            authViewModel.refreshCurrentUser(user)
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    LaunchedEffect(Unit) {
        inventoryViewModel.events.collect { event ->
            val message = when (event) {
                is InventoryEvent.Message -> event.text
                is InventoryEvent.Error -> event.text
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(currentUser) {
        val destination = if (currentUser == null) Destinations.Auth else Destinations.Inventory
        val currentRoute = navController.currentDestination?.route
        if (currentRoute != destination) {
            navController.navigate(destination) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
        val user = currentUser
        if (user == null) {
            inventoryViewModel.stopListening()
        } else {
            inventoryViewModel.startListening(user.uid)
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (currentUser == null) Destinations.Auth else Destinations.Inventory
    ) {
        composable(Destinations.Auth) {
            AuthScreen(
                state = authViewModel.uiState,
                onEmailChanged = authViewModel::onEmailChanged,
                onPasswordChanged = authViewModel::onPasswordChanged,
                onConfirmPasswordChanged = authViewModel::onConfirmPasswordChanged,
                onToggleMode = authViewModel::toggleMode,
                onSubmit = authViewModel::submitCredentials
            )
        }

        composable(Destinations.Inventory) {
            val user = currentUser
            if (user != null) {
                InventoryScreen(
                    state = inventoryViewModel.uiState,
                    snackbarHostState = snackbarHostState,
                    onAddItem = inventoryViewModel::openNewItem,
                    onEditItem = inventoryViewModel::editItem,
                    onDeleteItem = { item -> inventoryViewModel.deleteItem(user.uid, item) },
                    onDismissEditor = inventoryViewModel::dismissEditor,
                    onEditorNameChanged = inventoryViewModel::updateEditorName,
                    onEditorQuantityChanged = inventoryViewModel::updateEditorQuantity,
                    onEditorDescriptionChanged = inventoryViewModel::updateEditorDescription,
                    onSaveItem = { inventoryViewModel.saveEditor(user.uid) },
                    onSignOut = { auth.signOut() }
                )
            }
        }
    }
}
