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

    fun addOwnerWithPet(ownerName: String, ownerAge: Int, petName: String, petType: String, petBreed: String, petAge: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val newPet = PetModel().apply {
                    this.name = petName
                    this.petType = petType
                    this.breed = petBreed
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

    fun deleteOwnerAndPets(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val owner = query(OwnerModel::class, "id == $0", id).first().find()
                owner?.let {
                    val petsToDelete = it.pets.toList()
                    // Delete all pets
                    petsToDelete.forEach { pet ->
                        delete(pet)
                    }
                    delete(it)
                }
            }
        }
    }

    fun deleteOwnerOnly(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            realm.write {
                val owner = query(OwnerModel::class, "id == $0", id).first().find()
                owner?.let {
\                    it.pets.clear()
                    delete(it)
                }
            }
        }
    }
}
