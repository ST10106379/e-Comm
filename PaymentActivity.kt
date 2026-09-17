package com.example.e_comm

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.e_comm.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productTitle = intent.getStringExtra("PRODUCT_TITLE") ?: "Item"
        val totalPrice = intent.getDoubleExtra("TOTAL_PRICE", 0.0)

        binding.tvOrderSummary.text = productTitle
        binding.tvOrderTotal.text = "Total: R%.2f".format(totalPrice)

        binding.btnConfirmPayment.setOnClickListener { attemptPayment() }
    }

    private fun attemptPayment() {
        val address = binding.etShippingAddress.text.toString().trim()
        val cardName = binding.etCardName.text.toString().trim()
        val cardNumber = binding.etCardNumber.text.toString().trim()
        val expiry = binding.etCardExpiry.text.toString().trim()
        val cvv = binding.etCardCvv.text.toString().trim()

        if (address.isEmpty() || cardName.isEmpty() || cardNumber.isEmpty() ||
            expiry.isEmpty() || cvv.isEmpty()
        ) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (cardNumber.length < 12) {
            Toast.makeText(this, "Enter a valid card number", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show()
        finish()
    }
}
