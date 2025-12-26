package com.example.thenotes

import android.app.Application
import com.example.thenotes.data.AppContainer
import com.example.thenotes.data.AppDataContainer


class TheNotes : Application(){
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}