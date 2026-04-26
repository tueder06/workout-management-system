package com.teodor.shared.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.InputStream

class PropertyLoaderTest {
    private fun String.toStream(): InputStream = this.byteInputStream()

    @Test
    fun `init throws RuntimeException when inputStream is null`() {
        val exception = assertThrows(RuntimeException::class.java) {
            PropertyLoader(null)
        }
        assertEquals("Unable to find config.properties", exception.message)
    }

    @Test
    fun `init succeeds when inputStream is completely empty`() {
        val loader = PropertyLoader("".toStream())
        assertEquals("", loader.get("any.key"))
    }


    @Test
    fun `get returns correct value when key exists`() {
        val fileContent = "supabase.url=https://test.com\napi.timeout=60"
        val loader = PropertyLoader(fileContent.toStream())
        assertEquals("https://test.com", loader.get("supabase.url"))
        assertEquals("60", loader.get("api.timeout"))
    }

    @Test
    fun `get returns default empty string when key is missing and no default is provided`() {
        val loader = PropertyLoader("existing.key=123".toStream())
        val result = loader.get("missing.key")
        assertEquals("", result)
    }

    @Test
    fun `get returns custom default value when key is missing`() {
        val loader = PropertyLoader("existing.key=123".toStream())
        val result = loader.get("missing.key", "fallback_value")
        assertEquals("fallback_value", result)
    }

    @Test
    fun `get returns actual empty string if key exists but has no value`() {
        val fileContent = "empty.key=\nanother.key=value"
        val loader = PropertyLoader(fileContent.toStream())
        val result = loader.get("empty.key", "fallback_value")
        assertEquals("", result)
    }

    @Test
    fun `get trims leading whitespaces but keeps trailing whitespaces`() {
        val fileContent = "weird.key=   value_with_space   "
        val loader = PropertyLoader(fileContent.toStream())
        val result = loader.get("weird.key")
        assertEquals("value_with_space   ", result)
    }
}