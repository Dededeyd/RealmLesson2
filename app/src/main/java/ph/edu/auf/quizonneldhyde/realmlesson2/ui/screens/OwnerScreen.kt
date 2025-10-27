package ph.edu.auf.quizonneldhyde.realmlesson2.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ph.edu.auf.quizonneldhyde.realmlesson2.database.realmmodel.OwnerModel
import ph.edu.auf.quizonneldhyde.realmlesson2.ui.components.DeleteConfirmationDialog
import ph.edu.auf.quizonneldhyde.realmlesson2.ui.components.SearchAndFilterBar
import ph.edu.auf.quizonneldhyde.realmlesson2.ui.components.SortOption
import ph.edu.auf.quizonneldhyde.realmlesson2.models.PetType // <-- Make sure this is imported
import ph.edu.auf.quizonneldhyde.realmlesson2.viewmodels.OwnerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerScreen(ownerViewModel: OwnerViewModel = viewModel()) {
    val owners by ownerViewModel.owners.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var ownerToDelete by remember { mutableStateOf<OwnerModel?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSort by remember { mutableStateOf(SortOption.NAME_ASC) }

    // Filter and sort owners
    val filteredAndSortedOwners = remember(owners, searchQuery, selectedSort) {
        owners.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }.let { filtered ->
            when (selectedSort) {
                SortOption.NAME_ASC -> filtered.sortedBy { it.name.lowercase() }
                SortOption.NAME_DESC -> filtered.sortedByDescending { it.name.lowercase() }
                SortOption.AGE_ASC -> filtered.sortedBy { it.age }
                SortOption.AGE_DESC -> filtered.sortedByDescending { it.age }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Owner List",
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
                    contentDescription = "Add Owner",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Use the shared component
            SearchAndFilterBar(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                selectedSort = selectedSort,
                onSortChange = { selectedSort = it },
                modifier = Modifier.padding(16.dp)
            )

            if (filteredAndSortedOwners.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyOwnerState(
                        hasOwners = owners.isNotEmpty()
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredAndSortedOwners, key = { it.id }) { owner ->
                        OwnerCard(
                            owner = owner,
                            onDelete = { ownerToDelete = owner }
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    ownerToDelete?.let { owner ->
        val hasPets = owner.pets.isNotEmpty()
        DeleteConfirmationDialog(
            title = "Delete Owner?",
            message = "Are you sure you want to delete ${owner.name}?",
            hasRelatedData = hasPets,
            relatedDataMessage = "This owner has ${owner.pets.size} pet(s). You can choose whether to delete them too.",
            onConfirm = { deleteWithPets ->
                if (deleteWithPets) {
                    ownerViewModel.deleteOwnerAndPets(owner.id)
                } else {
                    ownerViewModel.deleteOwnerOnly(owner.id)
                }
                ownerToDelete = null
            },
            onDismiss = { ownerToDelete = null }
        )
    }

    if (showAddDialog) {
        AddOwnerDialog(
            onDismiss = { showAddDialog = false },
            onAddOwnerOnly = { name, age ->
                ownerViewModel.addOwner(name, age)
                showAddDialog = false
            },
            onAddOwnerWithPet = { name, age, petName, petType, petBreed, petAge ->
                ownerViewModel.addOwnerWithPet(name, age, petName, petType, petBreed, petAge)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun OwnerCard(owner: OwnerModel, onDelete: () -> Unit) {
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
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = owner.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${owner.age} years old",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Surface(
                    onClick = onDelete,
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Owner",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            if (owner.pets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "${owner.pets.size}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = "Pets",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                owner.pets.forEach { pet ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "• ${pet.name}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (pet.breed.isNotBlank()) {
                                    "${pet.breed}, ${pet.age} yrs"
                                } else {
                                    "${pet.petType}, ${pet.age} yrs"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "No pets yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyOwnerState(hasOwners: Boolean) {
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
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .padding(20.dp),
                tint = MaterialTheme.colorScheme.outline.copy(alpha = alpha)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            if (hasOwners) "No owners found" else "No owners yet",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.outline.copy(alpha = alpha)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            if (hasOwners) "Try a different search" else "Tap + to add an owner",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline.copy(alpha = alpha * 0.8f)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOwnerDialog(
    onDismiss: () -> Unit,
    onAddOwnerOnly: (String, Int) -> Unit,
    onAddOwnerWithPet: (String, Int, String, String, String, Int) -> Unit
) {
    var selectedMode by remember { mutableStateOf(OwnerAddMode.SelectMode) }
    var ownerName by remember { mutableStateOf("") }
    var ownerAge by remember { mutableStateOf("") }
    var petName by remember { mutableStateOf("") }
    // --- FIX: Changed from String to PetType? ---
    var selectedPetType by remember { mutableStateOf<PetType?>(null) }
    var petBreed by remember { mutableStateOf("") }
    var petAge by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                when (selectedMode) {
                    OwnerAddMode.SelectMode -> "Add New Owner"
                    OwnerAddMode.OwnerOnly -> "Add Owner Without Pet"
                    OwnerAddMode.OwnerWithPet -> "Add Owner With Pet"
                },
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                when (selectedMode) {
                    OwnerAddMode.SelectMode -> {
                        Text(
                            "How would you like to add the owner?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { selectedMode = OwnerAddMode.OwnerOnly },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text(
                                "Owner Only",
                                modifier = Modifier.padding(vertical = 8.dp),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { selectedMode = OwnerAddMode.OwnerWithPet },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text(
                                "Owner With Pet",
                                modifier = Modifier.padding(vertical = 8.dp),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    OwnerAddMode.OwnerOnly -> {
                        Text(
                            "Owner Details",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = ownerName,
                            onValueChange = { ownerName = it },
                            label = { Text("Owner Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = ownerAge,
                            onValueChange = { ownerAge = it },
                            label = { Text("Owner Age") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    OwnerAddMode.OwnerWithPet -> {
                        Text(
                            "Owner Details",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = ownerName,
                            onValueChange = { ownerName = it },
                            label = { Text("Owner Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = ownerAge,
                            onValueChange = { ownerAge = it },
                            label = { Text("Owner Age") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            "Pet Details",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = petName,
                            onValueChange = { petName = it },
                            label = { Text("Pet Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // --- FIX: Replaced TextField with Dropdown ---
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedPetType?.displayName ?: "Select Pet Type",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Pet Type") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                PetType.entries.forEach { petType ->
                                    DropdownMenuItem(
                                        text = { Text(petType.displayName) },
                                        leadingIcon = { Icon(petType.icon, null) },
                                        onClick = {
                                            selectedPetType = petType
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                        // --- END OF FIX ---

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = petBreed,
                            onValueChange = { petBreed = it },
                            label = { Text("Pet Breed (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = petAge,
                            onValueChange = { petAge = it },
                            label = { Text("Pet Age") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (selectedMode != OwnerAddMode.SelectMode) {
                Button(
                    onClick = {
                        // --- FIX: Get name from selectedPetType ---
                        val petTypeName = selectedPetType?.displayName ?: ""
                        when (selectedMode) {
                            OwnerAddMode.OwnerOnly -> {
                                if (ownerName.isNotBlank() && ownerAge.toIntOrNull() != null) {
                                    onAddOwnerOnly(ownerName, ownerAge.toInt())
                                }
                            }
                            OwnerAddMode.OwnerWithPet -> {
                                if (ownerName.isNotBlank() && ownerAge.toIntOrNull() != null &&
                                    petName.isNotBlank() && petTypeName.isNotBlank() && petAge.toIntOrNull() != null
                                ) {
                                    onAddOwnerWithPet(
                                        ownerName,
                                        ownerAge.toInt(),
                                        petName,
                                        petTypeName, // Use the display name
                                        petBreed,
                                        petAge.toInt()
                                    )
                                }
                            }
                            else -> {}
                        }
                    },
                    // --- FIX: Update validation logic ---
                    enabled = when (selectedMode) {
                        OwnerAddMode.OwnerOnly -> ownerName.isNotBlank() && ownerAge.toIntOrNull() != null
                        OwnerAddMode.OwnerWithPet -> ownerName.isNotBlank() && ownerAge.toIntOrNull() != null &&
                                petName.isNotBlank() && selectedPetType != null && petAge.toIntOrNull() != null
                        else -> false
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    if (selectedMode == OwnerAddMode.SelectMode) {
                        onDismiss()
                    } else {
                        selectedMode = OwnerAddMode.SelectMode
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (selectedMode == OwnerAddMode.SelectMode) "Cancel" else "Back")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

enum class OwnerAddMode {
    SelectMode,
    OwnerOnly,
    OwnerWithPet
}