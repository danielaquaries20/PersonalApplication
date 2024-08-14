package com.daniel.personalapplication.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.daniel.personalapplication.R
import com.daniel.personalapplication.data.database.AppDatabase
import com.daniel.personalapplication.data.database.Note
import com.daniel.personalapplication.databinding.ActivityAddNoteBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding

    private lateinit var myDatabase: AppDatabase

    private var idNote: Int = 0
    private lateinit var oldNote: Note

    private lateinit var photoFile: File

    private var photoStr: String = ""

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
                photoStr = ProfileActivity().bitmapToString(takenImage)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note)
//        setContentView(R.layout.activity_add_note)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        myDatabase = AppDatabase.getDatabase(this)

        idNote = intent.getIntExtra("id_note", 0)

        binding.isEdit = true
        if (idNote != 0) {
            binding.btnDelete.isVisible = true
            binding.btnAdd.text = "Edit"
            binding.isEdit = false
        }


        photoFile = try {
            createImageFile()
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

        binding.ivBack.setOnClickListener { finish() }

        binding.ivPhoto.setOnClickListener {
            openGallery()
        }

        binding.btnAdd.setOnClickListener {
            if (idNote != 0) {
                editNote(true)
            } else {
                addNote()
            }
        }

        binding.btnDelete.setOnClickListener {
            deleteNote()
        }

        binding.btnSave.setOnClickListener {
            saveEdit()
            editNote(false)
        }

        binding.btnCancel.setOnClickListener {
            editNote(false)

            binding.etTitle.setText(oldNote.title)
            binding.etNote.setText(oldNote.note)
            photoStr = ""
        }

        observe()
    }

    private fun saveEdit() {
        val title = binding.etTitle.text.toString().trim()
        val note = binding.etNote.text.toString().trim()

        if (title.isEmpty() || note.isEmpty()) {
            Toast.makeText(this, "Isi form yang kosong", Toast.LENGTH_SHORT).show()
            return
        }

        if (title == oldNote.title && note == oldNote.note && photoStr.isEmpty()) {
            Toast.makeText(this, "Tidak ada perubahan data", Toast.LENGTH_SHORT).show()
            return
        }

        val newNote : Note = if (photoStr.isEmpty()) {
            oldNote.copy(title = title, note = note)
        } else {
            oldNote.copy(title = title, note = note, photo = photoStr)

        }
        lifecycleScope.launch {
            myDatabase.noteDao().update(newNote)
        }

        finish()
    }

    private fun deleteNote() {
        lifecycleScope.launch {
            myDatabase.noteDao().delete(oldNote)
            finish()
        }
    }

    private fun editNote(isEdit: Boolean) {
        binding.btnAdd.isVisible = !isEdit
        binding.btnDelete.isVisible = !isEdit
        binding.btnSave.isVisible = isEdit
        binding.btnCancel.isVisible = isEdit
        binding.isEdit = isEdit
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    myDatabase.noteDao().getItem(idNote).collect { data ->
                        data?.let {
                            oldNote = it
                            binding.etTitle.setText(it.title)
                            binding.etNote.setText(it.note)

                            val photo = ProfileActivity().stringToBitmap(it.photo)
                            photo?.let { image ->
                                binding.ivPhoto.setImageBitmap(image)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addNote() {
        val title = binding.etTitle.text.toString().trim()
        val note = binding.etNote.text.toString().trim()

        if (title.isEmpty() || note.isEmpty()) {
            Toast.makeText(this, "Isi form yang kosong", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            myDatabase.noteDao().insert(Note(title, note, photoStr))
        }

        finish()
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
}