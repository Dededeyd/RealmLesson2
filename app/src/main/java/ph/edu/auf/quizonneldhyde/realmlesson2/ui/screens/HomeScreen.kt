package ph.edu.auf.quizonneldhyde.realmlesson2.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ph.edu.auf.quizonneldhyde.realmlesson2.navigation.AppNavRoutes

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Pet Realm Sampler",
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { navController.navigate(AppNavRoutes.PetList.route) }
        ) {
            Text("Pet List")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { navController.navigate(AppNavRoutes.OwnerList.route)}
        ) {
            Text("Owner List")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}
