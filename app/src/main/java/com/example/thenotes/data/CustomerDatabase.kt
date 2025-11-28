package com.example.thenotes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Customer::class, Setting::class,
                     Produk::class, Nota::class,
    ItemNota::class], version = 1, exportSchema = false)
public abstract class CustomerDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun settingDao(): SettingDao
    abstract fun produkDao(): ProdukDao
    abstract fun notaDao(): NotaDao
    abstract fun itemNotaDao(): ItemNotaDao
    companion object{
        @Volatile
        private var Instance : CustomerDatabase?=null
        fun getDatabase(context: Context): CustomerDatabase{
            return Instance?: synchronized(this){
                Room.databaseBuilder(context, CustomerDatabase::class.java,"the-nota.db")
                    .build().also { Instance = it }
            }
        }
    }
}