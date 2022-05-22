package com.stambulo.mobileshop.domain

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.stambulo.mobileshop.data.model.Results

sealed class ProductsIntent {

    object FetchProducts: ProductsIntent()
    object GoToFavorites: ProductsIntent()
    data class GoToDetails(val bundle: Bundle): ProductsIntent()
    data class ConnectionChanged(val isOnline: Boolean): ProductsIntent()

    data class InsertProductIntoDb(
        val product: Results,
        val position: Int,
        val itemView: View?,
        val parent: ViewGroup
    ): ProductsIntent()

    data class RemoveFromDB(
        val id: Int,
        val position: Int,
        val itemView: View?,
        val parent: ViewGroup
    ): ProductsIntent()
}
