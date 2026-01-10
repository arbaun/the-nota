package com.arbadev.thenota.data

import kotlinx.coroutines.flow.Flow

class SettingRepository(private val settingDao: SettingDao): InterfaceSettingRepository {
    override fun getAllSettingsStream(): Flow<List<Setting>> = settingDao.getAllSettings()

    override fun getSettingById(id: Int): Flow<Setting> = settingDao.getSettingById(id)
    override suspend fun insertSetting(setting: Setting): Long = settingDao.insertSetting(setting)

    override suspend fun updateSetting(setting: Setting) = settingDao.updateSetting(setting)

    override suspend fun deleteSetting(setting: Setting) = settingDao.deleteSetting(setting)
}