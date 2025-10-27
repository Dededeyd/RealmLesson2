package ph.edu.auf.quizonneldhyde.realmlesson2.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

enum class PetType(
    val displayName: String,
    val icon: ImageVector,
    val emoji: String
) {
    DOG("Dog", Icons.Default.Pets, "🐕"),
    CAT("Cat", Icons.Default.Favorite, "🐈"),
    RABBIT("Rabbit", Icons.Default.Grass, "🐰"),
    BIRD("Bird", Icons.Default.Air, "🐦"),
    FISH("Fish", Icons.Default.WaterDrop, "🐠"),
    HAMSTER("Hamster", Icons.Default.Circle, "🐹"),
    TURTLE("Turtle", Icons.Default.Layers, "🐢"),
    SNAKE("Snake", Icons.Default.Timeline, "🐍"),
    GUINEA_PIG("Guinea Pig", Icons.Default.FiberManualRecord, "🐹"),
    OTHER("Other", Icons.Default.Pets, "🐾");

    companion object {
        fun fromDisplayName(name: String): PetType {
            return entries.find { it.displayName.equals(name, ignoreCase = true) } ?: OTHER
        }

        fun getIcon(petTypeName: String): ImageVector {
            return fromDisplayName(petTypeName).icon
        }

        fun getEmoji(petTypeName: String): String {
            return fromDisplayName(petTypeName).emoji
        }
    }
}