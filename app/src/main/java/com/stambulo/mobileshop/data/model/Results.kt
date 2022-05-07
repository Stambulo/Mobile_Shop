package com.stambulo.mobileshop.data.model

import com.google.gson.annotations.Expose
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicInteger

data class Results (
    @Expose val productCategory: ProductCategory,
    @Expose val name: String,
    @Expose val details: String,
    @Expose val size: String,
    @Expose val colour: String,
    @Expose val price: BigDecimal,
    @Expose val main_image: String,
    @Expose val id: Int
)
