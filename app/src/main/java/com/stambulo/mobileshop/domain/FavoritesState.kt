package com.stambulo.mobileshop.domain

import com.stambulo.mobileshop.data.db.EntityRoomProduct

sealed class FavoritesState{
    object Idle: FavoritesState()
    data class Success(val success: List<EntityRoomProduct>): FavoritesState()
}
