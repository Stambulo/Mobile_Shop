package com.stambulo.mobileshop.data.api

import com.stambulo.mobileshop.data.model.MobileShopData
import com.stambulo.mobileshop.data.model.Product
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    /***********************************************************************************************
     *                          GET Products
     **********************************************************************************************/
    @GET("products")
    suspend fun getProducts(): Response<MobileShopData>

    /***********************************************************************************************
     *                          GET Product by {id}
     **********************************************************************************************/
    @GET("products/{id}")
    suspend fun getProducts(@Path("id") id: Int): Response<Product>

    /***********************************************************************************************
     *                          GET Products with {page} and {page_size}
     **********************************************************************************************/
    @GET("products")
    suspend fun getProductsPage(
        @Query("page") page: Int,
        @Query("page_size") page_size: Int): Response<MobileShopData>
}
