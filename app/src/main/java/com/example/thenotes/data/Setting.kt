package com.example.thenotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Setting(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    var nama_toko: String,
    var alamat_toko: String,
    var uri_logo: String,
    var catatan_kaki: String
)
