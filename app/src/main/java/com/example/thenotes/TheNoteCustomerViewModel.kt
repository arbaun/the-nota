package com.example.thenotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thenotes.data.Customer
import com.example.thenotes.data.CustomerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TheNoteCustomerViewModel(private val data: CustomerRepository): ViewModel() {
    val customerList : Flow<List<Customer>> = data.getAllCustomerStream()
    fun addCustomer(customer: Customer) = viewModelScope.launch(Dispatchers.IO){
        data.insertCustomer(customer)
    }
    fun deleteCustomer(customer: Customer)= viewModelScope.launch(Dispatchers.IO){
        data.deleteCustomer(customer)
    }
    fun updateCustomer(customer: Customer)= viewModelScope.launch(Dispatchers.IO){
        data.updateCustomer(customer)
    }
    fun getCustomerById(id: Int): Flow<Customer>{
        return data.getCustomerByIdStream(id)
    }

}