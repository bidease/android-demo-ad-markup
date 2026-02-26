package com.bidease.android.demo.admarkup

import android.widget.FrameLayout
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bidease.mobile.interstitialads.InterstitialController

@Composable
fun AdMarkupDemoScreen() {
    val context = LocalContext.current
    val testBannerMarkup = remember(context) {
        requireNotNull(loadTestMarkupFromAssets(context, "banner")) { "Missing asset: banner.txt" }
    }
    val testInterstitialMarkup = remember(context) {
        requireNotNull(loadTestMarkupFromAssets(context, "interstitial")) { "Missing asset: interstitial.txt" }
    }
    val testRewardedMarkup = remember(context) {
        requireNotNull(loadTestMarkupFromAssets(context, "rewarded")) { "Missing asset: rewarded.txt" }
    }
    val testMraidMarkup = remember(context) {
        requireNotNull(loadTestMarkupFromAssets(context, "mraid")) { "Missing asset: mraid.txt" }
    }
    
    val bannerHolder = remember { MarkupHolder() }
    val interstitialHolder = remember { MarkupHolder() }
    val rewardedHolder = remember { MarkupHolder() }
    val mraidHolder = remember { MarkupHolder() }
    
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
            holder = bannerHolder,
            status = bannerStatus,
            onStatusChange = { bannerStatus = it },
            container = bannerContainer,
            onContainerChange = { bannerContainer = it },
            testMarkup = testBannerMarkup
        )
        
        HorizontalDivider()
        Spacer(modifier = Modifier.height(20.dp))
        
        InterstitialSection(
            holder = interstitialHolder,
            status = interstitialStatus,
            onStatusChange = { interstitialStatus = it },
            controller = interstitialController,
            onControllerChange = { interstitialController = it },
            testMarkup = testInterstitialMarkup
        )
        
        HorizontalDivider()
        Spacer(modifier = Modifier.height(20.dp))
        
        RewardedSection(
            holder = rewardedHolder,
            status = rewardedStatus,
            onStatusChange = { rewardedStatus = it },
            controller = rewardedController,
            onControllerChange = { rewardedController = it },
            testMarkup = testRewardedMarkup
        )
        
        HorizontalDivider()
        Spacer(modifier = Modifier.height(20.dp))
        
        MraidSection(
            holder = mraidHolder,
            status = mraidStatus,
            onStatusChange = { mraidStatus = it },
            container = mraidContainer,
            onContainerChange = { mraidContainer = it },
            testMarkup = testMraidMarkup
        )
        
        Spacer(modifier = Modifier.height(200.dp))
    }
}