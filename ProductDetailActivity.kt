package com.example.e_comm

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.e_comm.databinding.ActivityProductDetailBinding
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    private var quantity = 1
    private var currentProduct: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getIntExtra("PRODUCT_ID", -1)
        if (productId == -1) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchProductDetail(productId)

        binding.btnIncreaseQty.setOnClickListener {
            quantity++
            binding.tvQuantity.text = quantity.toString()
        }

        binding.btnDecreaseQty.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.tvQuantity.text = quantity.toString()
            }
        }

        binding.btnAddToCart.setOnClickListener {
            Toast.makeText(this, "Added $quantity to cart", Toast.LENGTH_SHORT).show()
        }

        binding.btnBuyNow.setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("PRODUCT_TITLE", currentProduct?.title)
            val usdTotal = (currentProduct?.price ?: 0.0) * quantity
            intent.putExtra("TOTAL_PRICE", CurrencyUtils.convertUsdToZar(usdTotal))
            startActivity(intent)
        }
    }

    private fun fetchProductDetail(id: Int) {
        lifecycleScope.launch {
            try {
                val product = RetrofitClient.apiService.getProductById(id)
                currentProduct = product
                binding.tvDetailName.text = product.title
                binding.tvDetailPrice.text = CurrencyUtils.formatPrice(product.price)
                binding.tvDetailDescription.text = product.description
                binding.ivDetailImage.load(product.image)
            } catch (e: Exception) {
                Toast.makeText(
                    this@ProductDetailActivity,
                    "Failed to load product: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
