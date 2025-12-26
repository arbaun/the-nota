package com.example.thenotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thenotes.data.Produk
import com.example.thenotes.data.ProdukRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProdukViewModel(private val dataProduk: ProdukRepository): ViewModel() {
    val getAllProdukStream = dataProduk.getAllProdukStream()
    fun getProdukById(id:Int) : Flow<Produk>{
        return dataProduk.getProdukByIdStream(id)
    }
    fun addProduk(produk: Produk) = viewModelScope.launch(Dispatchers.IO){
        dataProduk.insertProduk(produk = produk)
    }
    fun updateProduk(produk: Produk)= viewModelScope.launch(Dispatchers.IO){
        dataProduk.updateProduk(produk = produk)
    }
    fun deleteProduk(produk: Produk)=viewModelScope.launch(Dispatchers.IO){
        dataProduk.deleteProduk(produk = produk)
    }
    fun getAllProdukBySearch(search: String): Flow<List<Produk>>{
        return dataProduk.getListProdukByNameStream(search)
    }
}