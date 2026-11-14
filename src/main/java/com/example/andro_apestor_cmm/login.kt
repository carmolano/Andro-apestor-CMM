package com.example.andro_apestor_cmm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val btnlogin = findViewById<Button>(R.id.btnlogin)
        btnlogin.setOnClickListener {
            val intent = Intent(this, menu::class.java)
            startActivity(intent)
            finish()
        }

        val btnregistro = findViewById<Button>(R.id.btnregistro)
        btnregistro.setOnClickListener {
            val intent = Intent(this, registro::class.java)
            startActivity(intent)
        }

        val btnRecuperar_contrasena = findViewById<Button>(R.id.btnrecuperar_contrasena)
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