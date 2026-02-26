package com.bidease.android.demo.admarkup

import android.graphics.drawable.GradientDrawable
import android.text.method.ScrollingMovementMethod
import android.widget.EditText
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.addTextChangedListener

class MarkupHolder {
    var text: String = ""
}

@Composable
fun MarkupEditField(
    holder: MarkupHolder,
    editTextRef: MutableState<EditText?>,
    modifier: Modifier = Modifier,
    label: String? = null,
    minLines: Int = 3
) {
    val outlineColor = MaterialTheme.colorScheme.outline.toArgb()
    val containerColor = MaterialTheme.colorScheme.surface.toArgb()
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val hintColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f).toArgb()
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { 1.dp.toPx() }.toInt()
    val cornerRadiusPx = with(density) { 12.dp.toPx() }
    val paddingHorizontalPx = with(density) { 16.dp.toPx() }.toInt()
    val paddingVerticalPx = with(density) { 16.dp.toPx() }.toInt()
    val outlineBackground = GradientDrawable().apply {
        setShape(GradientDrawable.RECTANGLE)
        setCornerRadius(cornerRadiusPx)
        setStroke(strokeWidthPx, outlineColor)
        setColor(containerColor)
    }
    if (label != null) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
    AndroidView(
        factory = { context ->
            EditText(context).apply {
                setTextIsSelectable(true)
                setHorizontallyScrolling(false)
                setSingleLine(false)
                movementMethod = ScrollingMovementMethod()
                isVerticalScrollBarEnabled = true
                setText(holder.text)
                setPadding(paddingHorizontalPx, paddingVerticalPx, paddingHorizontalPx, paddingVerticalPx)
                addTextChangedListener { s ->
                    holder.text = s?.toString() ?: ""
                }
                editTextRef.value = this
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = (minLines * 24).dp, max = 200.dp),
        update = { editText ->
            editText.background = outlineBackground
            editText.setTextColor(textColor)
            editText.setHintTextColor(hintColor)
            if (editText.text.toString() != holder.text) {
                editText.setText(holder.text)
            }
        }
    )
}