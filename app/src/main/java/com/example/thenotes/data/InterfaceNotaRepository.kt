package com.example.thenotes.data

import kotlinx.coroutines.flow.Flow

interface InterfaceNotaRepository {
    fun getAllNotaStream(): Flow<List<Nota>>
    fun getNotaById(id: Int): Flow<Nota>
    suspend fun insertNota(nota: Nota): Long
    suspend fun updateNota(nota: Nota)
    suspend fun deleteNota(nota: Nota)
}