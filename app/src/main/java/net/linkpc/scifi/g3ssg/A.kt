package net.linkpc.scifi.g3ssg

import android.app.Application
import android.content.Context
import android.content.res.Resources
import net.linkpc.scifi.g3ssg.core.TimeAdapterD
import net.linkpc.scifi.g3ssg.core.TimeAdapterH

class A: Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        adapters.add(TimeAdapterD())
        adapters.add(TimeAdapterH())
    }

    companion object {
        lateinit var instance: A
            private set
        val context: Context get() = instance.applicationContext
        val resources: Resources get() = context.resources
        val adapters get() = net.linkpc.scifi.g3ssg.adapter.adapters
    }
}
