package com.arbadev.thenota.data

import kotlinx.coroutines.flow.Flow

class NotaRepository(private val notaDao: NotaDao): InterfaceNotaRepository {
    override fun getAllNotaStream(): Flow<List<Nota>> = notaDao.getAllNota()

    override fun getNotaByNameStream(name: String): Flow<Nota> = notaDao.getNotaByName(name)
    override fun getNotaByIdStream(id: Int): Flow<Nota> = notaDao.getNotaById(id)

    override fun getNotaByDatetime(datetime: String): Flow<Nota> = notaDao.getNotaByDatetime(datetime)

    override suspend fun insertNota(nota: Nota): Long = notaDao.insertNota(nota)

    override suspend fun updateNota(nota: Nota) = notaDao.updateNota(nota)

    override suspend fun deleteNota(nota: Nota) = notaDao.deleteNota(nota)
}