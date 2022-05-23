package com.stambulo.mobileshop.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.stambulo.mobileshop.data.db.EntityRoomProduct
import com.stambulo.mobileshop.databinding.ItemProductsBinding
import com.stambulo.mobileshop.presentation.image.IImageLoader

class FavoritesAdapter(
    private var imageLoader: IImageLoader<ImageView>,
    private var onClickListener: OnFavoritesClickListener
): BaseAdapter() {

    private var data: MutableList<EntityRoomProduct> = mutableListOf()
    private lateinit var binding: ItemProductsBinding

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, itemView: View?, parent: ViewGroup): View {
        binding = ItemProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.apply {
            root.setOnClickListener { onClickListener.onItemClick(data[position].id) }
            inFavoriteIcon.setOnClickListener { onClickListener.onHeartClick(data[position].id) }
            productName.text = data[position].name
            productDescription.text = data[position].details
            priceFirst.text = "$ " + data[position].price.toString()
            priceSecond.text = "$ " + data[position].price.toString()
            notInFavoriteIcon.visibility = View.GONE
            inFavoriteIcon.visibility = View.VISIBLE
            imageLoader.loadInto(data[position].image, imageProduct)
        }
        return binding.root
    }

    fun updateData(results: List<EntityRoomProduct>) {
        data.clear()
        data.addAll(results)
    }
}

interface OnFavoritesClickListener{
    fun onItemClick(productId: Int)
    fun onHeartClick(productId: Int)
}
