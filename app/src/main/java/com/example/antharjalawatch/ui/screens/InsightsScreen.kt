package com.example.antharjalawatch.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.antharjalawatch.data.model.*
import com.example.antharjalawatch.ui.components.*
import com.example.antharjalawatch.ui.theme.*
import com.example.antharjalawatch.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    viewModel : AppViewModel,
    onBack    : () -> Unit
) {
    val dark    = viewModel.darkMode
    val summary = remember { DemoData.getSummary() }

    var expandedInsight by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            PremiumTopBar(
                title    = "AI Insights",
                subtitle = "Groundwater Intelligence Analysis",
                darkMode = dark,
                onBack   = onBack
            )
        },
        containerColor = if (dark) Navy900 else SurfaceLight
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // ── AI Status Header ────────────────────────────────────────────
            AIStatusHeader(summary = summary, darkMode = dark)

            // ── Critical Alert ──────────────────────────────────────────────
            val criticalDistricts = DemoData.districts.filter { it.riskLevel == RiskLevel.CRITICAL }
            if (criticalDistricts.isNotEmpty()) {
                CriticalZoneAlert(districts = criticalDistricts, darkMode = dark)
            }

            // ── AI Insight Cards ────────────────────────────────────────────
            SectionHeader(
                title    = "AI Analysis Reports",
                subtitle = "Model confidence ≥ 80% on all insights",
                darkMode = dark,
                action   = {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = PrimaryMain.copy(alpha = 0.15f)
                    ) {
                        Text(
                            "LIVE",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style    = MaterialTheme.typography.labelSmall.copy(
                                color      = PrimaryMain,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            )

            DemoData.aiInsights.forEachIndexed { idx, insight ->
                ExpandableInsightCard(
                    insight    = insight,
                    isExpanded = expandedInsight == idx,
                    onToggle   = { expandedInsight = if (expandedInsight == idx) null else idx },
                    darkMode   = dark
                )
            }

            // ── District Risk Matrix ────────────────────────────────────────
            SectionHeader(
                title    = "District Risk Matrix",
                subtitle = "Comparative groundwater health index",
                darkMode = dark
            )
            DistrictRiskMatrix(darkMode = dark)

            // ── Water Trend Summary ─────────────────────────────────────────
            SectionHeader(
                title    = "Groundwater Trends",
                subtitle = "Year-on-year depth & yield analysis",
                darkMode = dark
            )
            WaterTrendCard(darkMode = dark)

            // ── Recommendations ─────────────────────────────────────────────
            SectionHeader(
                title    = "Action Recommendations",
                subtitle = "AI-generated conservation strategies",
                darkMode = dark
            )
            RecommendationsPanel(darkMode = dark)

            // ── Disclaimer ──────────────────────────────────────────────────
            DisclaimerCard(darkMode = dark)

            Spacer(Modifier.height(20.dp))
        }
    }
}

// ── Sub-composables ───────────────────────────────────────────────────────────

@Composable
private fun AIStatusHeader(summary: DemoData.Summary, darkMode: Boolean) {
    GlassCard(darkMode = darkMode, borderGlow = true) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "System Status",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color      = if (darkMode) TextOnDark else TextPrimary
                        )
                    )
                    Text(
                        "Karnataka Groundwater Monitor",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (darkMode) TextOnDarkSub else TextSecondary
                        )
                    )
                }
                // Animated pulse dot
                AnimatedPulse(color = StatusSafe)
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryChip("${summary.totalBorewells}", "Total",    PrimaryMain,    darkMode)
                SummaryChip("${summary.safeCount}",     "Safe",     StatusSafe,     darkMode)
                SummaryChip("${summary.warningCount}",  "Warning",  StatusWarning,  darkMode)
                SummaryChip("${summary.criticalCount}", "Critical", StatusCritical, darkMode)
            }

            Spacer(Modifier.height(16.dp))

            // Overall health bar
            Text(
                "Overall Health Index: ${summary.sustainabilityIdx}%",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color      = if (darkMode) TextOnDark else TextPrimary
                )
            )
            Spacer(Modifier.height(6.dp))

            val healthAnim by animateFloatAsState(
                targetValue   = summary.sustainabilityIdx / 100f,
                animationSpec = tween(1400, easing = FastOutSlowInEasing),
                label         = "health"
            )
            val healthColor = when {
                summary.sustainabilityIdx >= 70 -> StatusSafe
                summary.sustainabilityIdx >= 45 -> StatusModerate
                summary.sustainabilityIdx >= 25 -> StatusWarning
                else                             -> StatusCritical
            }
            LinearProgressIndicator(
                progress      = { healthAnim },
                modifier      = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color         = healthColor,
                trackColor    = if (darkMode) Navy600 else Color(0xFFE8F4FD)
            )
        }
    }
}

@Composable
private fun AnimatedPulse(color: Color) {
    val pulse = rememberInfiniteTransition(label = "pulse")
    val scale by pulse.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.3f,
        animationSpec = infiniteRepeatable(
            animation  = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    Box(
        modifier = Modifier
            .size(12.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun SummaryChip(value: String, label: String, color: Color, darkMode: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color      = color
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
private fun CriticalZoneAlert(districts: List<District>, darkMode: Boolean) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = StatusCriticalLight),
        border    = BorderStroke(1.5.dp, StatusCritical.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Warning, null, tint = StatusCritical, modifier = Modifier.size(20.dp))
                Text(
                    "CRITICAL ALERT — ${districts.size} Zone${if (districts.size > 1) "s" else ""} Require Immediate Action",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color      = StatusCritical
                    )
                )
            }
            Spacer(Modifier.height(8.dp))
            districts.forEach { d ->
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.Top
                ) {
                    Icon(Icons.Filled.LocationOn, null, tint = StatusCritical, modifier = Modifier.size(14.dp))
                    Column {
                        Text(
                            "${d.name} — Avg Depth: ${d.avgDepthFt}ft | Yield: ${d.avgYieldGph}gph | Score: ${d.sustainabilityScore}%",
                            style = MaterialTheme.typography.bodySmall.copy(color = StatusCritical),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandableInsightCard(
    insight    : AIInsight,
    isExpanded : Boolean,
    onToggle   : () -> Unit,
    darkMode   : Boolean
) {
    val color   = when (insight.riskLevel) {
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
        border    = BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(if (darkMode) 0.dp else 2.dp)
    ) {
        Column {
            // Header row (always visible)
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    modifier              = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Box(
                        modifier        = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Psychology, null, tint = color, modifier = Modifier.size(18.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            insight.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color      = if (darkMode) TextOnDark else TextPrimary
                            )
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            StatusBadge(insight.riskLevel.label.lowercase())
                            Text(
                                "AI ${insight.confidence}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (darkMode) TextOnDarkSub else TextHint
                                )
                            )
                        }
                    }
                }
                Icon(
                    if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    null,
                    tint     = if (darkMode) TextOnDarkSub else TextHint,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Expandable content
            AnimatedVisibility(
                visible = isExpanded,
                enter   = expandVertically() + fadeIn(),
                exit    = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(color = if (darkMode) Navy600 else Color(0xFFE8EEFF), thickness = 1.dp)

                    Text(
                        insight.description,
                        style     = MaterialTheme.typography.bodySmall.copy(
                            color = if (darkMode) TextOnDarkSub else TextSecondary
                        ),
                        lineHeight = 18.sp
                    )

                    // Confidence bar
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "Model Confidence",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (darkMode) TextOnDarkSub else TextHint
                            ),
                            modifier = Modifier.width(100.dp)
                        )
                        val confAnim by animateFloatAsState(
                            targetValue   = insight.confidence / 100f,
                            animationSpec = tween(900, easing = FastOutSlowInEasing),
                            label         = "conf"
                        )
                        LinearProgressIndicator(
                            progress   = { confAnim },
                            modifier   = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color      = color,
                            trackColor = if (darkMode) Navy600 else Color(0xFFE8F4FD)
                        )
                        Text(
                            "${insight.confidence}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold, color = color
                            )
                        )
                    }

                    // Action item
                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        shape     = RoundedCornerShape(10.dp),
                        colors    = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier              = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment     = Alignment.Top
                        ) {
                            Icon(Icons.Filled.Task, null, tint = color, modifier = Modifier.size(14.dp))
                            Text(
                                "Action: ${insight.actionItem}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color      = color,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DistrictRiskMatrix(darkMode: Boolean) {
    GlassCard(darkMode = darkMode) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("District", "Score", "Depth", "Status").forEach { col ->
                    Text(
                        col,
                        style    = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color      = if (darkMode) TextOnDarkSub else TextHint
                        ),
                        modifier = Modifier.weight(if (col == "District") 1.5f else 1f)
                    )
                }
            }
            HorizontalDivider(
                modifier  = Modifier.padding(vertical = 8.dp),
                color     = if (darkMode) Navy600 else Color(0xFFE8EEFF),
                thickness = 1.dp
            )
            DemoData.districts.forEach { d ->
                val color = when (d.riskLevel) {
                    RiskLevel.SAFE     -> StatusSafe
                    RiskLevel.MODERATE -> StatusModerate
                    RiskLevel.WARNING  -> StatusWarning
                    RiskLevel.CRITICAL -> StatusCritical
                }
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        d.name,
                        style    = MaterialTheme.typography.labelMedium.copy(
                            color = if (darkMode) TextOnDark else TextPrimary
                        ),
                        modifier = Modifier.weight(1.5f)
                    )
                    Text(
                        "${d.sustainabilityScore}%",
                        style    = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold, color = color
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "${d.avgDepthFt}ft",
                        style    = MaterialTheme.typography.labelMedium.copy(
                            color = if (darkMode) TextOnDarkSub else TextSecondary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        StatusBadge(d.riskLevel.label.lowercase())
                    }
                }
            }
        }
    }
}

@Composable
private fun WaterTrendCard(darkMode: Boolean) {
    GlassCard(darkMode = darkMode) {
        Column(modifier = Modifier.padding(16.dp)) {
            val bars = listOf(
                "2019" to 0.72f,
                "2020" to 0.68f,
                "2021" to 0.59f,
                "2022" to 0.48f,
                "2023" to 0.43f,
                "2024" to 0.38f,
                "2025" to 0.35f
            )
            val barColors = bars.map { (_, v) ->
                when {
                    v >= 0.6f -> StatusSafe
                    v >= 0.45f -> StatusModerate
                    v >= 0.30f -> StatusWarning
                    else        -> StatusCritical
                }
            }

            Text(
                "Average Yield Index (Normalized)",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color      = if (darkMode) TextOnDark else TextPrimary
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Karnataka districts combined — 2019 to 2025",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (darkMode) TextOnDarkSub else TextHint
                )
            )
            Spacer(Modifier.height(16.dp))

            SimpleBarChart(
                bars     = bars,
                colors   = barColors,
                darkMode = darkMode,
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )

            Spacer(Modifier.height(12.dp))

            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(10.dp),
                colors    = CardDefaults.cardColors(
                    containerColor = StatusCritical.copy(alpha = 0.08f)
                )
            ) {
                Row(
                    modifier              = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.TrendingDown, null, tint = StatusCritical, modifier = Modifier.size(14.dp))
                    Text(
                        "AI Forecast: Continued 6–8% decline projected if extraction rates unchanged.",
                        style     = MaterialTheme.typography.labelSmall.copy(color = StatusCritical),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

data class Recommendation(val icon: ImageVector, val color: Color, val title: String, val description: String)

@Composable
private fun RecommendationsPanel(darkMode: Boolean) {
    val recommendations = listOf(
        Recommendation(Icons.Filled.WaterDrop,  PrimaryMain,    "Mandatory Rainwater Harvesting",  "Implement RWH structures in all new constructions above 2000 sq.ft in critical zones."),
        Recommendation(Icons.Filled.Park,       Emerald500,     "Urban Greening Initiative",        "Plant 50,000+ native trees in Kolar and Tumakuru to restore vadose zone permeability."),
        Recommendation(Icons.Filled.Engineering,StatusWarning,  "Borewell Depth Regulation",        "Cap new borewell permits at 500ft in Bengaluru. Deeper drilling requires CGWB approval."),
        Recommendation(Icons.Filled.Recycling,  Teal500,        "Treated Water Reuse Programme",    "Divert 30% of treated sewage water to recharge wells in Bengaluru South zones."),
        Recommendation(Icons.Filled.MonitorHeart,StatusModerate,"Community Monitoring Network",     "Deploy IoT water level sensors at 200 pilot borewells for real-time data collection.")
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        recommendations.forEachIndexed { idx, (icon, color, title, desc) ->
            val bg = if (darkMode) CardDark else CardWhite
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(14.dp),
                colors    = CardDefaults.cardColors(containerColor = bg),
                border    = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
                elevation = CardDefaults.cardElevation(if (darkMode) 0.dp else 2.dp)
            ) {
                Row(
                    modifier              = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment     = Alignment.Top
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
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "${idx + 1}.",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = color, fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color      = if (darkMode) TextOnDark else TextPrimary
                                )
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            desc,
                            style     = MaterialTheme.typography.bodySmall.copy(
                                color = if (darkMode) TextOnDarkSub else TextSecondary
                            ),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DisclaimerCard(darkMode: Boolean) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(
            containerColor = if (darkMode) Navy800 else Color(0xFFF0F4FF)
        )
    ) {
        Text(
            "ℹ️  Data is sourced from community submissions and validated demo datasets. " +
                    "AI predictions use threshold-based models. For official data refer to " +
                    "Central Ground Water Board (CGWB) — cgwb.gov.in",
            modifier  = Modifier.padding(14.dp),
            style     = MaterialTheme.typography.labelSmall.copy(
                color = if (darkMode) TextOnDarkSub else TextHint
            ),
            lineHeight = 16.sp
        )
    }
}
