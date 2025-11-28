package com.example.thenotes.data

import kotlinx.coroutines.flow.Flow

class CustomerRepository(private val customerDao: CustomerDao): InterFaceCustomerRepository {
    override fun getAllCustomerStream(): Flow<List<Customer>> = customerDao.getAllCustomer()
    override fun getCustomerByIdStream(id: Int): Flow<Customer> = customerDao.getCustomerById(id)

    override suspend fun insertCustomer(customer: Customer): Long = customerDao.insertCustomer(customer)

    override suspend fun updateCustomer(customer: Customer) = customerDao.update(customer)

    override suspend fun deleteCustomer(customer: Customer) = customerDao.delete(customer)
}