package com.daniel.personalapplication.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class Note(
    var title: String,
    var note: String,
    var photo: String = "",

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
)
