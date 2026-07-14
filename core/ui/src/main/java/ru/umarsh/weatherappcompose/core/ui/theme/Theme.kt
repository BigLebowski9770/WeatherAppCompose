package ru.umarsh.weatherappcompose.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SkyBlue,
    onPrimary = Color.White,
    primaryContainer = SkyBlueContainer,
    onPrimaryContainer = SkyBlueDark,
    secondary = RainBlue,
    onSecondary = Color.White,
    background = CloudWhite,
    onBackground = Color(0xFF1A2332),
    surface = Color.White,
    onSurface = Color(0xFF1A2332),
    surfaceVariant = Color(0xFFE8F0F8),
    onSurfaceVariant = Color(0xFF5A6B7D),
)

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlueDarkTheme,
    onPrimary = Color(0xFF0F1419),
    primaryContainer = SkyBlueDarkContainer,
    onPrimaryContainer = SkyBlueDarkTheme,
    secondary = RainBlue,
    onSecondary = Color(0xFF0F1419),
    background = NightBackground,
    onBackground = Color(0xFFE8F0F8),
    surface = NightSurface,
    onSurface = Color(0xFFE8F0F8),
    surfaceVariant = Color(0xFF243044),
    onSurfaceVariant = Color(0xFF9AABB8),
)

@Composable
fun WeatherAppComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
