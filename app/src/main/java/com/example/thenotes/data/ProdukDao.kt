package com.example.thenotes.data

import android.icu.text.StringSearch
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdukDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProduk(produk: Produk): Long
    @Update
    suspend fun updateProduk(produk: Produk)
    @Delete
    suspend fun deleteProduk(produk: Produk)
    @Query("select * from produk")
    fun getAllProduct(): Flow<List<Produk>>
    @Query("select * from produk where id=:id")
    fun getProductById(id:Int): Flow<Produk>
    @Query("select * from produk where nama_produk LIKE '%' || :search ||'%'")
    fun getListProductBYName(search: String): Flow<List<Produk>>
}