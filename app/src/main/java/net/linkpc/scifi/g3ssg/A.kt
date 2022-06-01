package net.linkpc.scifi.g3ssg

import android.app.Application
import android.content.Context
import android.content.res.Resources

class A: Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: A
            private set
        val context: Context get() = instance.applicationContext
        val resources: Resources get() = context.resources
    }
}
