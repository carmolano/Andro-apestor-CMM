package com.example.andro_apestor_cmm

import android.R

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val accessToken: String?,
    val token: String?
)

data class RegisterRequest(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val role: String = "admin"
)

data class UserItem(
    val id: String,
    val name: String,
    val email : String,
    val role: String? = "USER",
    val password: String? = null

)



