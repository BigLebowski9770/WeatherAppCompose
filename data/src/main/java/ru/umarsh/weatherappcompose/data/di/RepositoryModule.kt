package ru.umarsh.weatherappcompose.data.di

import dagger.Binds
import dagger.Module
import ru.umarsh.weatherappcompose.data.location.AndroidLocationProvider
import ru.umarsh.weatherappcompose.data.repository.WeatherRepositoryImpl
import ru.umarsh.weatherappcompose.domain.location.DeviceLocationProvider
import ru.umarsh.weatherappcompose.domain.repository.WeatherRepository
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindLocationProvider(impl: AndroidLocationProvider): DeviceLocationProvider
}
