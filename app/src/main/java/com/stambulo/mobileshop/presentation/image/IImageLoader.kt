package com.stambulo.mobileshop.presentation.image

interface IImageLoader<T> {
    fun loadInto(url: String, container: T)
}
