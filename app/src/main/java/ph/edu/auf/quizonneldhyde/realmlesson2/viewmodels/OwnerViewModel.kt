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

class OwnerViewModel : ViewModel() {

    private val _owners = MutableStateFlow<List<OwnerModel>>(emptyList())
    val owners: StateFlow<List<OwnerModel>> get() = _owners

    init {
        loadOwners()
    }

    private fun loadOwners() {
        val realm = RealmHelper.getRealmInstance()

        viewModelScope.launch(Dispatchers.IO) {
            realm.query(OwnerModel::class)
                .asFlow()
                .collect { results ->
                    _owners.value = results.list
                }
        }
    }

    fun addOwner(name: String, age: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                copyToRealm(OwnerModel().apply {
                    this.name = name
                    this.age = age
                })
            }
        }
    }

    // --- MODIFIED HERE: Added 'petBreed' parameter ---
    fun addOwnerWithPet(ownerName: String, ownerAge: Int, petName: String, petType: String, petBreed: String, petAge: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val newPet = PetModel().apply {
                    this.name = petName
                    this.petType = petType
                    this.breed = petBreed // <-- THIS IS THE FIX
                    this.age = petAge
                }
                copyToRealm(OwnerModel().apply {
                    this.name = ownerName
                    this.age = ownerAge
                    this.pets.add(newPet)
                })
            }
        }
    }

    // Delete owner and all their pets (cascade delete)
    fun deleteOwnerAndPets(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val owner = query(OwnerModel::class, "id == $0", id).first().find()
                owner?.let {
                    // Create a list copy to avoid concurrent modification
                    val petsToDelete = it.pets.toList()
                    // Delete all pets
                    petsToDelete.forEach { pet ->
                        delete(pet)
                    }
                    // Then delete owner
                    delete(it)
                }
            }
        }
    }

    // Delete owner only, keep pets available for adoption
    fun deleteOwnerOnly(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val owner = query(OwnerModel::class, "id == $0", id).first().find()
                owner?.let {
                    // Clear the pets list (makes them available for adoption)
                    it.pets.clear()
                    // Then delete owner
                    delete(it)
                }
            }
        }
    }
}