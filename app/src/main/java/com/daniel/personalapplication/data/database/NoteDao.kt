package com.daniel.personalapplication.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(data: Note)

    @Update
    suspend fun update(data: Note)

    @Delete
    suspend fun delete(data: Note)

    @Query("SELECT * from note WHERE id = :id")
    fun getItem(id: Int): Flow<Note?>

    @Query("SELECT * from note ORDER BY title ASC")
    fun getAllItems(): Flow<List<Note>?>

}