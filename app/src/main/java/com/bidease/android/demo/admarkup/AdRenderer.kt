package com.bidease.android.demo.admarkup

import android.content.Context
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
    onClosed: () -> Unit = {}
) {
    val validation = CreativeFormatDetector.validateMarkup(markup)
    if (!validation.isValid) {
        onFailed("Invalid markup: ${validation.message}")
        return
    }

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
        }
        onViewReadyForImmediateDisplay = { creative ->
            parentView.removeAllViews()
            parentView.addView(creative, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ))
            onDisplayed()
        }
        onFailedToLoad = { error ->
            onFailed(error.toString())
        }
        onCreativeClicked = { _ ->
            onClicked()
        }
        onCreativeInterstitialClosed = {
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
    onClosed: () -> Unit = {}
): InterstitialController {
    val validation = CreativeFormatDetector.validateMarkup(markup)
    if (!validation.isValid) {
        onFailed("Invalid markup: ${validation.message}")
        throw IllegalArgumentException("Invalid markup: ${validation.message}")
    }

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

    val controller = InterstitialController(context).apply {
        onInterstitialReadyForDisplay = {
            show()
        }
        onInterstitialDisplayed = {
            onDisplayed()
        }
        onInterstitialFailedToLoad = { error ->
            onFailed(error ?: "Unknown error")
        }
        onInterstitialClicked = {
            onClicked()
        }
        onInterstitialClosed = {
            onClosed()
        }
    }

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
    onRewarded: () -> Unit = {}
): InterstitialController {
    val validation = CreativeFormatDetector.validateMarkup(markup)
    if (!validation.isValid) {
        onFailed("Invalid markup: ${validation.message}")
        throw IllegalArgumentException("Invalid markup: ${validation.message}")
    }

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
            show()
        }
        onInterstitialDisplayed = {
            onDisplayed()
        }
        onInterstitialFailedToLoad = { error ->
            onFailed(error ?: "Unknown error")
        }
        onInterstitialClicked = {
            onClicked()
        }
        onInterstitialClosed = {
            onClosed()
            onRewarded()
        }
    }

    controller.loadAd(config, legacyResponse)
    return controller
}