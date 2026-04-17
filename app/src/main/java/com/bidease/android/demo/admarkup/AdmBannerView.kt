package com.bidease.android.demo.admarkup

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bidease.mobile.ads.mraid.MraidEnv
import com.bidease.mobile.ads.AdWebView
import com.bidease.mobile.ads.AdSize
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
    private val scriptSource = ScriptSource(context)
    private val webViewChannel = DefaultWebViewChannel()

    private var adSize: AdSize? = null
    private var adViews: List<View> = emptyList()
    private var isLoaded = false

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
        val (mraidScript, bideaseScript) = withContext(Dispatchers.IO) {
            Pair(
                scriptSource.getMraidScript(),
                scriptSource.getBideaseScript()
            )
        }

        this.adViews = scenes?.filterIsInstance<WebViewScene>()?.mapIndexed { sceneIndex, scene ->
            val webView = AdWebView(context)
            val mraid = BannerMraidJsInterface(webView)
            mraid.onOpen = { url ->
                println("Bidease Banner click")

                if (url != null) {
                    context.openExternalLink(url)
                }

                coroutineScope.launch {
                    onClicked?.invoke()
                }
            }
            mraid.onClose = {
                dismiss()
            }
            webView.addJavascriptInterface(mraid, "jsBridge")
            val bidease = BideaseJsInterface(
                bideaseScript,
                webView,
                webViewChannel
            )
            bidease.onClose = {
                dismiss()
            }
            bidease.onSwitchScene = ::switchScene
            webView.addJavascriptInterface(bidease, "bideaseBridge")
            webView.loadHtml(
                scene.baseUrl,
                scene.html
            )

            webView.webViewClient = MraidWebViewClient(
                scene.mraidEnv ?: MraidEnv(),
                mraidScript,
                scene.timeoutMsec
            ).apply {
                onFinishLoading = {
                    mraid.onStateChange(MraidJsInterface.State.DEFAULT)
                    mraid.onReady()
                    bidease.onReady()
                    coroutineScope.launch {
                        if (!isLoaded) {
                            println("Bidease Banner loaded")
                            isLoaded = true
                            onLoaded?.invoke()
                            trackDisplay(webView)
                        }
                    }
                }
                onOpenExternalLink = { url ->
                    println("Bidease Banner click")

                    if (url != null) {
                        context.openExternalLink(url)
                    }

                    coroutineScope.launch {
                        onClicked?.invoke()
                    }
                }
                this@apply.onError = { error ->
                    println("Bidease Banner error ${error}")
                    coroutineScope.launch {
                        if (!isLoaded) {
                            onFailed?.invoke(error)
                        }
                    }
                }
            }
            webView
        } ?: emptyList()
        switchScene(0)
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
        if (scene >= 0 && scene < adViews.size) {
            removeAllViews()
            val view = adViews[scene]
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

    private fun dismiss() {
        println("Bidease Banner close")
        removeAllViews()
        coroutineScope.launch {
            onClosed?.invoke()
        }
    }
}
