package dev.arogundade.wojak.work

import android.content.Context

interface Initializer<out T : Any> {
    fun create(context: Context): T
    fun dependencies(): List<Class<out Initializer<*>>>
}