package com.stambulo.mobileshop.data.api

import com.stambulo.mobileshop.data.model.MobileShopData
import com.stambulo.mobileshop.data.model.Product
import retrofit2.Response

interface Repository {
    suspend fun getProduct(id: Int): Response<Product>
    suspend fun getProducts(): Response<MobileShopData>
}