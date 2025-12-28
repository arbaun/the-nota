package com.example.thenotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "produk")
data class Produk(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    var nama_produk: String,
    var harga_produk: Double,
    var unit_produk: String?,
)
