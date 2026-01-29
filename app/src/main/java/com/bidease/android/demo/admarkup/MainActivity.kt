package com.bidease.android.demo.admarkup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.bidease.android.demo.admarkup.ui.theme.AndroidDemoAdMarkupTheme
import com.bidease.mobile.BideaseMobile
import com.bidease.mobile.InitFailure
import com.bidease.mobile.InitSuccess
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch {
            when (val result = BideaseMobile.init(applicationContext)) {
                is InitSuccess -> {
                    println("SDK initialized successfully")
                }
                is InitFailure -> {
                    println("SDK initialization failed: ${result.error}")
                }
            }
        }
        
        setContent {
            AndroidDemoAdMarkupTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AdMarkupDemoScreen()
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        BideaseMobile.onDestroy()
    }
}