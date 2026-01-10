package com.arbadev.thenota

import android.app.Application
import com.arbadev.thenota.data.AppContainer
import com.arbadev.thenota.data.AppDataContainer


class TheNotes : Application(){
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}