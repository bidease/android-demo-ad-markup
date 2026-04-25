package com.bidease.android.demo.admarkup

import android.content.Context
import android.view.ViewGroup
import com.bidease.mobile.ads.AdSize
import com.bidease.mobile.ads.mraid.MraidEnv
import com.bidease.mobile.ads.scene.ButtonAction
import com.bidease.mobile.ads.scene.ButtonProps
import com.bidease.mobile.ads.scene.ElementType
import com.bidease.mobile.ads.scene.SceneType
import com.bidease.mobile.ads.scene.WebViewScene
import com.bidease.mobile.interstitialads.AdDialog
import com.bidease.mobile.interstitialads.AndroidAdDialog
import com.bidease.mobile.js.modules.DefaultWebViewChannel
import com.bidease.mobile.ktx.openExternalLink
import kotlin.collections.emptyList

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
    
    val banner = AdmBannerView(context)
    banner.onLoaded = {
        logger?.logEvent(AdLifecycleEvent.RENDER, "Banner ad loaded")
    }
    banner.onFailed = { error: String ->
        val message = error.ifBlank { "Unknown error" }
        logger?.logEvent(AdLifecycleEvent.FAIL, message)
        onFailed(message)
    }
    banner.onDisplayed = {
        logger?.logEvent(AdLifecycleEvent.SHOW, "Banner ad displayed")
        onDisplayed()
    }
    banner.onClicked = {
        logger?.logEvent(AdLifecycleEvent.CLICK, "Banner ad clicked")
        onClicked()
    }
    banner.onClosed = {
        logger?.logEvent(AdLifecycleEvent.CLOSE, "Banner ad closed")
        onClosed()
    }

    val scenes = listOf(
        WebViewScene(
            type = SceneType.WEB_VIEW,
            baseUrl = "",
            html = markup,
            timeoutMsec = 5000,
            template = emptyList(),
            skadn = null,
            aak = null,
            mraidEnv = MraidEnv(
                appId = "",
                ifa = "",
                limitAdTracking = false,
                coppa = false
            ),
        )
    )

    banner.show(AdSize(320, 50), scenes)
    parentView.removeAllViews()
    parentView.addView(
        banner,
        ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    )
}

suspend fun renderInterstitial(
    context: Context,
    markup: String,
    onDisplayed: () -> Unit = {},
    onFailed: (String) -> Unit = {},
    onClicked: () -> Unit = {},
    onClosed: () -> Unit = {},
    logger: AdLifecycleLogger? = null
): AndroidAdDialog {
    val validation = CreativeFormatDetector.validateMarkup(markup)
    if (!validation.isValid) {
        val errorMessage = "Invalid markup: ${validation.message}"
        logger?.logEvent(AdLifecycleEvent.FAIL, validation.message)
        onFailed(errorMessage)
        throw IllegalArgumentException(errorMessage)
    }

    logger?.logEvent(AdLifecycleEvent.LOAD, "Interstitial ad")

    val scenes = listOf(
        WebViewScene(
            type = SceneType.WEB_VIEW,
            baseUrl = "",
            html = markup,
            timeoutMsec = 5000,
            template = listOf(ButtonProps(type= ElementType.BUTTON, action = ButtonAction.CLOSE)),
            skadn = null,
            aak = null,
            mraidEnv = MraidEnv(
                appId = "",
                ifa = "",
                limitAdTracking = false,
                coppa = false
            ),
        )
    )

    val events = AdDialog.Events(
        onClick = { _, _ ->
            logger?.logEvent(AdLifecycleEvent.CLICK, "Interstitial ad clicked (button)")
            onClicked()
        },

        onWebViewClick = { _, url ->
            if (url != null) {
                context.openExternalLink(url)
            }

            logger?.logEvent(AdLifecycleEvent.CLICK, "Interstitial ad clicked")
            onClicked()
        },

        onError = { _, error ->
            val message = error?.ifBlank { "Unknown error" } ?: "Unknown error"
            logger?.logEvent(AdLifecycleEvent.FAIL, message)
            onFailed(message)
        }
    )

    val showEvents = AdDialog.ShowEvents(
        onDialogClose = {
            logger?.logEvent(AdLifecycleEvent.CLOSE, "Interstitial ad closed")
            onClosed()
        },

        onDisplayed = {
            logger?.logEvent(AdLifecycleEvent.SHOW, "Interstitial ad displayed")
            onDisplayed()
        },

        onError = { error ->
            val message = error.ifBlank { "Unknown error" }
            logger?.logEvent(AdLifecycleEvent.FAIL, message)
            onFailed(message)
        }
    )

    val adDialog = AndroidAdDialog(context, DefaultWebViewChannel())
    adDialog.load(AdDialog.LoadProps(scenes), events)
    adDialog.show(AdDialog.ShowProps(preventSystemClose = false), showEvents)

    return adDialog
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
): AndroidAdDialog {
    val validation = CreativeFormatDetector.validateMarkup(markup)
    if (!validation.isValid) {
        val errorMessage = "Invalid markup: ${validation.message}"
        logger?.logEvent(AdLifecycleEvent.FAIL, validation.message)
        onFailed(errorMessage)
        throw IllegalArgumentException(errorMessage)
    }

    logger?.logEvent(AdLifecycleEvent.LOAD, "Rewarded ad")

    val scenes = listOf(
        WebViewScene(
            type = SceneType.WEB_VIEW,
            baseUrl = "",
            html = markup,
            timeoutMsec = 5000,
            template = listOf(ButtonProps(type= ElementType.BUTTON, action = ButtonAction.CLOSE)),
            skadn = null,
            aak = null,
            mraidEnv = MraidEnv(
                appId = "",
                ifa = "",
                limitAdTracking = false,
                coppa = false
            ),
        )
    )

    val events = AdDialog.Events(
        onClick = { _, _ ->
            logger?.logEvent(AdLifecycleEvent.CLICK, "Interstitial ad clicked (button)")
            onClicked()
        },

        onWebViewClick = { _, url ->
            if (url != null) {
                context.openExternalLink(url)
            }

            logger?.logEvent(AdLifecycleEvent.CLICK, "Interstitial ad clicked (WebView)")
            onClicked()
        },

        onError = { _, error ->
            val message = error?.ifBlank { "Unknown error" } ?: "Unknown error"
            logger?.logEvent(AdLifecycleEvent.FAIL, message)
            onFailed(message)
        }
    )

    val showEvents = AdDialog.ShowEvents(
        onDialogClose = {
            logger?.logEvent(AdLifecycleEvent.CLOSE, "Interstitial ad closed")
            onRewarded()
            onClosed()
        },

        onDisplayed = {
            logger?.logEvent(AdLifecycleEvent.SHOW, "Interstitial ad displayed")
            onDisplayed()
        },

        onError = { error ->
            val message = error.ifBlank { "Unknown error" }
            logger?.logEvent(AdLifecycleEvent.FAIL, message)
            onFailed(message)
        }
    )

    val adDialog = AndroidAdDialog(context, DefaultWebViewChannel())
    adDialog.load(AdDialog.LoadProps(scenes), events)
    adDialog.show(AdDialog.ShowProps(preventSystemClose = false), showEvents)

    return adDialog
}
