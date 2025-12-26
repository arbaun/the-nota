package com.example.thenotes.data

import kotlinx.coroutines.flow.Flow

interface InterfaceProdukRepository {
    fun getAllProdukStream(): Flow<List<Produk>>
    fun getProdukByIdStream(id : Int): Flow<Produk>
    fun getListProdukByNameStream(search: String): Flow<List<Produk>>
    suspend fun insertProduk(produk: Produk): Long
    suspend fun updateProduk(produk: Produk)
    suspend fun deleteProduk(produk: Produk)

}