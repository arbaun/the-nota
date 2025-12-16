package com.example.thenotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="nota")
data class Nota(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    var customer_name: String,
    var date_time : String,
    var total: Double
)
