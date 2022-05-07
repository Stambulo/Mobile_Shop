package com.stambulo.mobileshop.data.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductCategory(
    @JsonProperty("name") val name: String?,
    @JsonProperty("icon") val icon: String?,
    @JsonProperty("id") val id: Int?
)
