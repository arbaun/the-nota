package com.example.thenotes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCustomer(customer:Customer):Long

    @Query("Select * from customer")
     fun getAllCustomer(): Flow<List<Customer>>

    @Query("select * from customer where id=:id")
     fun getCustomerById(id: Int):Flow<Customer>

     @Query("select * from customer where nama like '%' || :nama || '%'")
     fun getListCustomerByNama(nama: String): Flow<List<Customer>>

    @Update
    suspend fun update(customer:Customer)
    @Delete
    suspend fun delete(customer:Customer)
}