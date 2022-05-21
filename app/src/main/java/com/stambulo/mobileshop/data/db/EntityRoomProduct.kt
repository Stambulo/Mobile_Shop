package com.stambulo.mobileshop.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stambulo.mobileshop.data.model.ProductCategory
import com.stambulo.mobileshop.data.model.Results
import java.math.BigDecimal

@Entity(tableName = "products")
data class EntityRoomProduct(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "price") val price: BigDecimal,
    @ColumnInfo(name = "image") val image: String
)

fun roomEntityToResult(entity: EntityRoomProduct): Results{
    return Results(
        entity.id!!,
        entity.name,
        entity.description,
        "size",
        "colour",
        entity.price,
        entity.image,
        ProductCategory("string", "icon", 1)
    )
}
