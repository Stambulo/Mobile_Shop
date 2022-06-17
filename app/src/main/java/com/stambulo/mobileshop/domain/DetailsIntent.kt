package com.stambulo.mobileshop.domain

//TODO: MVI components should be in the same package, as their parent screen
sealed class DetailsIntent{
    object Idle: DetailsIntent()
    object BackNavigationIntent : DetailsIntent()
    object FavoritesNavigationIntent : DetailsIntent()
    data class FetchData(val productId: Int, val source: String) : DetailsIntent()
}
