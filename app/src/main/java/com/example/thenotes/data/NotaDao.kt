package com.example.thenotes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNota(nota: Nota): Long
    @Update
    suspend fun updateNota(nota: Nota)
    @Delete
    suspend fun deleteNota(nota: Nota)
    @Query("select * from nota")
    fun getAllNota(): Flow<List<Nota>>
    @Query("select * from nota where id= :id")
    fun getNotaById(id: Int): Flow<Nota>


}