package ru.umarsh.weatherappcompose.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.umarsh.weatherappcompose.core.common.DefaultDispatcherProvider
import ru.umarsh.weatherappcompose.core.common.DispatcherProvider
import javax.inject.Singleton

@Module
object AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()
}
