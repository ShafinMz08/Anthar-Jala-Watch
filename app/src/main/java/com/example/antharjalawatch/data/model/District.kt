package com.example.antharjalawatch.data.model

data class District(
    val id: String,
    val name: String,
    val borewellCount: Int,
    val avgDepthFt: Int,
    val avgYieldGph: Int,
    val sustainabilityScore: Int,
    val riskLevel: RiskLevel
)
