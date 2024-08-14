package com.daniel.personalapplication.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.daniel.personalapplication.R
import com.daniel.personalapplication.shared_pref.MySession

class RegisterActivity : AppCompatActivity() {

    private lateinit var btnRegister: Button // Deklarasi
    private lateinit var etUsername: EditText // Deklarasi
    private lateinit var etPhone: EditText // Deklarasi
    private lateinit var etDesc: EditText // Deklarasi
    private lateinit var etPassword: EditText // Deklarasi
    private lateinit var etConfirmPassword: EditText // Deklarasi
    private lateinit var tvBack: TextView // Deklarasi

    private lateinit var mySession: MySession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnRegister = findViewById(R.id.btn_register)
        etUsername = findViewById(R.id.et_username)
        etDesc = findViewById(R.id.et_desc)
        etPhone = findViewById(R.id.et_phone)
        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        tvBack = findViewById(R.id.tv_back_login)

        mySession = MySession(this)

        btnRegister.setOnClickListener {
            register()
        }
        tvBack.setOnClickListener { finish() }


    }

    private fun register() {
        val name = etUsername.text.toString().trim() // " Dan iel " -> "Dan iel"
        val phone = etPhone.text.toString().trim()
        val desc = etDesc.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty() || desc.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Isi form terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Cek Password terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        mySession.saveData("name", name)
        mySession.saveData("phone", phone)
        mySession.saveData("desc", desc)
        mySession.saveData("password", password)

        finish()
    }
}