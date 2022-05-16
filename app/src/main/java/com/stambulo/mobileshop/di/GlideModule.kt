package com.stambulo.mobileshop.di

import android.widget.ImageView
import com.stambulo.mobileshop.presentation.image.IImageLoader
import com.stambulo.mobileshop.presentation.image.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GlideModule {

    @Singleton
    @Provides
    fun provideImageLoader(): IImageLoader<ImageView> = ImageLoader()
}
