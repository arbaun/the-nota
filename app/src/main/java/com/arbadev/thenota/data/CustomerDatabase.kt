package com.arbadev.thenota.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Customer::class, Setting::class,
                     Produk::class, Nota::class,
    ItemNota::class], version = 4, exportSchema = false)
public abstract class CustomerDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun settingDao(): SettingDao
    abstract fun produkDao(): ProdukDao
    abstract fun notaDao(): NotaDao
    abstract fun itemNotaDao(): ItemNotaDao
    companion object{
        @Volatile
        private var Instance : CustomerDatabase?=null
        var MIGRATION_3_4 = object : Migration(3,4){
            override fun migrate(db: SupportSQLiteDatabase) {
                //super.migrate(db)
                db.execSQL("ALTER TABLE itemnota ADD COLUMN unit_produk TEXT")
                db.execSQL("ALTER TABLE produk ADD COLUMN unit_produk TEXT")
            }
        }
        fun getDatabase(context: Context): CustomerDatabase{
            return Instance?: synchronized(this){
                Room.databaseBuilder(context, CustomerDatabase::class.java,"tnota.db")
                    .addMigrations(MIGRATION_3_4)
                    .fallbackToDestructiveMigration(true).build().also { Instance = it }
            }
        }
    }
}