package com.example.andro_apestor_cmm

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class registro_usuarios : AppCompatActivity() {

    private lateinit var lvUsuarios: ListView
    private var listaUsuarios: List<UserItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_usuarios)

        lvUsuarios = findViewById(R.id.LvUsuarios)
        val btnSalir = findViewById<ToggleButton>(R.id.btnatras_menu1)
        val btnActualizar = findViewById<ToggleButton>(R.id.btnVolver)

        btnSalir.setOnClickListener {
            finish()
        }

        btnActualizar.setOnClickListener {
            Toast.makeText(this, "Actualizando lista...", Toast.LENGTH_SHORT).show()
            cargarUsuarios()
        }

        lvUsuarios.setOnItemClickListener { _, _, position, _ ->
            val usuarioSeleccionado = listaUsuarios[position]
            mostrarOpcionesUsuario(usuarioSeleccionado)
        }

        cargarUsuarios()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun obtenerToken(): String {
        val prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return prefs.getString("jwt_token", "") ?: ""
    }

    private fun cargarUsuarios() {
        val token = obtenerToken()
        if (token.isEmpty()) {
            Toast.makeText(this, "Sesión expirada. Inicia sesión de nuevo.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.instance.getUsers("Bearer $token").enqueue(object : Callback<List<UserItem>> {
            override fun onResponse(call: Call<List<UserItem>>, response: Response<List<UserItem>>) {
                if (response.isSuccessful && response.body() != null) {
                    listaUsuarios = response.body()!!
                    val textos = listaUsuarios.map {
                        "ID: ${it.id}  |  ${it.name}\n${it.email} (${it.role ?: "USER"})"
                    }
                    val adapter = ArrayAdapter(
                        this@registro_usuarios,
                        android.R.layout.simple_list_item_1,
                        textos
                    )
                    lvUsuarios.adapter = adapter
                } else {
                    Toast.makeText(
                        this@registro_usuarios,
                        "Error al cargar usuarios (Código: ${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<UserItem>>, t: Throwable) {
                Toast.makeText(this@registro_usuarios, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarOpcionesUsuario(usuario: UserItem) {
        val opciones = arrayOf("Editar usuario", "Eliminar usuario")
        AlertDialog.Builder(this)
            .setTitle("${usuario.name} (${usuario.id})")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> mostrarDialogoEditar(usuario)
                    1 -> confirmarEliminar(usuario)
                }
            }
            .show()
    }

    private fun mostrarDialogoEditar(usuario: UserItem) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 8)
        }

        val etNombre = EditText(this).apply {
            hint = "Nombre"
            setText(usuario.name)
        }
        val etEmail = EditText(this).apply {
            hint = "Email"
            setText(usuario.email)
        }
        val etRole = EditText(this).apply {
            hint = "Rol (ADMIN o USER)"
            setText(usuario.role ?: "USER")
        }

        layout.addView(etNombre)
        layout.addView(etEmail)
        layout.addView(etRole)

        AlertDialog.Builder(this)
            .setTitle("Editar Usuario ${usuario.id}")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val actualizado = UserItem(
                    id = usuario.id,
                    name = etNombre.text.toString().trim(),
                    email = etEmail.text.toString().trim(),
                    role = etRole.text.toString().trim().uppercase()
                )
                RetrofitClient.instance.updateUser("Bearer ${obtenerToken()}", usuario.id, actualizado)
                    .enqueue(object : Callback<Any> {
                        override fun onResponse(call: Call<Any>, response: Response<Any>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@registro_usuarios, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                                cargarUsuarios()
                            } else {
                                Toast.makeText(
                                    this@registro_usuarios,
                                    "Error al actualizar: ${response.code()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<Any>, t: Throwable) {
                            Toast.makeText(this@registro_usuarios, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmarEliminar(usuario: UserItem) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Usuario")
            .setMessage("¿confirmame que deseas eliminar a ${usuario.name}?")
            .setPositiveButton("Eliminar") { _, _ ->
                RetrofitClient.instance.deleteUser("Bearer ${obtenerToken()}", usuario.id)
                    .enqueue(object : Callback<Any> {
                        override fun onResponse(call: Call<Any>, response: Response<Any>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@registro_usuarios, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                                cargarUsuarios()
                            } else {
                                Toast.makeText(
                                    this@registro_usuarios,
                                    "Error al eliminar: ${response.code()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<Any>, t: Throwable) {
                            Toast.makeText(this@registro_usuarios, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}