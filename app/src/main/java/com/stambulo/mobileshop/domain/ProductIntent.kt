package com.stambulo.mobileshop.domain

sealed class ProductIntent {
    object FetchProducts: ProductIntent()
}
