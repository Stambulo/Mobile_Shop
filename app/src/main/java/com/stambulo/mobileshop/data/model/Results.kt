package com.stambulo.mobileshop.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class Results (
    @JsonProperty("id") val id: Int?,
    @JsonProperty("name") val name: String?,
    @JsonProperty("details") val details: String?,
    @JsonProperty("size") val size: String?,
    @JsonProperty("colour") val colour: String?,
    @JsonProperty("price") val price: BigDecimal?,
    @JsonProperty("main_image") val main_image: String?,
    @JsonProperty("productCategory") val category: ProductCategory?
)
