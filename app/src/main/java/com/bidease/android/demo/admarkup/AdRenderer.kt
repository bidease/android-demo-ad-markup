package com.bidease.android.demo.admarkup

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import com.bidease.bidservice.openrtb.response.Bid
import com.bidease.bidservice.openrtb.response.BidResponse
import com.bidease.bidservice.openrtb.response.SeatBid
import com.bidease.mobile.BideaseMobile
import com.bidease.mobile.interstitialads.InterstitialController
import com.bidease.mobile.legacy.AdSize
import com.bidease.mobile.legacy.configuration.AdUnitConfiguration
import com.bidease.mobile.legacy.converters.toLegacy
import com.bidease.mobile.legacy.rendering.views.interstitial.InterstitialManager
import com.bidease.mobile.views.AdViewManager

suspend fun renderBanner(
    context: Context,
    parentView: ViewGroup,
    markup: String,
    onDisplayed: () -> Unit = {},
    onFailed: (String) -> Unit = {},
    onClicked: () -> Unit = {},
    onClosed: () -> Unit = {},
    logger: AdLifecycleLogger? = null
) {
    val validation = CreativeFormatDetector.validateMarkup(markup)
    if (!validation.isValid) {
        val errorMessage = "Invalid markup: ${validation.message}"
        logger?.logEvent(AdLifecycleEvent.FAIL, validation.message)
        onFailed(errorMessage)
        return
    }

    logger?.logEvent(AdLifecycleEvent.LOAD, "Banner ad")
    BideaseMobile.awaitInit()
    
    val bid = Bid(
        id = "banner_${System.currentTimeMillis()}",
        adm = markup,
        width = 320,
        height = 50
    )

    val bidResponse = BidResponse(
        id = "response_${System.currentTimeMillis()}",
        seatBids = listOf(SeatBid(seat = "seat", bids = listOf(bid)))
    )

    val config = AdUnitConfiguration.createBannerAdUnitConfiguration().apply {
        addSize(AdSize(320, 50))
    }
    
    val legacyResponse = bidResponse.toLegacy(config)

    val adViewManager = AdViewManager(context, parentView, InterstitialManager()).apply {
        onAdLoaded = { _ ->
            logger?.logEvent(AdLifecycleEvent.RENDER, "Banner ad loaded")
        }
        onViewReadyForImmediateDisplay = { creative ->
            logger?.logEvent(AdLifecycleEvent.RENDER, "Banner creative ready")
            parentView.removeAllViews()
            parentView.addView(creative, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ))
            logger?.logEvent(AdLifecycleEvent.SHOW, "Banner ad displayed")
            onDisplayed()
        }
        onFailedToLoad = { error ->
            logger?.logEvent(AdLifecycleEvent.FAIL, error.toString())
            onFailed(error.toString())
        }
        onCreativeClicked = { _ ->
            logger?.logEvent(AdLifecycleEvent.CLICK, "Banner ad clicked")
            onClicked()
        }
        onCreativeInterstitialClosed = {
            logger?.logEvent(AdLifecycleEvent.CLOSE, "Banner ad closed")
            onClosed()
        }
    }

    adViewManager.loadBidTransaction(config, legacyResponse)
}

suspend fun renderInterstitial(
    context: Context,
    markup: String,
    onDisplayed: () -> Unit = {},
    onFailed: (String) -> Unit = {},
    onClicked: () -> Unit = {},
    onClosed: () -> Unit = {},
    logger: AdLifecycleLogger? = null
): InterstitialController {
    val validation = CreativeFormatDetector.validateMarkup(markup)
    if (!validation.isValid) {
        val errorMessage = "Invalid markup: ${validation.message}"
        logger?.logEvent(AdLifecycleEvent.FAIL, validation.message)
        onFailed(errorMessage)
        throw IllegalArgumentException(errorMessage)
    }

    logger?.logEvent(AdLifecycleEvent.LOAD, "Interstitial ad")
    BideaseMobile.awaitInit()
    
    val bid = Bid(
        id = "interstitial_${System.currentTimeMillis()}",
        adm = markup,
        width = 320,
        height = 480
    )

    val bidResponse = BidResponse(
        id = "response_${System.currentTimeMillis()}",
        seatBids = listOf(SeatBid(seat = "seat", bids = listOf(bid)))
    )

    val config = AdUnitConfiguration.createInterstitialAdUnitConfiguration().apply {
        addSize(AdSize(320, 480))
    }
    
    val legacyResponse = bidResponse.toLegacy(config)

    var clickHandlerSet = false
    val controller = InterstitialController(context).apply {
        onInterstitialReadyForDisplay = {
            logger?.logEvent(AdLifecycleEvent.RENDER, "Interstitial ad ready")
            show()
        }
        onInterstitialDisplayed = {
            logger?.logEvent(AdLifecycleEvent.SHOW, "Interstitial ad displayed")
            onDisplayed()
        }
        onInterstitialFailedToLoad = { error ->
            logger?.logEvent(AdLifecycleEvent.FAIL, error ?: "Unknown error")
            onFailed(error ?: "Unknown error")
        }
        onInterstitialClicked = {
            logger?.logEvent(AdLifecycleEvent.CLICK, "Interstitial ad clicked")
            clickHandlerSet = true
            onClicked()
        }
        onInterstitialClosed = {
            logger?.logEvent(AdLifecycleEvent.CLOSE, "Interstitial ad closed")
            onClosed()
        }
    }
    
    android.util.Log.d("AdRenderer", "Interstitial click handler set: $clickHandlerSet")

    controller.loadAd(config, legacyResponse)
    return controller
}

suspend fun renderRewarded(
    context: Context,
    markup: String,
    onDisplayed: () -> Unit = {},
    onFailed: (String) -> Unit = {},
    onClicked: () -> Unit = {},
    onClosed: () -> Unit = {},
    onRewarded: () -> Unit = {},
    logger: AdLifecycleLogger? = null
): InterstitialController {
    val validation = CreativeFormatDetector.validateMarkup(markup)
    if (!validation.isValid) {
        val errorMessage = "Invalid markup: ${validation.message}"
        logger?.logEvent(AdLifecycleEvent.FAIL, validation.message)
        onFailed(errorMessage)
        throw IllegalArgumentException(errorMessage)
    }

    logger?.logEvent(AdLifecycleEvent.LOAD, "Rewarded ad")
    BideaseMobile.awaitInit()
    
    val bid = Bid(
        id = "rewarded_${System.currentTimeMillis()}",
        adm = markup,
        width = 320,
        height = 480
    )

    val bidResponse = BidResponse(
        id = "response_${System.currentTimeMillis()}",
        seatBids = listOf(SeatBid(seat = "seat", bids = listOf(bid)))
    )

    val config = AdUnitConfiguration.createRewardedAdUnitConfiguration().apply {
        addSize(AdSize(320, 480))
    }
    
    val legacyResponse = bidResponse.toLegacy(config)

    val controller = InterstitialController(context).apply {
        onInterstitialReadyForDisplay = {
            logger?.logEvent(AdLifecycleEvent.RENDER, "Rewarded ad ready")
            show()
        }
        onInterstitialDisplayed = {
            logger?.logEvent(AdLifecycleEvent.SHOW, "Rewarded ad displayed")
            onDisplayed()
        }
        onInterstitialFailedToLoad = { error ->
            logger?.logEvent(AdLifecycleEvent.FAIL, error ?: "Unknown error")
            onFailed(error ?: "Unknown error")
        }
        onInterstitialClicked = {
            logger?.logEvent(AdLifecycleEvent.CLICK, "Rewarded ad clicked")
            onClicked()
        }
        onInterstitialClosed = {
            logger?.logEvent(AdLifecycleEvent.CLOSE, "Rewarded ad closed")
            onClosed()
            onRewarded()
        }
    }

    controller.loadAd(config, legacyResponse)
    return controller
}