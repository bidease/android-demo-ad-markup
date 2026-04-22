package com.bidease.android.demo.admarkup

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bidease.mobile.ads.AdService
import com.bidease.mobile.ads.mraid.MraidEnv
import com.bidease.mobile.ads.AdWebView
import com.bidease.mobile.ads.AdSize
import com.bidease.mobile.ads.AdView
import com.bidease.mobile.ads.BideaseJsInterface
import com.bidease.mobile.ads.MraidJsInterface
import com.bidease.mobile.ads.MraidWebViewClient
import com.bidease.mobile.ads.ScriptSource
import com.bidease.mobile.ads.scene.Scene
import com.bidease.mobile.ads.scene.WebViewScene
import com.bidease.mobile.bannerads.BannerMraidJsInterface
import com.bidease.mobile.js.modules.DefaultWebViewChannel
import com.bidease.mobile.js.modules.AdViewController
import com.bidease.mobile.ktx.dpToPx
import com.bidease.mobile.ktx.openExternalLink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.String

class AdmBannerView: FrameLayout {
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val adViewController = AdViewController()
    private val webViewChannel = DefaultWebViewChannel()

    private var adSize: AdSize? = null
    private var sceneResults: List<AdView.LoadResult> = emptyList()

    var onLoaded: (() -> Unit)? = null
    var onDisplayed: (() -> Unit)? = null
    var onFailed: ((String) -> Unit)? = null
    var onClicked: (() -> Unit)? = null
    var onClosed: (() -> Unit)? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int,
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        adViewController.onSwitch = ::switchScene
        adViewController.onDismiss = ::dismiss
    }

    suspend fun show(adSize: AdSize, scenes: List<Scene>?) {
        this.adSize = adSize

        var sceneResults = emptyList<AdView.LoadResult>()
        scenes?.let { scenes ->
            sceneResults = scenes.filterIsInstance<WebViewScene>().mapIndexed { sceneIndex, scene ->
                val adView = AdView(
                    context = context,
                    scene = scene,
                    webViewChannel = webViewChannel,
                    shouldCloseVisible = false,
                    createMraidJsInterface = { webView -> BannerMraidJsInterface(webView) }
                ).apply {
                    onClick = { _ ->
                        println("Bidease Banner button click")
                    }
                    onCloseClick = ::dismiss
                    onWebViewClick = { url ->
                        println("Bidease Banner click")

                        coroutineScope.launch {
                            if (url != null) {
                                context.openExternalLink(url)
                            }

                            onClicked?.invoke()
                        }
                    }
                    onSwitchScene = ::switchScene
                }
                adView.load()
            }
        }

        if (sceneResults.isNotEmpty()) {
            when(val sceneRes = sceneResults.first()) {
                is AdView.LoadSuccess -> {
                    this.sceneResults = sceneResults
                    switchScene(0)
                    println("Bidease Banner loaded")
                    onLoaded?.invoke()
                    trackDisplay(sceneRes.view)
                }
                is AdView.LoadFailure -> {
                    println("Bidease Banner error ${sceneRes.error}")
                    onFailed?.invoke(sceneRes.error)
                }
            }
        } else {
            println("Bidease Banner error scenes is empty")
            onFailed?.invoke("scenes is empty")
        }
    }

    private fun fireDisplay() {
        println("Bidease Banner displayed")
        coroutineScope.launch {
            onDisplayed?.invoke()
        }
    }

    private fun trackDisplay(view: View) {
        if (view.isAttachedToWindow) {
            fireDisplay()
            return
        }
        view.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(p0: View) {
                fireDisplay()
            }

            override fun onViewDetachedFromWindow(p0: View) {

            }
        })
    }

    private fun switchScene(scene: Int) {
        println("Bidease Banner switchScene $scene")
        if (scene >= 0 && scene < sceneResults.size) {
            removeAllViews()
            val sceneResult = sceneResults[scene]
            if (sceneResult is AdView.LoadSuccess) {
                val view = sceneResult.view
                val height = adSize?.height?.dpToPx(context)?.toInt()
                    ?: LayoutParams.WRAP_CONTENT
                addView(
                    view, ViewGroup.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        height
                    )
                )
            }
        }
    }

    private fun dismiss() {
        println("Bidease Banner close")
        coroutineScope.launch {
            removeAllViews()
            onClosed?.invoke()
        }
    }
}
