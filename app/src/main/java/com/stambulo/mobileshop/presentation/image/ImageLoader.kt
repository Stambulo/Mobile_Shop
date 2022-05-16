package com.stambulo.mobileshop.presentation.image

import android.widget.ImageView
import com.bumptech.glide.Glide

class ImageLoader: IImageLoader<ImageView> {

    override fun loadInto(url: String, container: ImageView) {
        Glide.with(container.context)
            .asBitmap()
            .load(url)
            .into(container)
    }
}
