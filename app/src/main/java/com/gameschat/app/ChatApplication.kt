package com.gameschat.app

import android.app.Application

class ChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: ChatApplication
            private set
    }
}
