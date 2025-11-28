package com.example.thenotes.data

import kotlinx.coroutines.flow.Flow

interface InterfaceItemNotaRepository {
    fun getItemByNotaIdStream(nota_id:Int): Flow<List<ItemNota>>
    suspend fun insertItemNota(itemNota: ItemNota): Long
    suspend fun updateItemNota(itemNota: ItemNota)
    suspend fun deleteItemNota(itemNota: ItemNota)
}