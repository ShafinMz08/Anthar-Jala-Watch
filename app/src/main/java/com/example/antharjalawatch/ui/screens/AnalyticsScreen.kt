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
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.antharjalawatch.data.model.*
import com.example.antharjalawatch.ui.components.*
import com.example.antharjalawatch.ui.theme.*
import com.example.antharjalawatch.viewmodel.AppViewModel
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel : AppViewModel,
    onBack    : () -> Unit
) {
    val dark    = viewModel.darkMode
    val summary = remember { DemoData.getSummary() }

    Scaffold(
        topBar = {
            PremiumTopBar(
                title    = "Analytics Dashboard",
                subtitle = "Groundwater Intelligence Platform",
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ── KPI Row ─────────────────────────────────────────────────────
            SectionHeader("Key Performance Indicators", darkMode = dark)
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    label    = "Borewells",
                    value    = "${summary.totalBorewells}",
                    icon     = Icons.Filled.WaterDrop,
                    color    = PrimaryMain,
                    darkMode = dark,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label    = "Avg Depth",
                    value    = "${summary.avgDepth.toInt()}",
                    unit     = "ft",
                    icon     = Icons.Filled.UnfoldMore,
                    color    = StatusWarning,
                    darkMode = dark,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label    = "Avg Yield",
                    value    = "${summary.avgYield.toInt()}",
                    unit     = "gph",
                    icon     = Icons.Filled.Waves,
                    color    = Emerald500,
                    darkMode = dark,
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Donut Chart ─────────────────────────────────────────────────
            SectionHeader(
                title    = "Status Distribution",
                subtitle = "Real-time groundwater health breakdown",
                darkMode = dark
            )
            GlassCard(darkMode = dark) {
                Column(
                    modifier            = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DonutChart(
                        slices = listOf(
                            DonutSlice("Safe",     summary.safeCount.toFloat(),     StatusSafe),
                            DonutSlice("Moderate", summary.moderateCount.toFloat(), StatusModerate),
                            DonutSlice("Warning",  summary.warningCount.toFloat(),  StatusWarning),
                            DonutSlice("Critical", summary.criticalCount.toFloat(), StatusCritical)
                        ),
                        size     = 180.dp,
                        darkMode = dark
                    )
                    Spacer(Modifier.height(16.dp))
                    // Legend
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DonutLegendItem("Safe",     summary.safeCount,     StatusSafe,     dark)
                        DonutLegendItem("Moderate", summary.moderateCount, StatusModerate, dark)
                        DonutLegendItem("Warning",  summary.warningCount,  StatusWarning,  dark)
                        DonutLegendItem("Critical", summary.criticalCount, StatusCritical, dark)
                    }
                }
            }

            // ── Bar Chart: District Depth ───────────────────────────────────
            SectionHeader(
                title    = "District Depth Profile",
                subtitle = "Average borewell depth by district (normalized)",
                darkMode = dark
            )
            GlassCard(darkMode = dark) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val maxDepth = DemoData.districts.maxOf { it.avgDepthFt }.toFloat()
                    val bars = DemoData.districts.map { d ->
                        d.name.take(6) to (d.avgDepthFt / maxDepth)
                    }
                    val barColors = DemoData.districts.map { d ->
                        when (d.riskLevel) {
                            RiskLevel.SAFE     -> StatusSafe
                            RiskLevel.MODERATE -> StatusModerate
                            RiskLevel.WARNING  -> StatusWarning
                            RiskLevel.CRITICAL -> StatusCritical
                        }
                    }
                    SimpleBarChart(
                        bars     = bars,
                        colors   = barColors,
                        darkMode = dark,
                        modifier = Modifier.fillMaxWidth().height(160.dp)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Kolar leads with ${DemoData.districts.first { it.id == "kolar" }.avgDepthFt}ft avg depth — highest in region",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (dark) TextOnDarkSub else TextHint
                        ),
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── Sustainability Rings ─────────────────────────────────────────
            SectionHeader(
                title    = "Sustainability Index",
                subtitle = "AI-computed health score per district",
                darkMode = dark
            )
            GlassCard(darkMode = dark) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val rows = DemoData.districts.chunked(3)
                    rows.forEach { row ->
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            row.forEach { d ->
                                val ringColor = when (d.riskLevel) {
                                    RiskLevel.SAFE     -> StatusSafe
                                    RiskLevel.MODERATE -> StatusModerate
                                    RiskLevel.WARNING  -> StatusWarning
                                    RiskLevel.CRITICAL -> StatusCritical
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier            = Modifier.padding(8.dp)
                                ) {
                                    ProgressRing(
                                        progress = d.sustainabilityScore / 100f,
                                        color    = ringColor,
                                        size     = 74.dp,
                                        label    = "${d.sustainabilityScore}%",
                                        darkMode = dark
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        d.name,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (dark) TextOnDark else TextPrimary,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            // Fill empty slots in last row
                            repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                        if (row != rows.last()) Spacer(Modifier.height(8.dp))
                    }
                }
            }

            // ── Year-wise Drilling Trend ────────────────────────────────────
            SectionHeader(
                title    = "Drilling Timeline",
                subtitle = "New borewells commissioned per year",
                darkMode = dark
            )
            GlassCard(darkMode = dark) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val yearData = DemoData.borewells
                        .groupBy { it.yearOfDig }
                        .filterKeys { it.isNotBlank() }
                        .toSortedMap()
                    val maxCount = yearData.values.maxOfOrNull { it.size }?.toFloat() ?: 1f

                    val bars     = yearData.map { (yr, entries) -> yr.takeLast(2) to (entries.size / maxCount) }
                    val clrs     = bars.indices.map { idx ->
                        val fraction = idx.toFloat() / bars.size.coerceAtLeast(1)
                        lerp(PrimaryLight, StatusWarning, fraction)
                    }

                    SimpleBarChart(
                        bars     = bars,
                        colors   = clrs,
                        darkMode = dark,
                        modifier = Modifier.fillMaxWidth().height(130.dp)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Drilling activity increased 42% from 2019 to 2023 in critical zones",
                        style     = MaterialTheme.typography.labelSmall.copy(
                            color = if (dark) TextOnDarkSub else TextHint
                        ),
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── Risk Comparison Table ───────────────────────────────────────
            SectionHeader("Comparative Risk Analysis", darkMode = dark)
            GlassCard(darkMode = dark) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TableCell("District", darkMode = dark, header = true, weight = 1.4f)
                        TableCell("Depth",    darkMode = dark, header = true)
                        TableCell("Yield",    darkMode = dark, header = true)
                        TableCell("Score",    darkMode = dark, header = true)
                        TableCell("Risk",     darkMode = dark, header = true)
                    }
                    HorizontalDivider(color = if (dark) Navy600 else Color(0xFFE8EEFF), modifier = Modifier.padding(vertical = 6.dp), thickness = 1.dp)
                    DemoData.districts.forEach { d ->
                        val rColor = when (d.riskLevel) {
                            RiskLevel.SAFE     -> StatusSafe
                            RiskLevel.MODERATE -> StatusModerate
                            RiskLevel.WARNING  -> StatusWarning
                            RiskLevel.CRITICAL -> StatusCritical
                        }
                        Row(
                            modifier          = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableCell(d.name,                 darkMode = dark, weight = 1.4f)
                            TableCell("${d.avgDepthFt}ft",    darkMode = dark)
                            TableCell("${d.avgYieldGph}gph",  darkMode = dark)
                            TableCell("${d.sustainabilityScore}%", darkMode = dark, color = rColor)
                            Box(modifier = Modifier.weight(1f)) {
                                StatusBadge(d.riskLevel.label.lowercase())
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

// ── Donut Chart Implementation ────────────────────────────────────────────────

data class DonutSlice(val label: String, val value: Float, val color: Color)

@Composable
private fun DonutChart(
    slices   : List<DonutSlice>,
    size     : Dp      = 180.dp,
    darkMode : Boolean = false
) {
    val total = slices.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)

    // Animate each slice
    val sweepAngles = slices.map { slice ->
        animateFloatAsState(
            targetValue   = (slice.value / total) * 360f,
            animationSpec = tween(1200, easing = FastOutSlowInEasing),
            label         = "donut_${slice.label}"
        ).value
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = this.size.width * 0.18f
            val radius      = (this.size.width - strokeWidth) / 2f
            val topLeft     = Offset(strokeWidth / 2, strokeWidth / 2)
            val arcSize     = Size(radius * 2, radius * 2)
            var startAngle  = -90f

            slices.forEachIndexed { i, slice ->
                drawArc(
                    color      = slice.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngles[i] - 2f, // 2dp gap between segments
                    useCenter  = false,
                    topLeft    = topLeft,
                    size       = arcSize,
                    style      = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
                startAngle += sweepAngles[i]
            }
        }

        // Center label
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "${slices.sumOf { it.value.toInt() }}",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color      = if (darkMode) TextOnDark else TextPrimary
                )
            )
            Text(
                "Borewells",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (darkMode) TextOnDarkSub else TextHint
                )
            )
        }
    }
}

@Composable
private fun DonutLegendItem(label: String, count: Int, color: Color, darkMode: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(Modifier.height(3.dp))
        Text(
            "$count",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = color)
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (darkMode) TextOnDarkSub else TextHint
            )
        )
    }
}

// ── Table helpers ──────────────────────────────────────────────────────────────

@Composable
private fun RowScope.TableCell(
    text     : String,
    darkMode : Boolean,
    header   : Boolean = false,
    weight   : Float   = 1f,
    color    : Color?  = null
) {
    Text(
        text     = text,
        modifier = Modifier.weight(weight),
        style    = if (header)
            MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color      = if (darkMode) TextOnDarkSub else TextHint
            )
        else
            MaterialTheme.typography.labelMedium.copy(
                color = color ?: (if (darkMode) TextOnDark else TextPrimary)
            )
    )
}

// ── Color interpolation helper ────────────────────────────────────────────────
private fun lerp(start: Color, end: Color, fraction: Float): Color {
    val f = fraction.coerceIn(0f, 1f)
    return Color(
        red   = start.red   + (end.red   - start.red)   * f,
        green = start.green + (end.green - start.green) * f,
        blue  = start.blue  + (end.blue  - start.blue)  * f,
        alpha = 1f
    )
}
