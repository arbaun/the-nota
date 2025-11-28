package com.example.thenotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thenotes.data.AppContainer
import com.example.thenotes.data.Nota
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NotaViewModel(private val appContainer: AppContainer): ViewModel() {
    val allNotaStream: Flow<List<Nota>> =appContainer.notaRepository.getAllNotaStream()
    fun addNota(nota: Nota)= viewModelScope.launch(Dispatchers.IO){
        appContainer.notaRepository.insertNota(nota)
    }
}
