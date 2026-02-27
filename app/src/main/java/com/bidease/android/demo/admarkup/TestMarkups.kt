package com.bidease.android.demo.admarkup

import android.content.Context
import java.io.InputStreamReader
private val LOG_EXTENSIONS = listOf("txt")

fun loadTestMarkupFromAssets(context: Context, name: String): String? {
    for (ext in LOG_EXTENSIONS) {
        val path = "$name.$ext"
        try {
            return context.assets.open(path).use { stream ->
                InputStreamReader(stream, Charsets.UTF_8).readText()
            }
        } catch (_: Exception) {
            continue
        }
    }
    return null
}