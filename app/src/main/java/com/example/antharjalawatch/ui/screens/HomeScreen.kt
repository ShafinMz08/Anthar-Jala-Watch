package com.example.antharjalawatch.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.antharjalawatch.data.model.*
import com.example.antharjalawatch.ui.components.*
import com.example.antharjalawatch.ui.theme.*
import com.example.antharjalawatch.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel   : AppViewModel,
    onLogData   : () -> Unit,
    onViewMap   : () -> Unit,
    onAnalytics : () -> Unit,
    onInsights  : () -> Unit,
    onSettings  : () -> Unit
) {
    val dark = viewModel.darkMode
    val summary = remember { DemoData.getSummary() }
    val districts = viewModel.districts

    // Animated counter
    var counterStarted by remember { mutableStateOf(false) }
    val totalAnim by animateIntAsState(
        targetValue   = if (counterStarted) summary.totalBorewells else 0,
        animationSpec = tween(1400, easing = FastOutSlowInEasing),
        label         = "total"
    )

    LaunchedEffect(Unit) { counterStarted = true }

    Box(modifier = Modifier.fillMaxSize().background(if (dark) Navy900 else SurfaceLight)) {

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            // ── Hero Header ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                if (dark) Navy900 else PrimaryDark,
                                if (dark) Navy700 else PrimaryMain,
                                if (dark) Navy600 else PrimaryLight
                            )
                        )
                    )
            ) {
                // Decorative circles
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .offset(x = 220.dp, y = (-30).dp)
                        .alpha(0.1f)
                        .clip(CircleShape)
                        .background(White)
                )
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .offset(x = (-20).dp, y = 180.dp)
                        .alpha(0.08f)
                        .clip(CircleShape)
                        .background(Aqua200)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 24.dp, end = 24.dp, top = 56.dp),
                ) {
                    // Header row
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Anthar-Jala Watch",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color      = White
                                )
                            )
                            Text(
                                "Groundwater Intelligence Platform",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = White.copy(alpha = 0.75f)
                                )
                            )
                        }
                        IconButton(onClick = onSettings) {
                            Icon(
                                Icons.Filled.Settings,
                                contentDescription = "Settings",
                                tint = White
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Live stats row
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HeroStatCard("$totalAnim", "Borewells", "Total Monitored", Modifier.weight(1f))
                        HeroStatCard("${DemoData.districts.size}", "Districts", "Active Zones",   Modifier.weight(1f))
                        HeroStatCard(
                            "${summary.safeCount + summary.moderateCount}",
                            "Stable", "Within Limits", Modifier.weight(1f)
                        )
                    }
                }

                // Curved bottom edge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(if (dark) Navy900 else SurfaceLight)
                )
            }

            Column(
                modifier            = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                Spacer(Modifier.height(4.dp))

                // ── AI Alert Banner ────────────────────────────────────────
                val criticalDistrict = districts.firstOrNull { it.riskLevel == RiskLevel.CRITICAL }
                if (criticalDistrict != null) {
                    AlertBanner(
                        message  = "⚠️ Critical alert: ${criticalDistrict.name} shows severe aquifer depletion. AI confidence 94%.",
                        darkMode = dark
                    )
                }

                // ── Quick Actions ──────────────────────────────────────────
                SectionHeader("Quick Actions", darkMode = dark)

                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        label    = "Log Data",
                        icon     = Icons.Filled.WaterDrop,
                        color    = PrimaryMain,
                        darkMode = dark,
                        onClick  = onLogData,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        label    = "Water Map",
                        icon     = Icons.Filled.Map,
                        color    = Emerald500,
                        darkMode = dark,
                        onClick  = onViewMap,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        label    = "Analytics",
                        icon     = Icons.Filled.BarChart,
                        color    = StatusWarning,
                        darkMode = dark,
                        onClick  = onAnalytics,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        label    = "Insights",
                        icon     = Icons.Filled.Lightbulb,
                        color    = Aqua400,
                        darkMode = dark,
                        onClick  = onInsights,
                        modifier = Modifier.weight(1f)
                    )
                }

                // ── Sustainability Index ────────────────────────────────────
                SectionHeader(
                    "Sustainability Index",
                    subtitle = "AI-computed groundwater health score",
                    darkMode = dark
                )

                GlassCard(darkMode = dark) {
                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        ProgressRing(
                            progress = summary.sustainabilityIdx / 100f,
                            color    = StatusSafe,
                            size     = 90.dp,
                            label    = "${summary.sustainabilityIdx}%",
                            sublabel = "Health",
                            darkMode = dark
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatusStatRow("Safe",     "${summary.safeCount}",     StatusSafe,     dark)
                            StatusStatRow("Moderate", "${summary.moderateCount}", StatusModerate, dark)
                            StatusStatRow("Warning",  "${summary.warningCount}",  StatusWarning,  dark)
                            StatusStatRow("Critical", "${summary.criticalCount}", StatusCritical, dark)
                        }
                    }
                }

                // ── District Overview ──────────────────────────────────────
                SectionHeader(
                    "District Overview",
                    subtitle = "Tap a district to filter map & data",
                    darkMode = dark
                )

                districts.forEach { district ->
                    DistrictOverviewCard(district = district, darkMode = dark)
                }

                // ── AI Insight Preview ─────────────────────────────────────
                SectionHeader(
                    "AI Recommendations",
                    subtitle = "Predictive groundwater intelligence",
                    darkMode = dark
                )

                DemoData.aiInsights.take(2).forEach { insight ->
                    AIInsightPreviewCard(insight = insight, darkMode = dark)
                }

                TextButton(
                    onClick  = onInsights,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        "View All Insights →",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = PrimaryMain, fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// ── Private composables ───────────────────────────────────────────────────────

@Composable
private fun HeroStatCard(value: String, label: String, sublabel: String, modifier: Modifier) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = White.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier            = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold, color = White
                )
            )
            Text(
                label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = White, fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                sublabel,
                style     = MaterialTheme.typography.labelSmall.copy(color = White.copy(alpha = 0.65f)),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AlertBanner(message: String, darkMode: Boolean) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = StatusCriticalLight),
        border    = BorderStroke(1.dp, StatusCritical.copy(alpha = 0.4f))
    ) {
        Row(
            modifier              = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment     = Alignment.Top
        ) {
            Icon(Icons.Filled.Warning, null, tint = StatusCritical, modifier = Modifier.size(18.dp))
            Text(
                message,
                style     = MaterialTheme.typography.bodySmall.copy(color = StatusCritical),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    label    : String,
    icon     : ImageVector,
    color    : Color,
    darkMode : Boolean,
    onClick  : () -> Unit,
    modifier : Modifier = Modifier
) {
    val bg = if (darkMode) CardDark else CardWhite

    Card(
        onClick   = onClick,
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = bg),
        border    = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(if (darkMode) 0.dp else 2.dp)
    ) {
        Column(
            modifier            = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier        = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Text(
                label,
                style     = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color      = if (darkMode) TextOnDark else TextPrimary
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StatusStatRow(label: String, value: String, color: Color, darkMode: Boolean) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            label,
            style    = MaterialTheme.typography.bodySmall.copy(
                color = if (darkMode) TextOnDarkSub else TextSecondary
            ),
            modifier = Modifier.width(64.dp)
        )
        Text(
            value,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold, color = color
            )
        )
    }
}

@Composable
private fun DistrictOverviewCard(district: District, darkMode: Boolean) {
    val color = when (district.riskLevel) {
        RiskLevel.SAFE     -> StatusSafe
        RiskLevel.MODERATE -> StatusModerate
        RiskLevel.WARNING  -> StatusWarning
        RiskLevel.CRITICAL -> StatusCritical
    }
    val bg = if (darkMode) CardDark else CardWhite

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = bg),
        border    = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(if (darkMode) 0.dp else 2.dp)
    ) {
        Row(
            modifier              = Modifier.padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Left color strip
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        brush = Brush.verticalGradient(listOf(color, color.copy(alpha = 0.4f)))
                    )
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        district.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color      = if (darkMode) TextOnDark else TextPrimary
                        )
                    )
                    StatusBadge(district.riskLevel.label.lowercase())
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DistrictStat("${district.borewellCount}", "Borewells", darkMode)
                    DistrictStat("${district.avgDepthFt} ft", "Avg Depth", darkMode)
                    DistrictStat("${district.avgYieldGph} gph", "Avg Yield", darkMode)
                    DistrictStat("${district.sustainabilityScore}%", "Score", darkMode)
                }
            }
        }
    }
}

@Composable
private fun DistrictStat(value: String, label: String, darkMode: Boolean) {
    Column {
        Text(
            value,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color      = if (darkMode) TextOnDark else PrimaryDark
            )
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (darkMode) TextOnDarkSub else TextHint
            )
        )
    }
}

@Composable
private fun AIInsightPreviewCard(insight: AIInsight, darkMode: Boolean) {
    val color = when (insight.riskLevel) {
        RiskLevel.SAFE     -> StatusSafe
        RiskLevel.MODERATE -> StatusModerate
        RiskLevel.WARNING  -> StatusWarning
        RiskLevel.CRITICAL -> StatusCritical
    }
    val bg = if (darkMode) CardDark else CardWhite

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = bg),
        border    = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(if (darkMode) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Psychology, null, tint = color, modifier = Modifier.size(16.dp))
                    Text(
                        insight.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color      = if (darkMode) TextOnDark else TextPrimary
                        )
                    )
                }
                Surface(shape = RoundedCornerShape(20.dp), color = color.copy(alpha = 0.15f)) {
                    Text(
                        "${insight.confidence}% AI",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style    = MaterialTheme.typography.labelSmall.copy(
                            color = color, fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                insight.description,
                style     = MaterialTheme.typography.bodySmall.copy(
                    color = if (darkMode) TextOnDarkSub else TextSecondary
                ),
                lineHeight = 18.sp
            )
        }
    }
}
