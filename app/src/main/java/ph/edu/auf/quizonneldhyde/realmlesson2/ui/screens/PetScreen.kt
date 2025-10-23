package ph.edu.auf.quizonneldhyde.realmlesson2.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ph.edu.auf.quizonneldhyde.realmlesson2.database.realmmodel.PetModel
import ph.edu.auf.quizonneldhyde.realmlesson2.viewmodels.PetViewModel

@Composable
fun PetScreen(petViewModel: PetViewModel = viewModel()) {
    val pets by petViewModel.pets.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Scaffold { paddingValues ->
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                itemsIndexed(
                    items = pets,
                    key = { _, item -> item.id }
                ) { _, content  -> PetItem(petModel = content)
                }
            }
        }
    }
}

@Composable
fun PetItem(petModel: PetModel){
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(0.dp, 10.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        shape = RoundedCornerShape(10.dp )
    ){
        Column(modifier = Modifier.padding(10.dp))
        {
            Text(
                text = "Pet Name: ${petModel.name}",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Pet Age: ${petModel.age}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Pet Type: ${petModel.petType}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

