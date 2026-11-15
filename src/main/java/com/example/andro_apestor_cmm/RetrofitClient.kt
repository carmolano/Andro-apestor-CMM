package com.example.andro_apestor_cmm

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>


    @POST("api/users")
    fun register(
        @Header("Authorization") token: String,
        @Body request: RegisterRequest
    ): Call<Any>
}

object RetrofitClient {
    private const val BASE_URL = "https://users-management-api-ywek.onrender.com/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}