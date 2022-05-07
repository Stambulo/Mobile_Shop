package com.stambulo.mobileshop.data.api

import com.stambulo.mobileshop.data.model.MobileShopData
import com.stambulo.mobileshop.data.model.Product
import retrofit2.Response
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val apiService: ApiService): Repository {
    override suspend fun getProduct(id: Int): Response<Product> = apiService.getProduct(id)
    override suspend fun getProducts(): Response<MobileShopData> = apiService.getProducts()
}
