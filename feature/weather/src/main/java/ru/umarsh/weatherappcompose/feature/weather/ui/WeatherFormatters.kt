package ru.umarsh.weatherappcompose.feature.weather.ui

fun weatherCodeDescription(code: Int): String {
    return when (code) {
        0 -> "Clear sky"
        1 -> "Mainly clear"
        2 -> "Partly cloudy"
        3 -> "Overcast"
        45, 48 -> "Fog"
        51, 53, 55 -> "Drizzle"
        61, 63, 65 -> "Rain"
        71, 73, 75 -> "Snow"
        80, 81, 82 -> "Rain showers"
        95 -> "Thunderstorm"
        else -> "Weather code $code"
    }
}

fun formatTemperature(value: Double): String = "${value.toInt()}°"

fun formatHour(time: String): String {
    return time.substringAfter('T').take(5)
}

fun formatDay(date: String): String {
    return date.substringAfterLast('-')
}
