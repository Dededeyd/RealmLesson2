package ph.edu.auf.quizonneldhyde.realmlesson2.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ph.edu.auf.quizonneldhyde.realmlesson2.database.realmmodel.PetModel
import ph.edu.auf.quizonneldhyde.realmlesson2.models.PetType
import ph.edu.auf.quizonneldhyde.realmlesson2.ui.components.DeleteConfirmationDialog
import ph.edu.auf.quizonneldhyde.realmlesson2.ui.components.SearchAndFilterBar
import ph.edu.auf.quizonneldhyde.realmlesson2.ui.components.SortOption
import ph.edu.auf.quizonneldhyde.realmlesson2.ui.components.StatsDashboard
import ph.edu.auf.quizonneldhyde.realmlesson2.viewmodels.OwnerViewModel
import ph.edu.auf.quizonneldhyde.realmlesson2.viewmodels.PetViewModel
// --- ADDED DIALOG IMPORTS ---
import ph.edu.auf.quizonneldhyde.realmlesson2.ui.components.AddPetDialog
import ph.edu.auf.quizonneldhyde.realmlesson2.ui.components.AdoptPetDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetScreen(
    petViewModel: PetViewModel = viewModel(),
    ownerViewModel: OwnerViewModel = viewModel()
) {
    val pets by petViewModel.pets.collectAsState()
    val owners by ownerViewModel.owners.collectAsState()
    val isLoading by petViewModel.isLoading.collectAsState()
    val error by petViewModel.error.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var showAdoptDialog by remember { mutableStateOf<PetModel?>(null) }
    var petToDelete by remember { mutableStateOf<PetModel?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedSort by remember { mutableStateOf(SortOption.NAME_ASC) }

    // Filter and sort pets
    // --- MODIFIED HERE: Added 'breed' to filter ---
    val filteredAndSortedPets = remember(pets, searchQuery, selectedSort) {
        pets.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.petType.contains(searchQuery, ignoreCase = true) ||
                    it.breed.contains(searchQuery, ignoreCase = true) // <-- THIS IS THE FIX
        }.let { filtered ->
            when (selectedSort) {
                SortOption.NAME_ASC -> filtered.sortedBy { it.name.lowercase() }
                SortOption.NAME_DESC -> filtered.sortedByDescending { it.name.lowercase() }
                SortOption.AGE_ASC -> filtered.sortedBy { it.age }
                SortOption.AGE_DESC -> filtered.sortedByDescending { it.age }
            }
        }
    }

    // Show error messages
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            petViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Pet List",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Pet",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        shape = RoundedCornerShape(12.dp),
                        containerColor = MaterialTheme.colorScheme.inverseSurface,
                        contentColor = MaterialTheme.colorScheme.inverseOnSurface
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Stats Dashboard
                val petsWithOwners = pets.count { pet ->
                    owners.any { owner -> owner.pets.any { it.id == pet.id } }
                }
                StatsDashboard(
                    totalPets = pets.size,
                    petsWithOwners = petsWithOwners,
                    totalOwners = owners.size,
                    ownersWithPets = owners.count { it.pets.isNotEmpty() },
                    modifier = Modifier.padding(16.dp)
                )

                // Search and Filter
                SearchAndFilterBar(
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    selectedSort = selectedSort,
                    onSortChange = { selectedSort = it },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else if (filteredAndSortedPets.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyState(
                            icon = Icons.Default.Favorite,
                            title = if (pets.isEmpty()) "No pets yet" else "No pets found",
                            subtitle = if (pets.isEmpty()) "Tap + to add a pet" else "Try a different search"
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredAndSortedPets, key = { it.id }) { pet ->
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { dismissValue ->
                                    when (dismissValue) {
                                        SwipeToDismissBoxValue.StartToEnd -> {
                                            val hasOwner = owners.any { owner -> owner.pets.any { it.id == pet.id } }
                                            if (!hasOwner) {
                                                showAdoptDialog = pet
                                            } else {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("${pet.name} already has an owner!")
                                                }
                                            }
                                            false
                                        }
                                        SwipeToDismissBoxValue.EndToStart -> {
                                            petToDelete = pet
                                            false
                                        }
                                        else -> false
                                    }
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                backgroundContent = {
                                    val color by animateColorAsState(
                                        when (dismissState.targetValue) {
                                            SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.tertiary
                                            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
                                            else -> Color.Transparent
                                        },
                                        label = "background color"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(color, RoundedCornerShape(20.dp))
                                            .padding(horizontal = 24.dp),
                                        contentAlignment = when (dismissState.targetValue) {
                                            SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                            SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                            else -> Alignment.Center
                                        }
                                    ) {
                                        when (dismissState.targetValue) {
                                            SwipeToDismissBoxValue.StartToEnd -> {
                                                Text(
                                                    "Adopt",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                            }
                                            SwipeToDismissBoxValue.EndToStart -> {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                            else -> {}
                                        }
                                    }
                                },
                                content = {
                                    PetCard(pet = pet, owners = owners)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    petToDelete?.let { pet ->
        DeleteConfirmationDialog(
            title = "Delete Pet?",
            message = "Are you sure you want to delete ${pet.name}?",
            onConfirm = { _ ->
                petViewModel.deletePet(pet.id)
                petToDelete = null
            },
            onDismiss = { petToDelete = null }
        )
    }

    if (showAddDialog) {
        // --- MODIFIED HERE: Added 'breed' to the lambda functions ---
        AddPetDialog(
            owners = owners,
            onDismiss = { showAddDialog = false },
            onAddPetOnly = { name, type, breed, age -> // <-- THIS IS THE FIX
                petViewModel.addPet(name, type, breed, age) // <-- THIS IS THE FIX
                showAddDialog = false
            },
            onAddPetWithExistingOwner = { name, type, breed, age, ownerId -> // <-- THIS IS THE FIX
                petViewModel.addPet(name, type, breed, age, ownerId) // <-- THIS IS THE FIX
                showAddDialog = false
            },
            onAddPetWithNewOwner = { petName, petType, petBreed, petAge, ownerName, ownerAge -> // <-- THIS IS THE FIX
                petViewModel.addPetWithNewOwner(petName, petType, petBreed, petAge, ownerName, ownerAge) // <-- THIS IS THE FIX
                showAddDialog = false
            }
        )
    }

    showAdoptDialog?.let { pet ->
        AdoptPetDialog(
            pet = pet,
            owners = owners,
            onDismiss = { showAdoptDialog = null },
            onAdoptToExisting = { ownerId ->
                petViewModel.adoptPet(pet.id, ownerId)
                showAdoptDialog = null
            },
            onAdoptToNew = { ownerName, ownerAge ->
                petViewModel.adoptPetToNewOwner(pet.id, ownerName, ownerAge)
                showAdoptDialog = null
            }
        )
    }
}

@Composable
fun PetCard(pet: PetModel, owners: List<ph.edu.auf.quizonneldhyde.realmlesson2.database.realmmodel.OwnerModel>) {
    val owner = owners.firstOrNull { it.pets.any { p -> p.id == pet.id } }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pet.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))

                // --- MODIFIED HERE: This will now work without crashing ---
                if (pet.breed.isNotBlank()) {
                    Text(
                        text = pet.breed, // <-- THIS IS THE FIX
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Text(
                    text = "${pet.petType} • ${pet.age} years old",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (owner != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Owner: ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = owner.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Text(
                            text = "Available for adoption",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Pet Type Icon on the right
            Surface(
                modifier = Modifier.size(72.dp),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = PetType.getEmoji(pet.petType), // <-- THIS IS THE FIX (added PetType.)
                        fontSize = 40.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "empty_alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha * 0.5f),
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .padding(20.dp),
                tint = MaterialTheme.colorScheme.outline.copy(alpha = alpha)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.outline.copy(alpha = alpha)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline.copy(alpha = alpha * 0.8f)
        )
    }
}