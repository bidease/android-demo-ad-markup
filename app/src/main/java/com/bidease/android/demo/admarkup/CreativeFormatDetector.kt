package com.bidease.android.demo.admarkup

object CreativeFormatDetector {
    enum class Format {
        HTML,
        VAST,
        MRAID,
        UNKNOWN
    }

    fun detectFormat(markup: String): Format {
        val trimmed = markup.trim()
        
        return when {
            trimmed.startsWith("<VAST", ignoreCase = true) -> Format.VAST
            trimmed.startsWith("<?xml") && trimmed.contains("VAST", ignoreCase = true) -> Format.VAST
            trimmed.contains("<mraid", ignoreCase = true) || 
            trimmed.contains("mraid.js", ignoreCase = true) ||
            trimmed.contains("mraid://", ignoreCase = true) -> Format.MRAID
            trimmed.startsWith("<", ignoreCase = true) -> Format.HTML
            else -> Format.UNKNOWN
        }
    }

    fun validateMarkup(markup: String): ValidationResult {
        val format = detectFormat(markup)
        
        return when (format) {
            Format.VAST -> ValidationResult(false, "Unsupported format: VAST. Supported formats: HTML and MRAID")
            Format.HTML -> validateHTML(markup)
            Format.MRAID -> validateMRAID(markup)
            Format.UNKNOWN -> ValidationResult(false, "Unknown format. Supported formats: HTML and MRAID")
        }
    }

    private fun validateHTML(markup: String): ValidationResult {
        val trimmed = markup.trim()
        
        return when {
            trimmed.isEmpty() -> ValidationResult(false, "HTML markup is empty")
            !trimmed.startsWith("<") -> ValidationResult(false, "Invalid HTML: must start with <")
            !trimmed.contains(">") -> ValidationResult(false, "Invalid HTML: missing closing tag")
            else -> ValidationResult(true, "Valid HTML")
        }
    }

    private fun validateMRAID(markup: String): ValidationResult {
        val trimmed = markup.trim()
        
        return when {
            trimmed.isEmpty() -> ValidationResult(false, "MRAID markup is empty")
            !trimmed.contains("mraid", ignoreCase = true) -> 
                ValidationResult(false, "Invalid MRAID: missing mraid reference")
            else -> ValidationResult(true, "Valid MRAID")
        }
    }

    data class ValidationResult(
        val isValid: Boolean,
        val message: String
    )
}