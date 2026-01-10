package com.arbadev.thenota

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arbadev.thenota.data.ItemNota
import com.arbadev.thenota.data.ItemNotaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ItemNotaViewModel(private val itemNotaRepository: ItemNotaRepository): ViewModel() {
    fun getItemByNotaIdStream(notaId: Int) : Flow<List<ItemNota>>{
        return itemNotaRepository.getItemByNotaIdStream(notaId)
    }

    fun getItemNotaByIdStream(id: Int): Flow<ItemNota>{
        return itemNotaRepository.getItemByIdStream(id)
    }
    fun inserItem(itemNota: ItemNota)= viewModelScope.launch(Dispatchers.IO){
        itemNotaRepository.insertItemNota(itemNota)
    }
    fun deleteItem(itemNota: ItemNota)= viewModelScope.launch(Dispatchers.IO){
        itemNotaRepository.deleteItemNota(itemNota)
    }
    fun updateItem(itemNota: ItemNota)= viewModelScope.launch(Dispatchers.IO){
        itemNotaRepository.updateItemNota(itemNota)
    }

    fun calculateSubtotal(harga:Double, qty:Int):Double{
        return harga*qty
    }
}