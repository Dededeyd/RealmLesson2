package ph.edu.auf.quizonneldhyde.realmlesson2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ph.edu.auf.quizonneldhyde.realmlesson2.database.RealmHelper
import ph.edu.auf.quizonneldhyde.realmlesson2.database.realmmodel.OwnerModel

class OwnerViewModel : ViewModel() {
    
    private val _owners = MutableStateFlow<List<OwnerModel>>(emptyList())
    val owners: StateFlow<List<OwnerModel>> = _owners

    init {
        loadOwners()
    }



    private fun loadOwners(){
        viewModelScope.launch(Dispatchers.IO) {
            val realm = RealmHelper.getRealmInstance()
            val results = realm.query(OwnerModel::class).find()
            _owners.value = results
        }
    }


}