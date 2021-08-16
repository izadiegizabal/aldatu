package xyz.izadi.aldatu.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import xyz.izadi.aldatu.data.local.PreferencesManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PrefModule {
    companion object {
        const val PREFERENCE_KEY = "my_preferences"
    }

    @Singleton
    @Provides
    fun providesPreferences(@ApplicationContext appContext: Context): SharedPreferences =
        appContext.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun providesPrefManager(preferences: SharedPreferences): PreferencesManager =
        PreferencesManager(preferences)
}