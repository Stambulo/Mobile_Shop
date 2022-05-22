package com.stambulo.mobileshop.domain

import android.os.Bundle

sealed class FavoritesIntent {
    object SelectItems: FavoritesIntent()
    object BackNavigation : FavoritesIntent()
    data class DeleteItems(val id: Int) : FavoritesIntent()
    data class ToDetailsNavigation(val bundle: Bundle): FavoritesIntent()
}
