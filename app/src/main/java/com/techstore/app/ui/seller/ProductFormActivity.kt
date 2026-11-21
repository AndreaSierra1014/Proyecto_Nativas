package com.techstore.app.ui.seller

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.techstore.app.data.model.Product
import com.techstore.app.data.repository.ProductRepository
import com.techstore.app.databinding.ActivityProductFormBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ProductFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductFormBinding
    private val repository = ProductRepository()
    private var imageUri: Uri? = null
    private var editing: Product? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            binding.ivPreview.setImageURI(uri)
        }
    }

    private val takePhoto =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) saveCameraImage(bitmap)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPickImage.setOnClickListener { pickImage.launch("image/*") }
        binding.btnCamera.setOnClickListener { takePhoto.launch(null) }
        binding.btnSave.setOnClickListener { saveProduct() }

        intent.getStringExtra(EXTRA_PRODUCT_ID)?.let(::loadProduct)
    }

    private fun loadProduct(id: String) {
        lifecycleScope.launch {
            try {
                val product = repository.getById(id)
                if (product == null) {
                    Toast.makeText(this@ProductFormActivity, "Producto no encontrado.", Toast.LENGTH_LONG)
                        .show()
                    finish()
                    return@launch
                }
                editing = product
                binding.etName.setText(product.nombre)
                binding.etDescription.setText(product.descripcion)
                binding.etPrice.setText(product.precio.toString())
                binding.etStock.setText(product.stock.toString())
                binding.etCategory.setText(product.categoria)
                if (product.imagenUrl.isNotBlank()) {
                    binding.ivPreview.load(product.imagenUrl)
                }
            } catch (error: Exception) {
                Toast.makeText(
                    this@ProductFormActivity,
                    "No se pudo cargar el producto: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun saveProduct() {
        val name = binding.etName.text?.toString()?.trim().orEmpty()
        val description = binding.etDescription.text?.toString()?.trim().orEmpty()
        val price = binding.etPrice.text?.toString()?.toDoubleOrNull()
        val stock = binding.etStock.text?.toString()?.toIntOrNull()
        val category = binding.etCategory.text?.toString()?.trim().orEmpty()

        if (name.isBlank() || price == null || price < 0 || stock == null || stock < 0 ||
            category.isBlank()
        ) {
            Toast.makeText(
                this,
                "Verifica nombre, precio, stock y categoría.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val base = editing ?: Product(
            sellerId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        )
        val product = base.copy(
            nombre = name,
            descripcion = description,
            precio = price,
            stock = stock,
            categoria = category
        )

        binding.btnSave.isEnabled = false
        lifecycleScope.launch {
            try {
                if (editing == null) {
                    repository.create(product, imageUri)
                } else {
                    repository.update(product, imageUri)
                }
                Toast.makeText(this@ProductFormActivity, "Producto guardado.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            } catch (error: Exception) {
                Toast.makeText(
                    this@ProductFormActivity,
                    "No se pudo guardar el producto: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                binding.btnSave.isEnabled = true
            }
        }
    }

    private fun saveCameraImage(bitmap: Bitmap) {
        try {
            val imageFile = File(cacheDir, "product-${System.currentTimeMillis()}.jpg")
            FileOutputStream(imageFile).use { output ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)) {
                    throw IOException("No se pudo guardar la foto.")
                }
            }
            imageUri = Uri.fromFile(imageFile)
            binding.ivPreview.setImageBitmap(bitmap)
        } catch (error: IOException) {
            Toast.makeText(
                this,
                "No se pudo guardar la foto: ${error.localizedMessage}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "productId"
    }
}
