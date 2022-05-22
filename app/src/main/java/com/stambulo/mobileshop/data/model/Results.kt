package com.stambulo.mobileshop.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.stambulo.mobileshop.data.db.EntityRoomProduct
import java.math.BigDecimal

data class Results (
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String,
    @JsonProperty("details") val details: String,
    @JsonProperty("size") val size: String,
    @JsonProperty("colour") val colour: String,
    @JsonProperty("price") val price: BigDecimal,
    @JsonProperty("main_image") val main_image: String,
    @JsonProperty("category") val category: ProductCategory
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
