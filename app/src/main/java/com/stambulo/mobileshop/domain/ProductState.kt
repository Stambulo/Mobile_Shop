package com.stambulo.mobileshop.domain

import android.view.View
import android.view.ViewGroup
import com.stambulo.mobileshop.data.model.Results

sealed class ProductState{

    object Idle: ProductState()
    object Loading: ProductState()
    data class Error(val error: String): ProductState()

    data class UpdateIndices(
        val indices: List<Int>,
        val position: Int,
        val itemView: View?,
        val parent: ViewGroup): ProductState()

    data class Success(
        val success: List<Results>?,
        val endOfList: Boolean,
        val indices: List<Int>) : ProductState()
}
