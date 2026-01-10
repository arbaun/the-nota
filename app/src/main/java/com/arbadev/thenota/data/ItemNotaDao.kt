package com.arbadev.thenota.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemNotaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItemNota(itemNota: ItemNota): Long
    @Update
    suspend fun updateItemNota(itemNota: ItemNota)
    @Delete
    suspend fun deleteItemNota(itemNota: ItemNota)
    @Query("select * from itemnota where nota_id= :nota_id")
    fun getItemNotaByNotaId(nota_id : Int): Flow<List<ItemNota>>
    @Query("select * from itemnota where id= :id")
    fun getItemById(id: Int) :Flow<ItemNota>
}