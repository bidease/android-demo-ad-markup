package com.bidease.android.demo.admarkup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bidease.mobile.interstitialads.InterstitialController
import kotlinx.coroutines.launch

@Composable
fun InterstitialSection(
    markup: String,
    onMarkupChange: (String) -> Unit,
    status: String,
    onStatusChange: (String) -> Unit,
    controller: InterstitialController?,
    onControllerChange: (InterstitialController?) -> Unit,
    testMarkup: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = rememberAdLifecycleLogger(context)
    val events by logger.collectEventsAsState()
    
    Text(
        text = "Interstitial Ad",
        style = MaterialTheme.typography.titleLarge
    )
    
    OutlinedTextField(
        value = markup,
        onValueChange = onMarkupChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Interstitial Markup") },
        placeholder = { Text("Paste HTML/VAST/MRAID markup here") },
        minLines = 3,
        maxLines = 5
    )
    
    Button(
        onClick = { onMarkupChange(testMarkup) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.test_interstitial))
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
                        val newController = renderInterstitial(
                            context = context,
                            markup = markupToUse,
                            onDisplayed = { onStatusChange("Displayed") },
                            onFailed = { error -> onStatusChange("Failed: $error") },
                            onClicked = { onStatusChange("Clicked") },
                            onClosed = { onStatusChange("Closed") },
                            logger = logger
                        )
                        onControllerChange(newController)
                    } catch (e: Exception) {
                        onStatusChange("Error: ${e.message}")
                    }
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.show_interstitial))
        }
        
        Button(
            onClick = {
                onMarkupChange("")
                onStatusChange("")
                controller?.destroy()
                onControllerChange(null)
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
}