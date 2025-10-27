package ph.edu.auf.quizonneldhyde.realmlesson2.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ph.edu.auf.quizonneldhyde.realmlesson2.database.realmmodel.OwnerModel
import ph.edu.auf.quizonneldhyde.realmlesson2.database.realmmodel.PetModel
import ph.edu.auf.quizonneldhyde.realmlesson2.models.PetType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetDialog(
    owners: List<OwnerModel>,
    onDismiss: () -> Unit,
    onAddPetOnly: (name: String, type: String, breed: String, age: Int) -> Unit,
    onAddPetWithExistingOwner: (name: String, type: String, breed: String, age: Int, ownerId: String) -> Unit,
    onAddPetWithNewOwner: (petName: String, petType: String, petBreed: String, petAge: Int, ownerName: String, ownerAge: Int) -> Unit
) {
    var selectedMode by remember { mutableStateOf(PetAddMode.SelectMode) }
    var petName by remember { mutableStateOf("") }
    var selectedPetType by remember { mutableStateOf<PetType?>(null) }
    var petBreed by remember { mutableStateOf("") }
    var petAge by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var ownerAge by remember { mutableStateOf("") }
    var selectedOwnerId by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                when (selectedMode) {
                    PetAddMode.SelectMode -> "Add New Pet"
                    PetAddMode.PetOnly -> "Add Pet (Available for Adoption)"
                    PetAddMode.PetWithExistingOwner -> "Add Pet with Existing Owner"
                    PetAddMode.PetWithNewOwner -> "Add Pet with New Owner"
                },
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                when (selectedMode) {
                    PetAddMode.SelectMode -> {
                        Text(
                            "How would you like to add the pet?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        AddModeButton(
                            text = "Pet Only",
                            onClick = { selectedMode = PetAddMode.PetOnly }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        AddModeButton(
                            text = "To Existing Owner",
                            onClick = { selectedMode = PetAddMode.PetWithExistingOwner },
                            enabled = owners.isNotEmpty()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        AddModeButton(
                            text = "With New Owner",
                            onClick = { selectedMode = PetAddMode.PetWithNewOwner }
                        )
                    }

                    PetAddMode.PetOnly,
                    PetAddMode.PetWithExistingOwner,
                    PetAddMode.PetWithNewOwner -> {
                        PetDetailsFields(
                            petName = petName,
                            onPetNameChange = { petName = it },
                            selectedPetType = selectedPetType,
                            onPetTypeSelect = { selectedPetType = it },
                            petBreed = petBreed,
                            onPetBreedChange = { petBreed = it },
                            petAge = petAge,
                            onPetAgeChange = { petAge = it }
                        )

                        when (selectedMode) {
                            PetAddMode.PetWithExistingOwner -> {
                                Spacer(modifier = Modifier.height(20.dp))
                                OwnerSelectionList(
                                    owners = owners,
                                    selectedOwnerId = selectedOwnerId,
                                    onOwnerSelect = { selectedOwnerId = it }
                                )
                            }
                            PetAddMode.PetWithNewOwner -> {
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    "New Owner Details",
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
                            else -> {}
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (selectedMode != PetAddMode.SelectMode) {
                Button(
                    onClick = {
                        val type = selectedPetType?.displayName ?: ""
                        when (selectedMode) {
                            PetAddMode.PetOnly -> onAddPetOnly(petName, type, petBreed, petAge.toInt())
                            PetAddMode.PetWithExistingOwner -> selectedOwnerId?.let { id ->
                                onAddPetWithExistingOwner(petName, type, petBreed, petAge.toInt(), id)
                            }
                            PetAddMode.PetWithNewOwner -> onAddPetWithNewOwner(
                                petName, type, petBreed, petAge.toInt(), ownerName, ownerAge.toInt()
                            )
                            else -> {}
                        }
                    },
                    enabled = validatePetAddInput(selectedMode, petName, selectedPetType, petAge, ownerName, ownerAge, selectedOwnerId),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add Pet")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    if (selectedMode == PetAddMode.SelectMode) {
                        onDismiss()
                    } else {
                        selectedMode = PetAddMode.SelectMode
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (selectedMode == PetAddMode.SelectMode) "Cancel" else "Back")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PetDetailsFields(
    petName: String,
    onPetNameChange: (String) -> Unit,
    selectedPetType: PetType?,
    onPetTypeSelect: (PetType) -> Unit,
    petBreed: String,
    onPetBreedChange: (String) -> Unit,
    petAge: String,
    onPetAgeChange: (String) -> Unit
) {
    Text(
        "Pet Details",
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = petName,
        onValueChange = onPetNameChange,
        label = { Text("Pet Name") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))

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
                        onPetTypeSelect(petType)
                        expanded = false
                    }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = petBreed,
        onValueChange = onPetBreedChange,
        label = { Text("Pet Breed (Optional)") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = petAge,
        onValueChange = onPetAgeChange,
        label = { Text("Pet Age") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun OwnerSelectionList(
    owners: List<OwnerModel>,
    selectedOwnerId: String?,
    onOwnerSelect: (String) -> Unit
) {
    Text(
        "Select Existing Owner",
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(12.dp))
    LazyColumn(
        modifier = Modifier.heightIn(max = 200.dp)
    ) {
        items(owners, key = { it.id }) { owner ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedOwnerId == owner.id,
                    onClick = { onOwnerSelect(owner.id) }
                )
                Text(
                    text = "${owner.name} (${owner.age})",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

private fun validatePetAddInput(
    mode: PetAddMode,
    petName: String,
    selectedPetType: PetType?,
    petAge: String,
    ownerName: String,
    ownerAge: String,
    selectedOwnerId: String?
): Boolean {
    val basePetValid = petName.isNotBlank() && selectedPetType != null && petAge.toIntOrNull() != null
    return when (mode) {
        PetAddMode.PetOnly -> basePetValid
        PetAddMode.PetWithExistingOwner -> basePetValid && selectedOwnerId != null
        PetAddMode.PetWithNewOwner -> basePetValid && ownerName.isNotBlank() && ownerAge.toIntOrNull() != null
        else -> false
    }
}


@Composable
fun AdoptPetDialog(
    pet: PetModel,
    owners: List<OwnerModel>,
    onDismiss: () -> Unit,
    onAdoptToExisting: (ownerId: String) -> Unit,
    onAdoptToNew: (ownerName: String, ownerAge: Int) -> Unit
) {
    var selectedMode by remember { mutableStateOf(AdoptionMode.SelectMode) }
    var ownerName by remember { mutableStateOf("") }
    var ownerAge by remember { mutableStateOf("") }
    var selectedOwnerId by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Adopt ${pet.name}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Pet Type: ${pet.petType}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (pet.breed.isNotBlank()) {
                    Text(
                        "Breed: ${pet.breed}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                when (selectedMode) {
                    AdoptionMode.SelectMode -> {
                        Text(
                            "Who will adopt ${pet.name}?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        AddModeButton(
                            text = "Existing Owner",
                            onClick = { selectedMode = AdoptionMode.ExistingOwner },
                            enabled = owners.isNotEmpty()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        AddModeButton(
                            text = "New Owner",
                            onClick = { selectedMode = AdoptionMode.NewOwner }
                        )
                    }
                    AdoptionMode.ExistingOwner -> {
                        OwnerSelectionList(
                            owners = owners,
                            selectedOwnerId = selectedOwnerId,
                            onOwnerSelect = { selectedOwnerId = it }
                        )
                    }
                    AdoptionMode.NewOwner -> {
                        Text(
                            "New Owner Details",
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
                }
            }
        },
        confirmButton = {
            if (selectedMode != AdoptionMode.SelectMode) {
                Button(
                    onClick = {
                        when (selectedMode) {
                            AdoptionMode.ExistingOwner -> selectedOwnerId?.let { onAdoptToExisting(it) }
                            AdoptionMode.NewOwner -> onAdoptToNew(ownerName, ownerAge.toInt())
                            else -> {}
                        }
                    },
                    enabled = validateAdoptInput(selectedMode, ownerName, ownerAge, selectedOwnerId),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirm Adoption")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    if (selectedMode == AdoptionMode.SelectMode) {
                        onDismiss()
                    } else {
                        selectedMode = AdoptionMode.SelectMode
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (selectedMode == AdoptionMode.SelectMode) "Cancel" else "Back")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun AddModeButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Text(
            text,
            modifier = Modifier.padding(vertical = 8.dp),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}

private fun validateAdoptInput(
    mode: AdoptionMode,
    ownerName: String,
    ownerAge: String,
    selectedOwnerId: String?
): Boolean {
    return when (mode) {
        AdoptionMode.ExistingOwner -> selectedOwnerId != null
        AdoptionMode.NewOwner -> ownerName.isNotBlank() && ownerAge.toIntOrNull() != null
        else -> false
    }
}

enum class PetAddMode {
    SelectMode,
    PetOnly,
    PetWithExistingOwner,
    PetWithNewOwner
}

enum class AdoptionMode {
    SelectMode,
    ExistingOwner,
    NewOwner
}
