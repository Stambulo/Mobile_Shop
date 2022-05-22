package com.stambulo.mobileshop.domain

import android.os.Bundle
import com.stambulo.mobileshop.data.db.EntityRoomProduct

sealed class FavoritesState{
    object Idle: FavoritesState()
    object BackNavigation: FavoritesState()
    data class ToDetailsNavigation(val bundle: Bundle): FavoritesState()
    data class Success(val success: List<EntityRoomProduct>): FavoritesState()
}
