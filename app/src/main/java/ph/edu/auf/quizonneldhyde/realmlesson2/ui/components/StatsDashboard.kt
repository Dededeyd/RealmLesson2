package ph.edu.auf.quizonneldhyde.realmlesson2.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items 
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val StatPurple = Color(0xFFEFE6FD)
val StatPink = Color(0xFFFCE7F3)
val StatPurple2 = Color(0xFFF5F3FF)
val StatGreen = Color(0xFFECFDF5)

private data class StatInfo(
    val icon: ImageVector,
    val value: Int,
    val label: String,
    val backgroundColor: Color,
    val iconColor: Color
)

@Composable
fun StatsDashboard(
    totalPets: Int,
    petsWithOwners: Int,
    totalOwners: Int,
    ownersWithPets: Int,
    modifier: Modifier = Modifier
) {
    val statsList = listOf(
        StatInfo(Icons.Default.Pets, totalPets, "Total Pets", StatPurple, Color(0xFF7C3AED)),
        StatInfo(Icons.Default.Favorite, petsWithOwners, "Adopted", StatPink, Color(0xFFDB2777)),
        StatInfo(Icons.Default.Person, totalOwners, "Owners", StatPurple2, Color(0xFF6D28D9)),
        StatInfo(Icons.Default.Group, ownersWithPets, "With Pets", StatGreen, Color(0xFF059669))
    )

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp) 
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp), 
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Default.GridView,
                    contentDescription = "View",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(statsList) { stat ->
                    AnimatedStatCard(
                        icon = stat.icon,
                        value = stat.value,
                        label = stat.label,
                        backgroundColor = stat.backgroundColor,
                        iconColor = stat.iconColor,
                        modifier = Modifier.width(140.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedStatCard(
    icon: ImageVector,
    value: Int,
    label: String,
    backgroundColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    var targetValue by remember { mutableStateOf(0) }
    val animatedValue by animateIntAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "statValue"
    )

    LaunchedEffect(value) {
        targetValue = value
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(vertical = 24.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(28.dp),
                tint = iconColor
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = animatedValue.toString(),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = iconColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
