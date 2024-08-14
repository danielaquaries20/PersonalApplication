package com.daniel.personalapplication.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.daniel.personalapplication.data.model.Person
import com.daniel.personalapplication.R
import com.daniel.personalapplication.databinding.ActivityProfileBinding
import com.daniel.personalapplication.shared_pref.MySession
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private lateinit var mySession: MySession

    private lateinit var photoFile: File

    private var photoStr: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //DataBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.isEdit = false

        mySession = MySession(this)

        binding.user = Person(
            mySession.getData("name"),
            mySession.getData("phone"),
            mySession.getData("desc"),
            mySession.getData("photo") ?: ""
        )

        binding.ivBack.setOnClickListener { finish() }

        binding.btnLogout.setOnClickListener {
            mySession.saveData("login", "")
            finishAffinity() // Menghilangkan semua activity
        }

        binding.ivPhoto.setOnClickListener {
            if (binding.isEdit == true) {
                openGallery()
            }
//            takePhoto()
        }

        binding.btnEdit.setOnClickListener {
            binding.isEdit = true
            it.isVisible = false
            binding.btnLogout.isVisible = false
            binding.btnSave.isVisible = true
            binding.btnCancel.isVisible = true
        }

        binding.btnCancel.setOnClickListener {
            binding.isEdit = false
            it.isVisible = false
            binding.btnLogout.isVisible = true
            binding.btnSave.isVisible = false
            binding.btnEdit.isVisible = true

            binding.etName.setText(binding.user?.name)
            binding.etPhone.setText(binding.user?.phone)
            binding.etDesc.setText(binding.user?.description)
            photoStr = ""
        }

        binding.btnSave.setOnClickListener {
            //Program untuk save data
            saveProfile()

            binding.isEdit = false
            it.isVisible = false
            binding.btnLogout.isVisible = true
            binding.btnCancel.isVisible = false
            binding.btnEdit.isVisible = true
        }

        photoFile = try {
            createImageFile()
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

        if (binding.user?.photo?.isNotEmpty() == true) {
            val photo = stringToBitmap(binding.user?.photo ?: "")
            photo?.let { binding.ivPhoto.setImageBitmap(it) }
        }
    }

    private fun saveProfile() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val desc = binding.etDesc.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Isi form yang kosong", Toast.LENGTH_SHORT).show()
            return
        }

        if (name == binding.user?.name && phone == binding.user?.phone && desc == binding.user?.description && photoStr.isEmpty()) {
            Toast.makeText(this, "Data sama dengan sebelumnya", Toast.LENGTH_SHORT).show()
            return
        }

        if (photoStr.isEmpty()) {
            mySession.saveData("name", name)
            mySession.saveData("phone", phone)
            mySession.saveData("desc", desc)
        } else {
            mySession.saveData("name", name)
            mySession.saveData("phone", phone)
            mySession.saveData("desc", desc)
            mySession.saveData("photo", photoStr)
        }
    }

    private var galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val parcelFileDescriptor = contentResolver.openFileDescriptor(
                    result?.data?.data
                        ?: return@registerForActivityResult, "r"
                )
                val fileDescriptor = parcelFileDescriptor?.fileDescriptor
                val inputStream = FileInputStream(fileDescriptor)

                val outputStream = FileOutputStream(photoFile)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                parcelFileDescriptor?.close()
                val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
                binding.ivPhoto.setImageBitmap(takenImage)
                photoStr = bitmapToString(takenImage)
            }
        }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("PHOTO_", ".jpg", storageDir)
    }

    private var cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
                binding.ivPhoto.setImageBitmap(takenImage)
            }
        }

    private fun takePhoto() {
        val photoUri =
            FileProvider.getUriForFile(
                this,
                "com.daniel.personalapplication.fileprovider",
                photoFile
            )

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }

        try {
            cameraLauncher.launch(cameraIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Cannot use Camera", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    fun bitmapToString(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun stringToBitmap(encodedString: String): Bitmap? {
        return try {
            val byteArray = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }

}