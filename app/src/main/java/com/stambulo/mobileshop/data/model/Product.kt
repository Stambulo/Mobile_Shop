package com.stambulo.mobileshop.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class Product(
    @JsonProperty("id") val id: Int?,
    @JsonProperty("category") val category: ProductCategory?,
    @JsonProperty("name") val name: String?,
    @JsonProperty("details") val details: String?,
    @JsonProperty("size") val size: String?,
    @JsonProperty("colour") val colour: String?,
    @JsonProperty("price") val price: BigDecimal?,
    @JsonProperty("main_image") val main_image: String?,
    @JsonProperty("images") var images: List<Image>,
    @JsonProperty("reviews") val reviews: List<Reviews>?,
)

data class Image(
    @JsonProperty("image") val image: String?
)
