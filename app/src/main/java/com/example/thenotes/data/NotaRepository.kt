package com.example.thenotes.data

import kotlinx.coroutines.flow.Flow

class NotaRepository(private val notaDao: NotaDao): InterfaceNotaRepository {
    override fun getAllNotaStream(): Flow<List<Nota>> = notaDao.getAllNota()

    override fun getNotaById(id: Int): Flow<Nota> = notaDao.getNotaById(id)

    override suspend fun insertNota(nota: Nota): Long = notaDao.insertNota(nota)

    override suspend fun updateNota(nota: Nota) = notaDao.updateNota(nota)

    override suspend fun deleteNota(nota: Nota) = notaDao.deleteNota(nota)
}