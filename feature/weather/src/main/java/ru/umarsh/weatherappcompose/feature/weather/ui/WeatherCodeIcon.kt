package ru.umarsh.weatherappcompose.feature.weather.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

fun weatherCodeIcon(code: Int): ImageVector {
    return when (code) {
        0 -> Icons.Filled.WbSunny
        1 -> Icons.Filled.WbSunny
        2 -> Icons.Filled.WbCloudy
        3 -> Icons.Filled.Cloud
        45, 48 -> Icons.Filled.Cloud
        51, 53, 55 -> Icons.Filled.Grain
        61, 63, 65 -> Icons.Filled.WaterDrop
        71, 73, 75 -> Icons.Filled.AcUnit
        80, 81, 82 -> Icons.Filled.WaterDrop
        95 -> Icons.Filled.Thunderstorm
        else -> Icons.Filled.WbCloudy
    }
}

@Composable
fun WeatherCodeIcon(
    code: Int,
    modifier: Modifier = Modifier,
    contentDescription: String = weatherCodeDescription(code),
) {
    Icon(
        imageVector = weatherCodeIcon(code),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = MaterialTheme.colorScheme.primary,
    )
}
