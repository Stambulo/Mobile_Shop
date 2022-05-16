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
            root.setOnClickListener { onListItemClickListener.onItemClick(data[position].id) }
            productName.text = data[position].name
            productDescription.text = data[position].details
            priceFirst.text = "$ " + data[position].price.toString()
            priceSecond.text = "$ " + data[position].price.toString()
            imageLoader.loadInto(data[position].main_image, imageProduct)
        }
        return binding.root
    }

    fun updateData(results: List<Results>) {
        data.clear()
        data.addAll(results)
    }
}

interface OnListItemClickListener{ fun onItemClick(productId: Int)}
