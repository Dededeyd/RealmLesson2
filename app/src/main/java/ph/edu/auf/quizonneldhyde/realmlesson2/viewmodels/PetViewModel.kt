package ph.edu.auf.quizonneldhyde.realmlesson2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ph.edu.auf.quizonneldhyde.realmlesson2.database.RealmHelper
import ph.edu.auf.quizonneldhyde.realmlesson2.database.realmmodel.OwnerModel
import ph.edu.auf.quizonneldhyde.realmlesson2.database.realmmodel.PetModel

class PetViewModel : ViewModel() {

    private val _pets = MutableStateFlow<List<PetModel>>(emptyList())
    val pets: StateFlow<List<PetModel>> get() = _pets

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    init {
        loadPets()
    }

    private fun loadPets() {
        val realm = RealmHelper.getRealmInstance()
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                realm.query(PetModel::class)
                    .asFlow()
                    .collect { results ->
                        _pets.value = results.list
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = "Failed to load pets: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // --- MODIFIED HERE: Added 'breed' parameter ---
    fun addPet(name: String, type: String, breed: String, age: Int, ownerId: String? = null) {
        if (!validatePetInput(name, type, age)) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val realm = RealmHelper.getRealmInstance()
                realm.write {
                    val newPet = copyToRealm(PetModel().apply {
                        this.name = name.trim()
                        this.petType = type.trim()
                        this.breed = breed.trim() // <-- THIS IS THE FIX
                        this.age = age
                    })
                    if (ownerId != null) {
                        val owner = query(OwnerModel::class, "id == $0", ownerId).first().find()
                        owner?.pets?.add(newPet)
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to add pet: ${e.message}"
            }
        }
    }

    // --- MODIFIED HERE: Added 'petBreed' parameter ---
    fun addPetWithNewOwner(petName: String, petType: String, petBreed: String, petAge: Int, ownerName: String, ownerAge: Int) {
        if (!validatePetInput(petName, petType, petAge) || !validateOwnerInput(ownerName, ownerAge)) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val realm = RealmHelper.getRealmInstance()
                realm.write {
                    val newPet = PetModel().apply {
                        this.name = petName.trim()
                        this.petType = petType.trim()
                        this.breed = petBreed.trim() // <-- THIS IS THE FIX
                        this.age = petAge
                    }
                    copyToRealm(OwnerModel().apply {
                        this.name = ownerName.trim()
                        this.age = ownerAge
                        this.pets.add(newPet)
                    })
                }
            } catch (e: Exception) {
                _error.value = "Failed to add pet with owner: ${e.message}"
            }
        }
    }

    fun deletePet(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val realm = RealmHelper.getRealmInstance()
                realm.write {
                    val pet = query(PetModel::class, "id == $0", id).first().find()
                    pet?.let { delete(it) }
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete pet: ${e.message}"
            }
        }
    }

    fun adoptPet(petId: String, newOwnerId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val realm = RealmHelper.getRealmInstance()
                realm.write {
                    val pet = query(PetModel::class, "id == $0", petId).first().find()
                    val newOwner = query(OwnerModel::class, "id == $0", newOwnerId).first().find()
                    if (pet != null && newOwner != null) {
                        val oldOwner = query(OwnerModel::class)
                            .find()
                            .firstOrNull { it.pets.any { p -> p.id == petId } }
                        oldOwner?.pets?.remove(pet)
                        newOwner.pets.add(pet)
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to adopt pet: ${e.message}"
            }
        }
    }

    fun adoptPetToNewOwner(petId: String, ownerName: String, ownerAge: Int) {
        if (!validateOwnerInput(ownerName, ownerAge)) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val realm = RealmHelper.getRealmInstance()
                realm.write {
                    val pet = query(PetModel::class, "id == $0", petId).first().find()
                    if (pet != null) {
                        val oldOwner = query(OwnerModel::class)
                            .find()
                            .firstOrNull { it.pets.any { p -> p.id == petId } }
                        oldOwner?.pets?.remove(pet)

                        copyToRealm(OwnerModel().apply {
                            this.name = ownerName.trim()
                            this.age = ownerAge
                            this.pets.add(pet)
                        })
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to adopt pet to new owner: ${e.message}"
            }
        }
    }

    private fun validatePetInput(name: String, type: String, age: Int): Boolean {
        return when {
            name.isBlank() -> {
                _error.value = "Pet name cannot be empty"
                false
            }
            type.isBlank() -> {
                _error.value = "Pet type cannot be empty"
                false
            }
            age < 0 || age > 100 -> {
                _error.value = "Pet age must be between 0 and 100"
                false
            }
            else -> true
        }
    }

    private fun validateOwnerInput(name: String, age: Int): Boolean {
        return when {
            name.isBlank() -> {
                _error.value = "Owner name cannot be empty"
                false
            }
            age < 1 || age > 150 -> {
                _error.value = "Owner age must be between 1 and 150"
                false
            }
            else -> true
        }
    }

    fun clearError() {
        _error.value = null
    }
}