package com.bidease.android.demo.admarkup

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

private val AdMarkupOpenDocumentMimeTypes = arrayOf(
    "text/*",
    "application/xml",
    "application/xhtml+xml",
    "application/json",
    "*/*"
)

private const val MAX_AD_MARKUP_BYTES = 12 * 1024 * 1024

fun readAdMarkupFromUri(context: Context, uri: Uri): String? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            val bytes = input.readBytes()
            if (bytes.size > MAX_AD_MARKUP_BYTES) return null
            bytes.toString(Charsets.UTF_8)
        }
    } catch (_: Exception) {
        null
    }
}

@Composable
fun BrowseAdFileButton(
    onMarkupLoaded: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val errorText = stringResource(R.string.browse_ad_file_error)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val text = readAdMarkupFromUri(context, uri)
        if (text != null) {
            onMarkupLoaded(text)
        } else {
            Toast.makeText(context, errorText, Toast.LENGTH_LONG).show()
        }
    }
    OutlinedButton(
        onClick = { launcher.launch(AdMarkupOpenDocumentMimeTypes) },
        modifier = modifier.then(Modifier.fillMaxWidth())
    ) {
        Text(stringResource(R.string.browse_ad_file))
    }
}