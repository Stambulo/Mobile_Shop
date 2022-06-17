package com.stambulo.mobileshop.data.model

import com.stambulo.mobileshop.data.db.EntityRoomProduct
import java.math.BigDecimal

data class Results (
    val id: Int,
    val name: String,
    val details: String,
    val size: String,
    val colour: String,
    val price: BigDecimal,
    val main_image: String,
    val category: ProductCategory
)

fun resultToRoomConverter(results: Results): EntityRoomProduct{
    return EntityRoomProduct(
        results.id,
        results.name,
        results.details,
        results.price,
        results.main_image,
        results.category.name
    )
}
