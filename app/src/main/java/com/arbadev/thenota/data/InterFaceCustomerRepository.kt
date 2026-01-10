package com.arbadev.thenota.data

import kotlinx.coroutines.flow.Flow

interface InterFaceCustomerRepository {
    fun getAllCustomerStream(): Flow<List<Customer>>
    fun getCustomerByIdStream(id: Int): Flow<Customer>
    fun getListCustomerByNamaStream(nama: String): Flow<List<Customer>>
    suspend fun insertCustomer(customer: Customer): Long
    suspend fun updateCustomer(customer: Customer)
    suspend fun deleteCustomer(customer: Customer)
}