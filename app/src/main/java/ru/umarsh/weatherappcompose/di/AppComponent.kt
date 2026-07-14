package ru.umarsh.weatherappcompose.di

import android.app.Application
import dagger.Component
import ru.umarsh.weatherappcompose.data.di.DatabaseModule
import ru.umarsh.weatherappcompose.data.di.NetworkModule
import ru.umarsh.weatherappcompose.data.di.RepositoryModule
import ru.umarsh.weatherappcompose.domain.location.DeviceLocationProvider
import ru.umarsh.weatherappcompose.domain.repository.WeatherRepository
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        DatabaseModule::class,
        RepositoryModule::class,
    ],
)
interface AppComponent {

    fun weatherRepository(): WeatherRepository

    fun locationProvider(): DeviceLocationProvider

    @Component.Factory
    interface Factory {
        fun create(@dagger.BindsInstance application: Application): AppComponent
    }
}
