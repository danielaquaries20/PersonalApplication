package com.daniel.personalapplication.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.daniel.personalapplication.R
import com.daniel.personalapplication.shared_pref.MySession

class MainActivity : AppCompatActivity() {

    //String, Char, Int, Boolean, Long, Double, Float
    //String -> "Text"
    //Char -> 'T'
    //Int -> 1,2,3,0, -2,-3
    //Boolean -> True (1) atau False(0)
    //Long -> 1231434L
    //Double & Float -> 9.9 , 9.9f

    //private lateinit var btnNext: TextView // Deklarasi

    private lateinit var mySession: MySession


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mySession = MySession(this)

        val sesionLogin = mySession.getData("login")
        //Inisiasi
        //btnNext = findViewById(R.id.tv_app)

//         btnNext.setOnClickListener {
//             //Perpindahan antar activity
//             val intent = Intent(this, LoginActivity::class.java)
//             startActivity(intent)
//         }

        /*Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 1500)*/

        Handler(Looper.getMainLooper()).postDelayed({
            if (sesionLogin.isNullOrEmpty() || sesionLogin == "-") {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, 1000)
    }
}