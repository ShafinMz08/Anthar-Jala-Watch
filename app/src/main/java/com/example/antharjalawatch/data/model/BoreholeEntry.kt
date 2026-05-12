package com.example.antharjalawatch.data.model

/**
 * BoreholeEntry — the core data model for a single borewell record.
 */
data class BoreholeEntry(
    val id          : String = "",
    val depth       : String = "",   // feet
    val yield       : String = "",   // gallons per hour
    val yearOfDig   : String = "",
    val latitude    : Double = 0.0,
    val longitude   : Double = 0.0,
    val district    : String = "Bengaluru",
    val waterStatus : String = "moderate",
    val timestamp   : Long   = System.currentTimeMillis()
) {
    /** Derives status from the yield-to-depth ratio. */
    fun computeStatus(): String {
        val d = depth.toDoubleOrNull() ?: return "moderate"
        val y = yield.toDoubleOrNull() ?: return "moderate"
        if (d <= 0) return "moderate"
        val ratio = y / d
        return when {
            ratio >= 0.4  -> "safe"
            ratio >= 0.15 -> "moderate"
            else           -> "critical"
        }
    }
}

/** Extension to provide AI insights for a specific borehole. */
fun BoreholeEntry.generateAIInsight(): String {
    return when (waterStatus) {
        "safe"     -> "Aquifer levels are stable. Recommended for regular monitoring."
        "moderate" -> "Moderate depletion detected. Consider implementing water-saving measures."
        "warning"  -> "Significant drop in yield. AI suggests a potential recharge zone nearby."
        else       -> "CRITICAL: Severe depletion. AI recommends immediate rainwater harvesting."
    }
}

// ── Sample / Mock Data ────────────────────────────────────────────────────────
object SampleData {
    val borewells = listOf(
        BoreholeEntry(
            id = "s1", depth = "350", yield = "180",
            yearOfDig = "2015", latitude = 12.9716, longitude = 77.5946,
            district = "Bengaluru Central", waterStatus = "safe"
        ),
        BoreholeEntry(
            id = "s2", depth = "500", yield = "60",
            yearOfDig = "2019", latitude = 13.0012, longitude = 77.5773,
            district = "Bengaluru North", waterStatus = "moderate"
        )
    )
}
