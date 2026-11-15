package com.example.andro_apestor_cmm

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>


    @POST("api/users")
    fun register(
        @Header("Authorization") token: String,
        @Body request: RegisterRequest
    ): Call<Any>

    @GET("api/users")
    fun getUsers(@Header("Authorization") token: String): Call<List<UserItem>>

    @PUT("api/users/{id}")
    fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: UserItem
    ): Call<Any>

    @DELETE("api/users/{id}")
    fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") id: String
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