package ru.umarsh.weatherappcompose.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import ru.umarsh.weatherappcompose.WeatherApplication
import ru.umarsh.weatherappcompose.core.navigation.SplashRoute
import ru.umarsh.weatherappcompose.core.navigation.WeatherRoute
import ru.umarsh.weatherappcompose.di.WeatherViewModelFactory
import ru.umarsh.weatherappcompose.feature.splash.splashScreen
import ru.umarsh.weatherappcompose.feature.weather.weatherScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current
    val appComponent = remember(context) {
        (context.applicationContext as WeatherApplication).appComponent
    }

    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = modifier.fillMaxSize(),
    ) {
        splashScreen(
            onNavigateToWeather = { useLocation ->
                navController.navigate(WeatherRoute(useLocation = useLocation)) {
                    popUpTo(SplashRoute) {
                        inclusive = true
                    }
                }
            },
        )
        weatherScreen(
            viewModelFactory = { useLocation ->
                WeatherViewModelFactory(
                    weatherRepository = appComponent.weatherRepository(),
                    locationProvider = appComponent.locationProvider(),
                    useLocation = useLocation,
                )
            },
        )
    }
}
