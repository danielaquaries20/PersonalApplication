package com.daniel.personalapplication.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.daniel.personalapplication.R
import com.daniel.personalapplication.databinding.ActivityLoginBinding
import com.daniel.personalapplication.shared_pref.MySession

class LoginActivity : AppCompatActivity() {

    /*private lateinit var btnLogin: Button // Deklarasi
    private lateinit var etUsername: EditText // Deklarasi
    private lateinit var etPhone: EditText // Deklarasi
    private lateinit var etPassword: EditText // Deklarasi
    private lateinit var tvRegister: TextView // Deklarasi*/

    private lateinit var binding: ActivityLoginBinding

    private lateinit var mySession: MySession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater) // ViewBinding
//        setContentView(R.layout.activity_login)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /* btnLogin = findViewById(R.id.btn_login)
         etUsername = findViewById(R.id.et_username)
         etPhone = findViewById(R.id.et_phone)
         etPassword = findViewById(R.id.et_password)
         tvRegister = findViewById(R.id.tv_register)*/

        mySession = MySession(this)
        //camelCase
        //snake_case

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        //Perpindahan antar activity
        val name = binding.etUsername.text.toString()
        val phone = binding.etPhone.text.toString()
        val password = binding.etPassword.text.toString()

        if (name.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Isi form terlebih dahulu", Toast.LENGTH_SHORT).show()
        } else {

            val saveName = mySession.getData("name")
            val savePhone = mySession.getData("phone")
            val savePassword = mySession.getData("password")

            if (name != saveName || phone != savePhone || password != savePassword) {
                Toast.makeText(
                    this,
                    "Data tidak ada atau tidak sesui dengan yang didaftarkan",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)

            mySession.saveData("login", "login")
            finish()
        }

    }
}