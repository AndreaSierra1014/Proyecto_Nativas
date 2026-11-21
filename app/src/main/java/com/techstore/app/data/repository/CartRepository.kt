package com.techstore.app.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.techstore.app.data.model.CartItem
import com.techstore.app.data.model.ProductModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartRepository private constructor(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("techstore_cart", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _items = MutableStateFlow(load())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    fun add(product: ProductModel) {
        val current = _items.value.toMutableList()
        val index = current.indexOfFirst { it.productId == product.id }
        if (index >= 0) {
            current[index] = current[index].copy(quantity = current[index].quantity + 1)
        } else {
            current.add(
                CartItem(
                    productId = product.id,
                    name = product.name,
                    price = product.price,
                    quantity = 1,
                    imageUrl = product.imageUrl
                )
            )
        }
        update(current)
    }

    fun setQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) return remove(productId)
        update(_items.value.map {
            if (it.productId == productId) it.copy(quantity = quantity) else it
        })
    }

    fun remove(productId: String) =
        update(_items.value.filterNot { it.productId == productId })

    fun clear() = update(emptyList())

    fun total(): Double = _items.value.sumOf { it.price * it.quantity }

    fun count(): Int = _items.value.sumOf { it.quantity }

    private fun update(list: List<CartItem>) {
        _items.value = list
        prefs.edit().putString(KEY_ITEMS, gson.toJson(list)).apply()
    }

    private fun load(): List<CartItem> {
        val json = prefs.getString(KEY_ITEMS, null) ?: return emptyList()
        val type = object : TypeToken<List<CartItem>>() {}.type
        return try {
            gson.fromJson<List<CartItem>>(json, type)
                ?: throw IllegalStateException("El carrito guardado no contiene una lista válida.")
        } catch (e: JsonParseException) {
            throw IllegalStateException("No se pudo leer el carrito guardado.", e)
        }
    }

    companion object {
        private const val KEY_ITEMS = "items"

        @Volatile
        private var instance: CartRepository? = null

        fun getInstance(context: Context): CartRepository =
            instance ?: synchronized(this) {
                instance ?: CartRepository(context).also { instance = it }
            }
    }
}
