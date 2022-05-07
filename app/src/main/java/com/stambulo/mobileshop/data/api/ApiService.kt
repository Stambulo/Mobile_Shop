package com.stambulo.mobileshop.data.api

import com.stambulo.mobileshop.data.model.MobileShopData
import com.stambulo.mobileshop.data.model.Product
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("products")
    suspend fun getProducts(): Response<MobileShopData>

    @GET("products/{id}")
    suspend fun getProduct(@Query("id") id: Int): Response<Product>
}
