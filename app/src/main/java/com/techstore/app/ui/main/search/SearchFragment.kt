package com.techstore.app.ui.main.search

import android.os.Bundle
import android.view.View
import android.widget.TextView
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
import com.techstore.app.ui.main.home.ProductAdapter
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val productRepository = ProductRepository()
    private lateinit var productAdapter: ProductAdapter
    private var products: List<ProductModel> = emptyList()
    private var searchQuery: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productAdapter = ProductAdapter()
        view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewSearchResults)
            .apply {
                layoutManager = GridLayoutManager(requireContext(), 2)
                adapter = productAdapter
            }

        view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextSearch)
            .doAfterTextChanged {
                searchQuery = it?.toString()?.trim().orEmpty()
                applyFilters(view)
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
                        applyFilters(view)
                    }
            }
        }
    }

    private fun applyFilters(view: View) {
        val query = searchQuery
        val filteredProducts = if (query.isBlank()) {
            emptyList()
        } else {
            products.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                    product.brand.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true)
            }
        }
        productAdapter.submitList(filteredProducts)

        view.findViewById<TextView>(R.id.tvSearchEmpty).apply {
            text = if (query.isBlank()) {
                getString(R.string.search_empty)
            } else {
                getString(R.string.search_no_results)
            }
            visibility = if (filteredProducts.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}
