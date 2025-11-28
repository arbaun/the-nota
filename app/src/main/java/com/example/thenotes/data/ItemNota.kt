package com.example.thenotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "itemnota")
data class ItemNota(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val nota_id : Int,
    val nama_produk : String,
    val harga_produk: Double,
    val diskon_produk: Double,
    val subtotal : Double
)
