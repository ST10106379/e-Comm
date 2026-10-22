package com.example.e_comm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_comm.databinding.ActivityProductListBinding
import com.example.e_comm.local.AppDatabase
import com.example.e_comm.local.toEntity
import com.example.e_comm.local.toProduct
import com.example.e_comm.util.NetworkMonitor
import kotlinx.coroutines.launch

class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding
    private lateinit var adapter: ProductAdapter
    private var allProducts: List<Product> = emptyList()
    private lateinit var db: AppDatabase
    private var stopObservingReconnect: (() -> Unit)? = null

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(this, "Notifications disabled — enable later in Settings if you'd like updates", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)

        setupRecyclerView()
        setupSearch()
        loadProducts()
        requestNotificationPermissionIfNeeded()

        binding.btnGoToSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.btnGoToSocials.setOnClickListener {
            startActivity(Intent(this, SocialsActivity::class.java))
        }

        // Automatically re-sync with the API the moment connectivity returns
        stopObservingReconnect = NetworkMonitor.observeReconnect(this) {
            runOnUiThread {
                Toast.makeText(this, "Back online — syncing products...", Toast.LENGTH_SHORT).show()
                fetchFromApiAndCache()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopObservingReconnect?.invoke()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(emptyList()) { product ->
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT_ID", product.id)
            startActivity(intent)
        }
        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        binding.rvProducts.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearchProducts.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                val filtered = if (query.isEmpty()) {
                    allProducts
                } else {
                    allProducts.filter { it.title.contains(query, ignoreCase = true) }
                }
                adapter.updateProducts(filtered)
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    /**
     * Decides whether to hit the network or fall back to the local cache,
     * depending on current connectivity.
     */
    private fun loadProducts() {
        if (NetworkMonitor.isOnline(this)) {
            fetchFromApiAndCache()
        } else {
            Toast.makeText(this, "You're offline — showing saved products", Toast.LENGTH_LONG).show()
            loadFromCache()
        }
    }

    private fun fetchFromApiAndCache() {
        lifecycleScope.launch {
            try {
                val products = RetrofitClient.apiService.getProducts()
                allProducts = products
                adapter.updateProducts(products)

                // Cache for offline use
                db.productDao().clearAll()
                db.productDao().insertAll(products.map { it.toEntity() })
            } catch (e: Exception) {
                Toast.makeText(
                    this@ProductListActivity,
                    "Couldn't reach the server — showing saved products",
                    Toast.LENGTH_LONG
                ).show()
                loadFromCache()
            }
        }
    }

    private fun loadFromCache() {
        lifecycleScope.launch {
            val cached = db.productDao().getAllCached().map { it.toProduct() }
            if (cached.isEmpty()) {
                Toast.makeText(
                    this@ProductListActivity,
                    "No saved products yet — connect to the internet first",
                    Toast.LENGTH_LONG
                ).show()
            }
            allProducts = cached
            adapter.updateProducts(cached)
        }
    }
}
