package com.stambulo.mobileshop.domain

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.stambulo.mobileshop.data.model.Results

sealed class ProductState{

    object Idle: ProductState()
    object Loading: ProductState()
    object LostConnection : ProductState()
    object RestoreConnection : ProductState()
    object NavigateToFavorites : ProductState()
    data class Error(val error: String): ProductState()
    data class NavigateToDetails(val bundle: Bundle) : ProductState()

    data class UpdateItemView(
        val indices: List<Int>,
        val position: Int,
        val itemView: View?,
        val parent: ViewGroup): ProductState()

    data class Success(
        val success: List<Results>?,
        val endOfList: Boolean,
        val indices: List<Int>) : ProductState()
}
