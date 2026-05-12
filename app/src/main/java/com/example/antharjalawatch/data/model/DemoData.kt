package com.example.antharjalawatch.data.model

object DemoData {
    val districts = listOf(
        District("bengaluru_u", "Bengaluru Urban", 120, 450, 120, 65, RiskLevel.MODERATE),
        District("bengaluru_r", "Bengaluru Rural", 85, 600, 80, 45, RiskLevel.WARNING),
        District("kolar", "Kolar", 240, 1200, 30, 15, RiskLevel.CRITICAL),
        District("tumakuru", "Tumakuru", 95, 550, 95, 55, RiskLevel.MODERATE),
        District("mysuru", "Mysuru", 70, 300, 200, 85, RiskLevel.SAFE)
    )

    val borewells = listOf(
        BoreholeEntry("1", "450", "120", "2020", 12.9716, 77.5946, "Bengaluru Urban", "moderate"),
        BoreholeEntry("2", "1200", "30", "2022", 13.1373, 78.1354, "Kolar", "critical")
    )

    val aiInsights = listOf(
        AIInsight("Groundwater Depletion Alert", "Rapid decline in water table observed in Kolar.", RiskLevel.CRITICAL, 94, "Implement rainwater harvesting immediately."),
        AIInsight("Sustainability Forecast", "Bengaluru Urban shows positive trends in recharge.", RiskLevel.SAFE, 88, "Maintain current conservation efforts.")
    )

    data class Summary(
        val totalBorewells: Int,
        val avgDepth: Double,
        val avgYield: Double,
        val safeCount: Int,
        val moderateCount: Int,
        val warningCount: Int,
        val criticalCount: Int,
        val sustainabilityIdx: Int
    )

    fun getSummary() = Summary(
        totalBorewells = 150,
        avgDepth = 580.0,
        avgYield = 110.0,
        safeCount = 45,
        moderateCount = 60,
        warningCount = 25,
        criticalCount = 20,
        sustainabilityIdx = 62
    )
}

data class AIInsight(
    val title: String,
    val description: String,
    val riskLevel: RiskLevel,
    val confidence: Int,
    val actionItem: String = ""
)

fun generateAIInsight(districtName: String): AIInsight {
    return AIInsight(
        title = "Groundwater Depletion Alert",
        description = "Rapid decline in water table observed in $districtName.",
        riskLevel = RiskLevel.WARNING,
        confidence = 85,
        actionItem = "Implement rainwater harvesting immediately."
    )
}
