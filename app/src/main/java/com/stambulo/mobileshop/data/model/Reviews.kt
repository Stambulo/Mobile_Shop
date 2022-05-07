package com.stambulo.mobileshop.data.model

import com.google.gson.annotations.Expose

data class Reviews(
    @Expose val id: Int,
    @Expose val modified_at: String,
    @Expose val created_at: String,
    @Expose val first_name: String,
    @Expose val last_name: String,
    @Expose val image: String,
    @Expose val rating: Int,
    @Expose val message: String
)
