package ru.umarsh.weatherappcompose.core.navigation

import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute

@Serializable
data class WeatherRoute(
    val useLocation: Boolean = false,
)
