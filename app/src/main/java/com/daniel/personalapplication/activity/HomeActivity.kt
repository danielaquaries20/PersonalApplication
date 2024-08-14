package com.daniel.personalapplication.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.daniel.personalapplication.R
import com.daniel.personalapplication.activity.recycler_view.AdapterRvNote
import com.daniel.personalapplication.data.database.AppDatabase
import com.daniel.personalapplication.data.database.Note
import com.daniel.personalapplication.databinding.ActivityHomeBinding
import com.daniel.personalapplication.shared_pref.MySession
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private var name: String? = null
    private var phone: String? = null

    private lateinit var mySession: MySession

    private lateinit var adapter: AdapterRvNote

    private val data = ArrayList<Note>()

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = AppDatabase.getDatabase(this)

        adapter = AdapterRvNote(this) { position, data ->
            val destination = Intent(this, AddNoteActivity::class.java).apply {
                putExtra("id_note", data.id)
            }
            startActivity(destination)
        }
        binding.rvData.adapter = adapter

        lifecycleScope.launch {
            /*database.noteDao().insert(Note("Test 2", "lorem ipsum dolor sit amet..."))
            database.noteDao().insert(Note("Test 3", "lorem ipsum dolor sit amet..."))
            database.noteDao().insert(Note("Test 4", "lorem ipsum dolor sit amet..."))*/
//            database.noteDao().insert(Note("Pergi ke Bali", "Di Bali sungguh indah"))
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    database.noteDao().getAllItems().collect {
                        it?.let { list ->
                            data.clear()
                            data.addAll(list)
                            adapter.setData(data)
                        }
                    }
                }
            }
        }


        /*val listData = arrayOf(
            Note("Pergi ke Bali", "Di Bali sungguh indah"),
            Note("Pergi ke Jakarta", "Di Jakarta sering macet dan banyak polusi udara"),
            Note("Pergi ke Surabaya", "Monumen ikan Sura dan Buayanya sungguh ikonik"),
            Note("Pergi ke Bali", "Di Bali sungguh indah"),
            Note("Pergi ke Jakarta", "Di Jakarta sering macet dan banyak polusi udara"),
            Note(
                "Pergi ke Surabaya",
                "Monumen ikan Sura dan Buayanya sungguh ikonik Monumen ikan Sura dan Buayanya sungguh ikonik Monumen ikan Sura dan Buayanya sungguh ikonik Monumen ikan Sura dan Buayanya sungguh ikonik"
            ),
            Note("Pergi ke Bali", "Di Bali sungguh indah"),
            Note("Pergi ke Jakarta", "Di Jakarta sering macet dan banyak polusi udara"),
            Note("Pergi ke Surabaya", "Monumen ikan Sura dan Buayanya sungguh ikonik"),
        )*/

        /*mySession = MySession(this)

        name = mySession.getData("name")
        phone = mySession.getData("phone")
        val photo = ProfileActivity().stringToBitmap(mySession.getData("photo") ?: "")
        photo?.let { binding.ivPhoto.setImageBitmap(photo) }
        binding.tvUsername.text = name*/

        val intentProfile = Intent(this, ProfileActivity::class.java)

        binding.ivPhoto.setOnClickListener {
            startActivity(intentProfile)
        }

        binding.tvUsername.setOnClickListener {
            startActivity(intentProfile)
        }

        binding.ivLogout.setOnClickListener {
            mySession.saveData("login", "")
            finishAffinity()
        }

        binding.btnAdd.setOnClickListener {
            val destination = Intent(this, AddNoteActivity::class.java)
            startActivity(destination)
        }
    }

    override fun onStart() {
        super.onStart()
        initData()
    }

    private fun initData() {
        mySession = MySession(this)

        name = mySession.getData("name")
        phone = mySession.getData("phone")
        val photo = ProfileActivity().stringToBitmap(mySession.getData("photo") ?: "")
        photo?.let { binding.ivPhoto.setImageBitmap(photo) }
        binding.tvUsername.text = name
    }
}