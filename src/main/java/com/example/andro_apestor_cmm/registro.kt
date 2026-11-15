package com.example.andro_apestor_cmm

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        val txtid = findViewById<EditText>(R.id.txtid)
        val txtnombre = findViewById<EditText>(R.id.txtnombre)
        val txtemail = findViewById<EditText>(R.id.txtemail)
        val txtPassw = findViewById<EditText>(R.id.txt_passw)
        val btnGuardarData = findViewById<ToggleButton>(R.id.btnGuardarData)
        val btnatras = findViewById<ToggleButton>(R.id.btnatras)

        btnGuardarData.setOnClickListener {
            val idUsuario = txtid.text.toString().trim()
            val nombre = txtnombre.text.toString().trim()
            val email = txtemail.text.toString().trim()
            val password = txtPassw.text.toString().trim()

            if (idUsuario.isEmpty() || nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            val token = prefs.getString("jwt_token", "") ?: ""

            if (token.isEmpty()) {
                Toast.makeText(this, "Inicia sesión como admin primero para registrar usuarios", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            btnGuardarData.isEnabled = false
            Toast.makeText(this, "Guardando usuario en TiDB...", Toast.LENGTH_SHORT).show()

            val nuevoUsuario = RegisterRequest(
                id = idUsuario,
                name = nombre,
                email = email,
                password = password,
                role = "USER"
            )

            RetrofitClient.instance.register("Bearer $token", nuevoUsuario).enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    btnGuardarData.isEnabled = true

                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@registro,
                            "¡Usuario registrado con éxito en TiDB!",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@registro,
                            "Error al registrar (Código: ${response.code()})",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    btnGuardarData.isEnabled = true
                    Toast.makeText(
                        this@registro,
                        "Sin respuesta del servidor: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }

        btnatras.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}