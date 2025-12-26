package com.example.thenotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thenotes.data.Setting
import com.example.thenotes.data.SettingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingViewModel(private val dataSetting: SettingRepository): ViewModel() {
    val settingList = dataSetting.getAllSettingsStream()
    fun addSetting(setting: Setting) = viewModelScope.launch(Dispatchers.IO){
        dataSetting.insertSetting(setting)
    }
    fun editSetting(setting: Setting)= viewModelScope.launch(Dispatchers.IO){
        dataSetting.updateSetting(setting = setting)
    }
    fun deleteSetting(setting: Setting)= viewModelScope.launch(Dispatchers.IO){
        dataSetting.deleteSetting(setting)
    }
}