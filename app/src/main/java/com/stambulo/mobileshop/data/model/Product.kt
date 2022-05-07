package com.stambulo.mobileshop.data.model

import com.google.gson.annotations.Expose
import java.math.BigDecimal

data class Product(
    @Expose val productCategory: ProductCategory,
    @Expose val name: String,
    @Expose val details: String,
    @Expose val size: String,
    @Expose val colour: String,
    @Expose val price: BigDecimal,
    @Expose val id: Int,
    @Expose val main_image: String,
    @Expose val images: List<String>,
    @Expose val reviews: List<Reviews>
)
