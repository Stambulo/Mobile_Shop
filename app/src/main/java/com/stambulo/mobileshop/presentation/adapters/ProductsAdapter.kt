package com.stambulo.mobileshop.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.stambulo.mobileshop.data.model.Results
import com.stambulo.mobileshop.databinding.ItemProductsBinding
import com.stambulo.mobileshop.presentation.image.IImageLoader

class ProductsAdapter(
    private var imageLoader: IImageLoader<ImageView>,
    private var onListItemClickListener: OnListItemClickListener
) : BaseAdapter() {

    private var data: MutableList<Results> = mutableListOf()
    private lateinit var binding: ItemProductsBinding
    private var indicesInDb: List<Int> = listOf()

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, itemView: View?, parent: ViewGroup): View {
        binding = ItemProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.apply {
            if (indicesInDb.contains(data[position].id)){
                inFavoriteIcon.visibility = View.VISIBLE
            }
            root.setOnClickListener {
                onListItemClickListener.onItemClick(data[position].id) }
            notInFavoriteIcon.setOnClickListener {
                onListItemClickListener.onAddToFavoritesClick(data[position], position, itemView, parent) }
            inFavoriteIcon.setOnClickListener {
                onListItemClickListener.onRemoveFromFavoritesClick(data[position].id, position, itemView, parent)
            }
            productName.text = data[position].name
            productDescription.text = data[position].details
            priceFirst.text = "$ " + data[position].price.toString()
            priceSecond.text = "$ " + data[position].price.toString()
            imageLoader.loadInto(data[position].main_image, imageProduct)
        }
        return binding.root
    }

    fun updateList(results: List<Results>, indices: List<Int>) {
        indicesInDb = indices
        data.clear()
        data.addAll(results)
    }

    /********************************************************/
    /**                  Show and hide hearts               */
    /********************************************************/
    fun updateItemView(indices: List<Int>, position: Int, itemView: View?, parent: ViewGroup) {
        indicesInDb = indices
        getView(position, itemView, parent)
    }
}

interface OnListItemClickListener{
    fun onItemClick(productId: Int)
    fun onAddToFavoritesClick(product: Results, position: Int, itemView: View?, parent: ViewGroup)
    fun onRemoveFromFavoritesClick(id: Int, position: Int, itemView: View?, parent: ViewGroup)
}
