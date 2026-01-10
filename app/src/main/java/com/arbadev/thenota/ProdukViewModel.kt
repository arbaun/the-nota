package com.arbadev.thenota

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arbadev.thenota.data.Produk
import com.arbadev.thenota.data.ProdukRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

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
    fun createJsonString(lp: List<Produk>):String{
        val jsob = JSONObject()
        jsob.put("t","produk")
        val arrayProduk = JSONArray()
        lp.forEach { produk ->
            val proOb = JSONObject()
            proOb.put("i", produk.id)
            proOb.put("n", produk.nama_produk)
            proOb.put("h", produk.harga_produk)
            produk.unit_produk?.let {
                proOb.put("u", it)
            }?:run {
                proOb.put("u", "kosong")
            }
            arrayProduk.put(proOb)
        }
        jsob.put("p", arrayProduk)
        return jsob.toString()
    }
    fun deserJson(js: String): List<Produk>{
        val jsob = JSONObject(js)
        val table  = jsob.getString("t")
            val listoSave = mutableListOf<Produk>()
            val arrProd = jsob.getJSONArray("p")
            for(i in 0 until arrProd.length()) {
                val obj = arrProd.getJSONObject(i)
                val unit = obj.getString("u")
                if (!unit.contentEquals("kosong")){
                    listoSave.add(
                        Produk(
                            id = obj.getInt("i"),
                            nama_produk = obj.getString("n"),
                            harga_produk = obj.getDouble("h"),
                            unit_produk = obj.getString("u")
                        )
                    )
                }else{
                    listoSave.add(
                        Produk(
                            id = obj.getInt("i"),
                            nama_produk = obj.getString("n"),
                            harga_produk = obj.getDouble("h"),
                            null
                        )
                    )
                }
            }
        return listoSave

    }
}