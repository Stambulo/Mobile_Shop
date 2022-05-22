package com.stambulo.mobileshop.domain

sealed class DetailsIntent{
    object Idle: DetailsIntent()
    object BackNavigationIntent : DetailsIntent()
    object FavoritesNavigationIntent : DetailsIntent()
    data class FetchData(val productId: Int, val source: String) : DetailsIntent()
}
