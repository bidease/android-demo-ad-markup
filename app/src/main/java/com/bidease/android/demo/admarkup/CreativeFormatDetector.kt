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
            Format.VAST -> validateVAST(markup)
            Format.HTML -> validateHTML(markup)
            Format.MRAID -> validateMRAID(markup)
            Format.UNKNOWN -> ValidationResult(false, "Unknown format. Supported formats: HTML, VAST, MRAID")
        }
    }

    private fun validateVAST(markup: String): ValidationResult {
        val trimmed = markup.trim()
        
        return when {
            trimmed.isEmpty() -> ValidationResult(false, "VAST markup is empty")
            !trimmed.contains("<VAST", ignoreCase = true) && !trimmed.contains("<?xml") -> 
                ValidationResult(false, "Invalid VAST: missing VAST root element")
            !trimmed.contains("<Ad>", ignoreCase = true) && !trimmed.contains("<Ad ", ignoreCase = true) -> 
                ValidationResult(false, "Invalid VAST: missing Ad element")
            else -> ValidationResult(true, "Valid VAST")
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