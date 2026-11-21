package com.techstore.app.ui.main.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.techstore.app.R
import com.techstore.app.data.model.ProductModel
import com.techstore.app.data.repository.ProductRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val productRepository = ProductRepository()
    private lateinit var productAdapter: ProductAdapter
    private var products: List<ProductModel> = emptyList()
    private var selectedCategory: String? = null
    private var searchQuery: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productAdapter = ProductAdapter()
        view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewProducts).apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
        }

        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.categoryAll)
            .setOnClickListener { selectCategory(null) }
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.categoryPhones)
            .setOnClickListener { selectCategory("Celulares") }
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.categoryLaptops)
            .setOnClickListener { selectCategory("Laptops") }
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.categoryAudio)
            .setOnClickListener { selectCategory("Audio") }

        view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextHomeSearch)
            .doAfterTextChanged {
                searchQuery = it?.toString()?.trim().orEmpty()
                applyFilters()
            }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productRepository.observeActiveProducts()
                    .catch { error ->
                        Toast.makeText(
                            requireContext(),
                            "No se pudieron cargar los productos: ${error.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .collect { result ->
                        products = result
                        applyFilters()
                    }
            }
        }
    }

    private fun selectCategory(category: String?) {
        selectedCategory = category
        applyFilters()
    }

    private fun applyFilters() {
        val normalizedQuery = searchQuery.lowercase(Locale.ROOT)
        val filteredProducts = products.filter { product ->
            val matchesCategory =
                selectedCategory == null || product.category.equals(selectedCategory, ignoreCase = true)
            val matchesQuery = normalizedQuery.isBlank() ||
                product.name.contains(normalizedQuery, ignoreCase = true) ||
                product.brand.contains(normalizedQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
        productAdapter.submitList(filteredProducts)
    }
}
