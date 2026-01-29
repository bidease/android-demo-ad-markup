package com.bidease.android.demo.admarkup

import android.widget.FrameLayout
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bidease.mobile.interstitialads.InterstitialController

@Composable
fun AdMarkupDemoScreen() {
    val testBannerMarkup = getTestBannerMarkup()
    val testInterstitialMarkup = getTestInterstitialMarkup()
    val testRewardedMarkup = getTestRewardedMarkup()
    val testMraidMarkup = getTestMraidMarkup()
    
    var bannerMarkup by remember { mutableStateOf("") }
    var interstitialMarkup by remember { mutableStateOf("") }
    var rewardedMarkup by remember { mutableStateOf("") }
    var mraidMarkup by remember { mutableStateOf("") }
    
    var bannerStatus by remember { mutableStateOf("") }
    var interstitialStatus by remember { mutableStateOf("") }
    var rewardedStatus by remember { mutableStateOf("") }
    var mraidStatus by remember { mutableStateOf("") }
    
    var bannerContainer by remember { mutableStateOf<FrameLayout?>(null) }
    var mraidContainer by remember { mutableStateOf<FrameLayout?>(null) }
    var interstitialController by remember { mutableStateOf<InterstitialController?>(null) }
    var rewardedController by remember { mutableStateOf<InterstitialController?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        
        Text(
            text = "Bidease Ad Markup Demo",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Paste your ad markup (HTML, VAST, or MRAID) and click Show",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        HorizontalDivider()
        Spacer(modifier = Modifier.height(20.dp))
        
        BannerSection(
            markup = bannerMarkup,
            onMarkupChange = { bannerMarkup = it },
            status = bannerStatus,
            onStatusChange = { bannerStatus = it },
            container = bannerContainer,
            onContainerChange = { bannerContainer = it },
            testMarkup = testBannerMarkup
        )
        
        HorizontalDivider()
        Spacer(modifier = Modifier.height(20.dp))
        
        InterstitialSection(
            markup = interstitialMarkup,
            onMarkupChange = { interstitialMarkup = it },
            status = interstitialStatus,
            onStatusChange = { interstitialStatus = it },
            controller = interstitialController,
            onControllerChange = { interstitialController = it },
            testMarkup = testInterstitialMarkup
        )
        
        HorizontalDivider()
        Spacer(modifier = Modifier.height(20.dp))
        
        RewardedSection(
            markup = rewardedMarkup,
            onMarkupChange = { rewardedMarkup = it },
            status = rewardedStatus,
            onStatusChange = { rewardedStatus = it },
            controller = rewardedController,
            onControllerChange = { rewardedController = it },
            testMarkup = testRewardedMarkup
        )
        
        HorizontalDivider()
        Spacer(modifier = Modifier.height(20.dp))
        
        MraidSection(
            markup = mraidMarkup,
            onMarkupChange = { mraidMarkup = it },
            status = mraidStatus,
            onStatusChange = { mraidStatus = it },
            container = mraidContainer,
            onContainerChange = { mraidContainer = it },
            testMarkup = testMraidMarkup
        )
        
        Spacer(modifier = Modifier.height(200.dp))
    }
}