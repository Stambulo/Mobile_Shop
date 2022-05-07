package com.stambulo.mobileshop.data.model

import com.google.gson.annotations.Expose

data class MobileShopData (
    @Expose val count: Int,
    @Expose val total_pages: Int,
    @Expose val per_page: Int,
    @Expose val current_page: Int,
    @Expose val results: ArrayList<Results>
)
