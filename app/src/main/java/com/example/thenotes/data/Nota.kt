package com.example.thenotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="nota")
data class Nota(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val costumer_name: String,
    val date_time : String,
    val total: Double
)
