package ru.umarsh.weatherappcompose.feature.weather

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import ru.umarsh.weatherappcompose.core.navigation.WeatherRoute

fun NavGraphBuilder.weatherScreen(
    viewModelFactory: (useLocation: Boolean) -> ViewModelProvider.Factory,
) {
    composable<WeatherRoute> { entry ->
        val route = entry.toRoute<WeatherRoute>()
        WeatherRouteScreen(
            useLocation = route.useLocation,
            viewModelFactory = viewModelFactory,
        )
    }
}

@Composable
private fun WeatherRouteScreen(
    useLocation: Boolean,
    viewModelFactory: (Boolean) -> ViewModelProvider.Factory,
) {
    val factory = viewModelFactory(useLocation)
    val viewModel: WeatherViewModel = viewModel(
        key = "weather_$useLocation",
        factory = factory,
    )
    WeatherScreen(viewModel = viewModel)
}
