package com.techstore.app.ui.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.techstore.app.R
import com.techstore.app.data.model.ProductModel
import com.techstore.app.util.loadImage
import com.techstore.app.util.setPrice

class ProductAdapter :
    ListAdapter<ProductModel, ProductAdapter.ProductViewHolder>(ProductDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image = itemView.findViewById<ShapeableImageView>(R.id.imageProduct)
        private val brand = itemView.findViewById<TextView>(R.id.textProductBrand)
        private val name = itemView.findViewById<TextView>(R.id.textProductName)
        private val price = itemView.findViewById<TextView>(R.id.textProductPrice)

        fun bind(product: ProductModel) {
            image.contentDescription = product.name
            image.loadImage(product.imageUrl)
            brand.text = product.brand
            name.text = product.name
            price.setPrice(product.price)
        }
    }

    private object ProductDiffCallback : DiffUtil.ItemCallback<ProductModel>() {
        override fun areItemsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean =
            oldItem == newItem
    }
}
