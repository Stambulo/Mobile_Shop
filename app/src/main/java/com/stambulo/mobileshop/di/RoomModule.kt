package com.stambulo.mobileshop.di

import android.content.Context
import androidx.room.Room
import com.stambulo.mobileshop.data.db.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): RoomDatabase =
        Room.databaseBuilder(
            appContext,
            RoomDatabase::class.java,
            "products.db").build()
}
