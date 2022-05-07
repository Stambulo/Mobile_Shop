package com.stambulo.mobileshop.domain

import com.stambulo.mobileshop.data.model.MobileShopData
import retrofit2.Response

sealed class ProductState{
    object Idle: ProductState()
    object Loading: ProductState()
    data class Error(val error: String): ProductState()
    data class Success(val success: Response<MobileShopData>): ProductState()
}
