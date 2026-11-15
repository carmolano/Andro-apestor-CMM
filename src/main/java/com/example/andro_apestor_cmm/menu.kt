package com.example.andro_apestor_cmm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        val btnVerRegistroApps = findViewById<ToggleButton>(R.id.btnVerRegistroapps)
        btnVerRegistroApps.setOnClickListener {
            val intent = Intent(this@menu, registro_aplicaciones::class.java)
            startActivity(intent)
        }

        val btnVerRegistroUsuarios = findViewById<ToggleButton>(R.id.btnVerRegistroUsuarios)
        btnVerRegistroUsuarios.setOnClickListener {
            val intent = Intent(this@menu, registro_usuarios::class.java)
            startActivity(intent)
        }

        val btnatras_menu = findViewById<Button>(R.id.btnatras_menu)
        btnatras_menu.setOnClickListener {
            val intent = Intent(this@menu, registro::class.java)
            startActivity(intent)
            finish()
        }

        val btnatras_login = findViewById<Button>(R.id.btnatras_login)
        btnatras_login.setOnClickListener {
            val intent = Intent(this@menu, login::class.java)
            startActivity(intent)
            finish()
        }

        val btnSensores = findViewById<Button>(R.id.btnSensores)

        btnSensores.setOnClickListener {
            startActivity(Intent(this, SensoresActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}