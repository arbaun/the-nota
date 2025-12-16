package com.example.thenotes

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thenotes.data.AppContainer
import com.example.thenotes.data.ItemNota
import com.example.thenotes.data.Nota
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NotaViewModel(private val appContainer: AppContainer): ViewModel() {
    val allNotaStream: Flow<List<Nota>> = appContainer.notaRepository.getAllNotaStream()

    fun getNotaByNameStream(name: String): Flow<Nota>{
        return appContainer.notaRepository.getNotaByNameStream(name)
    }
    fun getNotaByIdStream(id : Int): Flow<Nota> = appContainer.notaRepository.getNotaByIdStream(id)
    fun addNota(nota: Nota) = viewModelScope.launch(Dispatchers.IO) {
        appContainer.notaRepository.insertNota(nota)
    }
    fun deleteNota(nota: Nota)= viewModelScope.launch(Dispatchers.IO){
        appContainer.notaRepository.deleteNota(nota)
    }
    fun updateNota(nota: Nota) = viewModelScope.launch(Dispatchers.IO){
        appContainer.notaRepository.updateNota(nota)
    }
    fun getNotaByDatetimeStream(datetime: String): Flow<Nota> = appContainer.notaRepository.getNotaByDatetime(datetime)
    fun calculateTotal(listItem: List<ItemNota>): Double{
        return listItem.sumOf { it.subtotal }
    }
    fun getTime(): String{
        val calendar = Calendar.getInstance().time
        return calendar.toString()
    }


}
