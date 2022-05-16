package com.stambulo.mobileshop.domain

sealed class ProductsIntent { object FetchProducts: ProductsIntent() }
