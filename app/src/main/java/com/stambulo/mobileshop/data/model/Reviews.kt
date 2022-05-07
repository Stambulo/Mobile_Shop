package com.stambulo.mobileshop.data.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Reviews(
    @JsonProperty("id") val id: Int?,
    @JsonProperty("modified_at") val modified_at: String?,
    @JsonProperty("created_at") val created_at: String?,
    @JsonProperty("first_name") val first_name: String?,
    @JsonProperty("last_name") val last_name: String?,
    @JsonProperty("image") val image: String?,
    @JsonProperty("rating") val rating: Int?,
    @JsonProperty("message") val message: String?
)
