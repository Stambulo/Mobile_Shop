package com.stambulo.mobileshop.domain

import android.view.View
import android.view.ViewGroup
import com.stambulo.mobileshop.data.model.Results

sealed class ProductsIntent {

    object FetchProducts: ProductsIntent()

    data class InsertProductIntoDb(
        val product: Results,
        val position: Int,
        val itemView: View?,
        val parent: ViewGroup
    ): ProductsIntent()

    data class RemoveFromFavorites(
        val id: Int,
        val position: Int,
        val itemView: View?,
        val parent: ViewGroup
    ): ProductsIntent()
}
