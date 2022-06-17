package com.stambulo.mobileshop.data.model

import java.math.BigDecimal

data class Product(
    val id: Int?,
    val category: ProductCategory?,
    val name: String?,
    val details: String?,
    val size: String?,
    val colour: String?,
    val price: BigDecimal?,
    val main_image: String?,
    var images: List<Image>,
    val reviews: List<Reviews>?,
)

data class Image(
    val image: String?
)
