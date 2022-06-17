package com.stambulo.mobileshop.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.stambulo.mobileshop.data.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Named("baseUrl")
    @Provides
    fun baseUrl() = "http://mobile-shop-api.hiring.devebs.net/"

    @Singleton
    @Provides
    fun api(@Named("baseUrl") baseUrl: String): ApiService =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson()))
            .client(creteOkHttpClient(MainInterceptor()))
            .build()
            .create(ApiService::class.java)

    @Singleton
    @Provides
    fun gson(): Gson = GsonBuilder()
        .create()

    private fun creteOkHttpClient(interceptor: Interceptor): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor)
        httpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        return httpClient.build()
    }

    inner class MainInterceptor: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            return chain.proceed(chain.request())
        }
    }
}
