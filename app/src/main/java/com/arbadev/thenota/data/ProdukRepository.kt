package com.arbadev.thenota.data

import kotlinx.coroutines.flow.Flow

class ProdukRepository(private val produkDao: ProdukDao) : InterfaceProdukRepository {
    override fun getAllProdukStream(): Flow<List<Produk>> = produkDao.getAllProduct()

    override fun getProdukByIdStream(id: Int): Flow<Produk> = produkDao.getProductById(id)

    override fun getListProdukByNameStream(search: String): Flow<List<Produk>> = produkDao.getListProductBYName(search)

    override suspend fun insertProduk(produk: Produk): Long = produkDao.insertProduk(produk = produk)

    override suspend fun updateProduk(produk: Produk) = produkDao.updateProduk(produk)

    override suspend fun deleteProduk(produk: Produk) = produkDao.deleteProduk(produk)

}