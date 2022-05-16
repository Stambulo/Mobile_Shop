package com.stambulo.mobileshop.domain

import com.stambulo.mobileshop.data.model.Results

sealed class ProductState{
    object Idle: ProductState()
    object Loading: ProductState()
    data class Error(val error: String): ProductState()
    data class Success(val success: List<Results>?, val endOfList: Boolean): ProductState()
}
