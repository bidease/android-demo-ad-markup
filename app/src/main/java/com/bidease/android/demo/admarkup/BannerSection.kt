package com.bidease.android.demo.admarkup

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch

@Composable
fun BannerSection(
    markup: String,
    onMarkupChange: (String) -> Unit,
    status: String,
    onStatusChange: (String) -> Unit,
    container: FrameLayout?,
    onContainerChange: (FrameLayout?) -> Unit,
    testMarkup: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = rememberAdLifecycleLogger(context)
    val events by logger.collectEventsAsState()
    
    Text(
        text = "Banner Ad",
        style = MaterialTheme.typography.titleLarge
    )
    
    OutlinedTextField(
        value = markup,
        onValueChange = onMarkupChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Banner Markup") },
        placeholder = { Text("Paste HTML/VAST/MRAID markup here") },
        minLines = 3,
        maxLines = 5
    )
    
    Button(
        onClick = { onMarkupChange(testMarkup) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.test_banner))
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = {
                scope.launch {
                    try {
                        val markupToUse = if (markup.isBlank()) testMarkup else markup
                        onStatusChange("Loading...")
                        val newContainer = FrameLayout(context).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT
                            )
                        }
                        onContainerChange(newContainer)
                        
                        renderBanner(
                            context = context,
                            parentView = newContainer,
                            markup = markupToUse,
                            onDisplayed = { onStatusChange("Displayed") },
                            onFailed = { error -> onStatusChange("Failed: $error") },
                            onClicked = { onStatusChange("Clicked") },
                            onClosed = { onStatusChange("Closed") },
                            logger = logger
                        )
                    } catch (e: Exception) {
                        onStatusChange("Error: ${e.message}")
                    }
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.show_banner))
        }
        
        Button(
            onClick = {
                onMarkupChange("")
                onStatusChange("")
                container?.removeAllViews()
                onContainerChange(null)
                logger.clear()
            },
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.clear))
        }
    }
    
    if (status.isNotEmpty()) {
        Text(
            text = status,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
    
    if (events.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "Lifecycle Events:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                events.forEach { event ->
                    Text(
                        text = event,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
    
    container?.let { container ->
        AndroidView(
            factory = { container },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        )
    }
}