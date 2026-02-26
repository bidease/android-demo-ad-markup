package com.bidease.android.demo.admarkup

import android.content.Context
import android.widget.EditText
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
fun RewardedSection(
    holder: MarkupHolder,
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
    val editTextRef = remember { mutableStateOf<EditText?>(null) }
    
    Text(
        text = "Rewarded Ad",
        style = MaterialTheme.typography.titleLarge
    )
    
    MarkupEditField(
        holder = holder,
        editTextRef = editTextRef,
        modifier = Modifier.fillMaxWidth(),
        label = "Rewarded Markup",
        minLines = 3
    )
    
    Button(
        onClick = {
            holder.text = testMarkup
            editTextRef.value?.setText(testMarkup)
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.test_rewarded))
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = {
                scope.launch {
                    try {
                        val markupToUse = holder.text.ifBlank { testMarkup }
                        onStatusChange("Loading...")
                        val newController = renderRewarded(
                            context = context,
                            markup = markupToUse,
                            onDisplayed = { onStatusChange("Displayed") },
                            onFailed = { error -> onStatusChange("Failed: $error") },
                            onClicked = { onStatusChange("Clicked") },
                            onClosed = { onStatusChange("Closed") },
                            onRewarded = { onStatusChange("Rewarded!") },
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
            Text(stringResource(R.string.show_rewarded))
        }
        
        Button(
            onClick = {
                holder.text = ""
                editTextRef.value?.setText("")
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