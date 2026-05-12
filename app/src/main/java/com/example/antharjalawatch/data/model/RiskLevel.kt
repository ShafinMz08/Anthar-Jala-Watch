package com.example.antharjalawatch.data.model

import androidx.compose.ui.graphics.Color

enum class RiskLevel(val label: String, val color: Color) {
    SAFE("Safe", Color(0xFF4CAF50)),
    MODERATE("Moderate", Color(0xFFFFC107)),
    WARNING("Warning", Color(0xFFFF9800)),
    CRITICAL("Critical", Color(0xFFF44336))
}
