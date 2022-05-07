package com.stambulo.mobileshop.data.model

import com.google.gson.annotations.Expose

data class ProductCategory(
    @Expose val name: String,
    @Expose val icon: String,
    @Expose val id: Int
)
