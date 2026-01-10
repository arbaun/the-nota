package com.arbadev.thenota.data

import kotlinx.coroutines.flow.Flow

class ItemNotaRepository(private val itemNotaDao: ItemNotaDao): InterfaceItemNotaRepository {
    override fun getItemByNotaIdStream(nota_id: Int): Flow<List<ItemNota>> = itemNotaDao.getItemNotaByNotaId(nota_id)
    override fun getItemByIdStream(id: Int): Flow<ItemNota> = itemNotaDao.getItemById(id)

    override suspend fun insertItemNota(itemNota: ItemNota): Long = itemNotaDao.insertItemNota(itemNota)

    override suspend fun updateItemNota(itemNota: ItemNota) = itemNotaDao.updateItemNota(itemNota)

    override suspend fun deleteItemNota(itemNota: ItemNota) = itemNotaDao.deleteItemNota(itemNota)
}