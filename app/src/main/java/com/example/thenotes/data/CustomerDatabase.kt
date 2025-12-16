package com.example.thenotes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.security.KeyStore

@Database(entities = [Customer::class, Setting::class,
                     Produk::class, Nota::class,
    ItemNota::class], version = 3, exportSchema = false)
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
                Room.databaseBuilder(context, CustomerDatabase::class.java,"tnota.db")
                    .fallbackToDestructiveMigration(true).build().also { Instance = it }
            }
        }
    }
}