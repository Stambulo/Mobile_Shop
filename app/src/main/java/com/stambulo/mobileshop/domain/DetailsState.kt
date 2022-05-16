package com.stambulo.mobileshop.domain

import com.stambulo.mobileshop.data.model.Product
import retrofit2.Response

sealed class DetailsState{
    object Idle: DetailsState()
    object Loading: DetailsState()
    data class Error(val error: String): DetailsState()
    data class Success(val success: Response<Product>): DetailsState()
}
