package com.example.andro_apestor_cmm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val txtEmail1 = findViewById<EditText>(R.id.txtEmail1)
        val txtpass1 = findViewById<EditText>(R.id.txtpass1)
        val btnlogin = findViewById<Button>(R.id.btnlogin)
        val btnregistro = findViewById<Button>(R.id.btnregistro)
        val btnRecuperar_contrasena = findViewById<Button>(R.id.btnrecuperar_contrasena)

        btnlogin.setOnClickListener {
            val email = txtEmail1.text.toString().trim()
            val password = txtpass1.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnlogin.isEnabled = false
            Toast.makeText(this, "Conectando con el servidor...", Toast.LENGTH_SHORT).show()

            val request = LoginRequest(email = email, password = password)

            RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    btnlogin.isEnabled = true

                    if (response.isSuccessful && response.body() != null) {
                        val tokenRecibido = response.body()?.accessToken ?: response.body()?.token ?: ""

                        val prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                        prefs.edit().putString("jwt_token", tokenRecibido).apply()

                        Toast.makeText(this@login, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@login, menu::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@login,
                            "Credenciales incorrectas (Código: ${response.code()})",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    btnlogin.isEnabled = true
                    Toast.makeText(
                        this@login,
                        "Error de red o servidor iniciando. Intenta de nuevo: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }

        btnregistro.setOnClickListener {
            val intent = Intent(this, registro::class.java)
            startActivity(intent)
        }

        btnRecuperar_contrasena.setOnClickListener {
            val intent = Intent(this, Recovery_Email::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }
}