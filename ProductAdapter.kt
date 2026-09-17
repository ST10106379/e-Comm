package com.example.e_comm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class ProductAdapter(
    private var products: List<Product>,
    private val onProductClick: (Product) -> Unit,
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val ivThumb: ImageView = itemView.findViewById(R.id.ivProductThumb)
            val tvName: TextView = itemView.findViewById(R.id.tvProductName)
            val tvPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product, parent, false)
            return ProductViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            val product = products[position]
            holder.tvName.text = product.title
            holder.tvPrice.text = CurrencyUtils.formatPrice(product.price)
            holder.ivThumb.load(product.image)
            holder.itemView.setOnClickListener { onProductClick(product) }
        }

        override fun getItemCount(): Int = products.size

        fun updateProducts(newProducts: List<Product>) {
            products = newProducts
            notifyDataSetChanged()
        }
}