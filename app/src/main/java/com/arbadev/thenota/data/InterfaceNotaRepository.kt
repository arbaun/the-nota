package com.arbadev.thenota.data

import kotlinx.coroutines.flow.Flow

interface InterfaceNotaRepository {
    fun getAllNotaStream(): Flow<List<Nota>>
    fun getNotaByNameStream(name: String): Flow<Nota>
    fun getNotaByIdStream(id: Int): Flow<Nota>
    fun getNotaByDatetime(datetime: String): Flow<Nota>
    suspend fun insertNota(nota: Nota): Long
    suspend fun updateNota(nota: Nota)
    suspend fun deleteNota(nota: Nota)
}