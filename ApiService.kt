package com.example.e_comm

import retrofit2.http.GET
import retrofit2.http.Path
interface ApiService {

    @GET("products")
    suspend fun getProducts(): List<Product>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Product
}

