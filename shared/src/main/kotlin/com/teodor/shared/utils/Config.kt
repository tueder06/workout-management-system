package com.teodor.shared.utils

import java.util.Properties

class PropertyLoader(private val inputStream: java.io.InputStream?) {
    private val properties = Properties()

    init {
        inputStream?.use { properties.load(it) }
            ?: throw RuntimeException("Unable to find config.properties")
    }

    fun get(key: String, defaultValue: String = ""): String {
        return properties.getProperty(key, defaultValue)
    }
}

object Config {
    private val loader: PropertyLoader by lazy {
        val stream = this::class.java.classLoader.getResourceAsStream("config.properties")
        PropertyLoader(stream)
    }

    fun getProperty(key: String, defaultValue: String = "") = loader.get(key, defaultValue)
}