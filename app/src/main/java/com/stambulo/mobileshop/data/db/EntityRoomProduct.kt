package com.stambulo.mobileshop.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "products")
data class EntityRoomProduct(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "details") val details: String,
    @ColumnInfo(name = "price") val price: BigDecimal,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "description") val description: String,
)
