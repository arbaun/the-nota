package com.arbadev.thenota

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arbadev.thenota.data.Customer
import com.arbadev.thenota.data.CustomerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TheNoteCustomerViewModel(private val data: CustomerRepository): ViewModel() {
    val customerList : Flow<List<Customer>> = data.getAllCustomerStream()
    fun addCustomer(customer: Customer): Long {
        var masukin_data: Long = 0.toLong()
        viewModelScope.launch(Dispatchers.IO) {
            masukin_data = data.insertCustomer(customer)
        }
        return masukin_data
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