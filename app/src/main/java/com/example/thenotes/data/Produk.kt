package com.example.thenotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "produk")
data class Produk(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val nama_produk: String,
    val harga_produk: Double,
    val diskon: Double = 0.toDouble()
)
