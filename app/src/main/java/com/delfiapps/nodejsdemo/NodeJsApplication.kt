package com.delfiapps.nodejsdemo

import android.app.Application
import android.content.Context

class NodeJsApplication : Application() {

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

}