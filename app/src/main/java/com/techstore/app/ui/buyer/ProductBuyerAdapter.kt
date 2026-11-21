package com.techstore.app.ui.buyer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.techstore.app.data.model.Product
import com.techstore.app.databinding.ItemCatalogBinding
import com.techstore.app.util.toCOP

class ProductBuyerAdapter(
    private val onAdd: (Product) -> Unit
) : RecyclerView.Adapter<ProductBuyerAdapter.ProductViewHolder>() {

    private val items = mutableListOf<Product>()

    fun submit(products: List<Product>) {
        items.clear()
        items.addAll(products)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemCatalogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = items[position]
        holder.binding.tvName.text = product.nombre
        holder.binding.tvPrice.text = product.precio.toCOP()
        holder.binding.ivImage.load(product.imagenUrl) {
            crossfade(true)
        }
        holder.binding.btnAdd.setOnClickListener { onAdd(product) }
    }

    override fun getItemCount(): Int = items.size

    class ProductViewHolder(val binding: ItemCatalogBinding) :
        RecyclerView.ViewHolder(binding.root)
}
