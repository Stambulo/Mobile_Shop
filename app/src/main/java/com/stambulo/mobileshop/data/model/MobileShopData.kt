package com.stambulo.mobileshop.data.model

import com.fasterxml.jackson.annotation.JsonProperty

//TODO: use Moshi or GSON as your main converter
data class MobileShopData (
    @JsonProperty("count") val count: Int,
    @JsonProperty("total_pages") val total_pages: Int,
    @JsonProperty("per_page") val per_page: Int,
    @JsonProperty("current_page") val current_page: Int,
    @JsonProperty("results") val results: List<Results>
)
