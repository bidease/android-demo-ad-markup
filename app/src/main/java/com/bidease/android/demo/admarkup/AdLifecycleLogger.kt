package com.bidease.android.demo.admarkup

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AdLifecycleEvent {
    LOAD,
    RENDER,
    SHOW,
    CLICK,
    CLOSE,
    FAIL
}

class AdLifecycleLogger(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val _events = MutableStateFlow<List<String>>(emptyList())
    val events: StateFlow<List<String>> = _events.asStateFlow()
    
    private val toastQueue = mutableListOf<String>()
    private var isProcessingToast = false
    private val toastDelay = 2000L
    
    fun logEvent(event: AdLifecycleEvent, details: String = "") {
        val message = when (event) {
            AdLifecycleEvent.LOAD -> "🔄 LOAD: Loading ad${if (details.isNotEmpty()) ": $details" else ""}"
            AdLifecycleEvent.RENDER -> "🎨 RENDER: Rendering ad${if (details.isNotEmpty()) ": $details" else ""}"
            AdLifecycleEvent.SHOW -> "👁️ SHOW: Ad displayed${if (details.isNotEmpty()) ": $details" else ""}"
            AdLifecycleEvent.CLICK -> "👆 CLICK: Ad clicked${if (details.isNotEmpty()) ": $details" else ""}"
            AdLifecycleEvent.CLOSE -> "❌ CLOSE: Ad closed${if (details.isNotEmpty()) ": $details" else ""}"
            AdLifecycleEvent.FAIL -> "⚠️ FAIL: Rendering failed${if (details.isNotEmpty()) ": $details" else ""}"
        }

        _events.value += message
        toastQueue.add(message)
        processToastQueue()
    }
    
    fun log(message: String) {
        _events.value += message
        toastQueue.add(message)
        processToastQueue()
    }
    
    private fun processToastQueue() {
        if (isProcessingToast || toastQueue.isEmpty()) return
        
        isProcessingToast = true
        scope.launch {
            while (toastQueue.isNotEmpty()) {
                val message = toastQueue.removeAt(0)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                delay(toastDelay)
            }
            isProcessingToast = false
        }
    }
    
    fun clear() {
        _events.value = emptyList()
        toastQueue.clear()
    }
    
    fun dispose() {
        scope.cancel()
        _events.value = emptyList()
        toastQueue.clear()
    }
}

@Composable
fun rememberAdLifecycleLogger(context: Context): AdLifecycleLogger {
    val logger = remember {
        AdLifecycleLogger(context)
    }
    
    DisposableEffect(logger) {
        onDispose {
            logger.dispose()
        }
    }
    
    return logger
}

@Composable
fun AdLifecycleLogger.collectEventsAsState(): State<List<String>> {
    return events.collectAsState()
}