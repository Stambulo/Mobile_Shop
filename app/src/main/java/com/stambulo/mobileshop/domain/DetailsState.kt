package com.stambulo.mobileshop.domain

import com.stambulo.mobileshop.data.db.EntityRoomProduct
import com.stambulo.mobileshop.data.model.Product
import retrofit2.Response

//TODO: state in MVI should be as dataClass, however to implement this behaviour you should use lists with payloads.
sealed class DetailsState{
    object Idle: DetailsState()
    object Loading: DetailsState()
    object NavigateToFavorites: DetailsState()
    object NavigateToProducts: DetailsState()
    data class Error(val error: String): DetailsState()
    data class SuccessApi(val success: Response<Product>): DetailsState()
    data class SuccessDatabase(val product: EntityRoomProduct) : DetailsState()
}
