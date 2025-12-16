package com.example.thenotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "itemnota")
data class ItemNota(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    var nota_id : Int,
    var nama_produk : String,
    var harga_produk: Double,
    var qty: Int,
    var subtotal : Double
)
