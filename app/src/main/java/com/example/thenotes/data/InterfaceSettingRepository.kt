package com.example.thenotes.data

import kotlinx.coroutines.flow.Flow

interface InterfaceSettingRepository {
    fun getAllSettingsStream(): Flow<List<Setting>>
    fun getSettingById(id: Int): Flow<Setting>
    suspend fun insertSetting(setting: Setting): Long
    suspend fun updateSetting(setting: Setting)
    suspend fun deleteSetting(setting: Setting)
}