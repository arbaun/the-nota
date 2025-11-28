package com.example.thenotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Setting(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val nama_toko: String,
    val catatan_kaki: String
)
