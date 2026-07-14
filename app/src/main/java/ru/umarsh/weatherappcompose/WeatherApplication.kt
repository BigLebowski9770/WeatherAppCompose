package ru.umarsh.weatherappcompose

import android.app.Application
import ru.umarsh.weatherappcompose.di.AppComponent
import ru.umarsh.weatherappcompose.di.DaggerAppComponent

class WeatherApplication : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)
    }
}
