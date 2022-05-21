package com.stambulo.mobileshop.domain

sealed class FavoritesIntent {
    object SelectItems: FavoritesIntent()
    class DeleteItems(val id: Int) : FavoritesIntent()
}
