package com.example.thenotes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingDao  {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSetting(setting: Setting): Long
    @Update
    suspend fun updateSetting(setting: Setting)
    @Delete
    suspend fun deleteSetting(setting: Setting)
    @Query("Select * from settings")
    fun getAllSettings(): Flow<List<Setting>>
    @Query("select * from settings where id=:id")
    fun getSettingById(id: Int): Flow<Setting>
}