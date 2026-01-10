package com.arbadev.thenota

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arbadev.thenota.data.Setting
import com.arbadev.thenota.data.SettingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

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
    fun toJson(setting: Setting):String{
        val jsob = JSONObject()
        jsob.put("t","setting")
        jsob.put("i", setting.id)
        jsob.put("n",setting.nama_toko)
        jsob.put("a", setting.alamat_toko)
        jsob.put("c", setting.catatan_kaki)
        jsob.put("u",setting.uri_logo)
        return jsob.toString()
    }
    fun toDataSetting(setjson: String): Setting{
        val jsob = JSONObject(setjson)
        val id = jsob.getInt("i")
        val nama_toko = jsob.getString("n")
        val alamat_toko = jsob.getString("a")
        val catatan_kaki = jsob.getString("c")
        //val uri_logo = jsob.getString("u")
        return Setting(id, nama_toko,alamat_toko, "kosong",catatan_kaki)
    }
}