package com.stambulo.mobileshop.data.api

import com.stambulo.mobileshop.data.model.MobileShopData
import com.stambulo.mobileshop.data.model.Product
import retrofit2.Response
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val apiService: ApiService): Repository {
    override suspend fun getProducts(id: Int): Response<Product> = apiService.getProducts(id)
    override suspend fun getProducts(): Response<MobileShopData> = apiService.getProducts()
    override suspend fun getProductsPage(page: Int, page_size: Int): Response<MobileShopData> =
        apiService.getProductsPage(page, page_size)
}
