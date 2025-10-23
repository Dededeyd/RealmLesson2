package ph.edu.auf.quizonneldhyde.realmlesson2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ph.edu.auf.quizonneldhyde.realmlesson2.database.RealmHelper
import ph.edu.auf.quizonneldhyde.realmlesson2.database.realmmodel.PetModel

class PetViewModel : ViewModel() {

    private val _pets = MutableStateFlow<List<PetModel>>(emptyList())
    val pets: StateFlow<List<PetModel>> get() = _pets

    init {
        loadPets()
    }

    private fun loadPets(){
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            val results  = realm.query(PetModel::class).find()
            _pets.value = results
        }
    }



}