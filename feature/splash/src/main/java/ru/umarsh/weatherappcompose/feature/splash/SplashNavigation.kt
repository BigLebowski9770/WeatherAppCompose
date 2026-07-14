package ru.umarsh.weatherappcompose.feature.splash

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.umarsh.weatherappcompose.core.navigation.SplashRoute

fun NavGraphBuilder.splashScreen(
    onNavigateToWeather: (useLocation: Boolean) -> Unit,
) {
    composable<SplashRoute> {
        SplashScreen(onFinished = onNavigateToWeather)
    }
}
