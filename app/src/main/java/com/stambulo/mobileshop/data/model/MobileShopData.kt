package com.stambulo.mobileshop.data.model

data class MobileShopData (
    val count: Int,
    val total_pages: Int,
    val per_page: Int,
    val current_page: Int,
    val results: List<Results>
)
