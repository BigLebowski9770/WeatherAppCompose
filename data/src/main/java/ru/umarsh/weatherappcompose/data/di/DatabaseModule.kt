package ru.umarsh.weatherappcompose.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.umarsh.weatherappcompose.data.local.ForecastCacheDao
import ru.umarsh.weatherappcompose.data.local.WeatherDatabase
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWeatherDatabase(context: Context): WeatherDatabase {
        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather.db",
        ).build()
    }

    @Provides
    @Singleton
    fun provideForecastCacheDao(database: WeatherDatabase): ForecastCacheDao {
        return database.forecastCacheDao()
    }
}
