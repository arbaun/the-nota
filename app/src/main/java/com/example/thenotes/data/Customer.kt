package com.example.thenotes.data
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "customer",
    indices = [Index(value = ["nama"], unique = true)])
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val nama: String
)
