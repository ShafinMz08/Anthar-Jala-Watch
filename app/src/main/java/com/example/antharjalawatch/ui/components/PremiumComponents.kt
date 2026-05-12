package com.example.antharjalawatch.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.antharjalawatch.data.model.BoreholeEntry
import com.example.antharjalawatch.ui.theme.*

// ── Status helpers ────────────────────────────────────────────────────────────

fun riskColor(status: String): Color = when (status) {
    "safe"     -> StatusSafe
    "moderate" -> StatusModerate
    "warning"  -> StatusWarning
    else       -> StatusCritical
}
fun riskBg(status: String): Color = when (status) {
    "safe"     -> StatusSafeLight
    "moderate" -> StatusModerateLight
    "warning"  -> StatusWarningLight
    else       -> StatusCriticalLight
}
fun riskGlow(status: String): Color = when (status) {
    "safe"     -> StatusSafeGlow
    "moderate" -> StatusModerateGlow
    "warning"  -> StatusWarningGlow
    else       -> StatusCriticalGlow
}
fun riskEmoji(status: String): String = when (status) {
    "safe"     -> "🟢"
    "moderate" -> "🟡"
    "warning"  -> "🟠"
    else       -> "🔴"
}
fun riskLabel(status: String): String = when (status) {
    "safe"     -> "Safe"
    "moderate" -> "Moderate"
    "warning"  -> "Warning"
    else       -> "Critical"
}

// ── Glassmorphism Card ────────────────────────────────────────────────────────
@Composable
fun GlassCard(
    modifier    : Modifier = Modifier,
    darkMode    : Boolean  = false,
    borderGlow  : Boolean  = false,
    content     : @Composable ColumnScope.() -> Unit
) {
    val bg     = if (darkMode) CardDarkGlass else CardLightGlass
    val border = if (borderGlow) BorderGlow else DividerColor

    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = bg),
        border    = BorderStroke(1.dp, border),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(content = content)
    }
}

// ── Gradient Background Box ───────────────────────────────────────────────────
@Composable
fun GradientBox(
    modifier  : Modifier = Modifier,
    darkMode  : Boolean  = false,
    content   : @Composable BoxScope.() -> Unit
) {
    val gradient = if (darkMode) {
        Brush.verticalGradient(listOf(Navy900, Navy800, Navy700))
    } else {
        Brush.verticalGradient(listOf(PrimaryDark, PrimaryMain, PrimaryLight))
    }
    Box(
        modifier = modifier.background(gradient),
        content  = content
    )
}

// ── Status Badge ──────────────────────────────────────────────────────────────
@Composable
fun StatusBadge(
    status   : String,
    modifier : Modifier = Modifier,
    showGlow : Boolean = false
) {
    val color = riskColor(status)
    val bg    = riskBg(status)

    Surface(
        modifier = modifier,
        shape    = RoundedCornerShape(20.dp),
        color    = bg
    ) {
        Row(
            modifier              = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                riskLabel(status),
                style = MaterialTheme.typography.labelSmall.copy(
                    color      = color,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

// ── Metric Card ───────────────────────────────────────────────────────────────
@Composable
fun MetricCard(
    label    : String,
    value    : String,
    unit     : String    = "",
    icon     : ImageVector? = null,
    color    : Color     = PrimaryMain,
    darkMode : Boolean   = false,
    modifier : Modifier  = Modifier
) {
    GlassCard(modifier = modifier, darkMode = darkMode) {
        Column(
            modifier            = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (icon != null) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = color,
                    modifier           = Modifier.size(20.dp)
                )
                Spacer(Modifier.height(8.dp))
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    value,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color      = color
                    )
                )
                if (unit.isNotBlank()) {
                    Spacer(Modifier.width(3.dp))
                    Text(
                        unit,
                        style    = MaterialTheme.typography.labelSmall,
                        color    = if (darkMode) TextOnDarkSub else TextSecondary,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (darkMode) TextOnDarkSub else TextSecondary
                )
            )
        }
    }
}

// ── Animated Progress Ring ─────────────────────────────────────────────────────
@Composable
fun ProgressRing(
    progress  : Float,
    color     : Color,
    size      : Dp     = 80.dp,
    label     : String = "",
    sublabel  : String = "",
    darkMode  : Boolean = false
) {
    val animatedProgress by animateFloatAsState(
        targetValue    = progress.coerceIn(0f, 1f),
        animationSpec  = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label          = "progress"
    )
    val trackColor = if (darkMode) Navy600 else Color(0xFFE8F4FD)

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke     = size.toPx() * 0.12f
            val diameter   = size.toPx() - stroke
            val topLeft    = Offset(stroke / 2, stroke / 2)
            val arcSize    = androidx.compose.ui.geometry.Size(diameter, diameter)

            // Track arc
            drawArc(
                color      = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter  = false,
                topLeft    = topLeft,
                size       = arcSize,
                style      = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = stroke,
                    cap   = StrokeCap.Round
                )
            )
            // Progress arc
            drawArc(
                color      = color,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter  = false,
                topLeft    = topLeft,
                size       = arcSize,
                style      = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = stroke,
                    cap   = StrokeCap.Round
                )
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                label,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color      = color
                )
            )
            if (sublabel.isNotBlank()) {
                Text(
                    sublabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (darkMode) TextOnDarkSub else TextSecondary
                    )
                )
            }
        }
    }
}

// ── Simple Bar Chart ──────────────────────────────────────────────────────────
@Composable
fun SimpleBarChart(
    bars     : List<Pair<String, Float>>,   // label to value (0f–1f normalized)
    colors   : List<Color>,
    darkMode : Boolean = false,
    modifier : Modifier = Modifier
) {
    Row(
        modifier              = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment     = Alignment.Bottom
    ) {
        bars.forEachIndexed { index, (label, rawValue) ->
            val value = rawValue.coerceIn(0f, 1f)
            val animH by animateFloatAsState(
                targetValue   = value,
                animationSpec = tween(1000, delayMillis = index * 150, easing = FastOutSlowInEasing),
                label         = "bar$index"
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier            = Modifier.weight(1f)
            ) {
                Text(
                    "${(rawValue * 100).toInt()}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color      = colors.getOrElse(index) { PrimaryMain }
                    )
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height((120 * animH).dp)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    colors.getOrElse(index) { PrimaryMain },
                                    colors.getOrElse(index) { PrimaryMain }.copy(alpha = 0.5f)
                                )
                            )
                        )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (darkMode) TextOnDarkSub else TextSecondary
                    )
                )
            }
        }
    }
}

// ── Shimmer Loading ────────────────────────────────────────────────────────────
@Composable
fun ShimmerCard(
    modifier : Modifier = Modifier,
    darkMode : Boolean  = false
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shimmerX by transition.animateFloat(
        initialValue   = -1000f,
        targetValue    = 1000f,
        animationSpec  = infiniteRepeatable(
            animation  = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerX"
    )

    val shimmerBrush = Brush.linearGradient(
        colors      = if (darkMode) listOf(Navy700, Navy500, Navy700)
        else listOf(Color(0xFFE8EEFF), Color(0xFFCDD5F0), Color(0xFFE8EEFF)),
        start       = Offset(shimmerX, 0f),
        end         = Offset(shimmerX + 600f, 300f)
    )

    Card(
        modifier  = modifier
            .fillMaxWidth()
            .height(80.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(shimmerBrush))
    }
}

// ── Borehole Entry Card ────────────────────────────────────────────────────────
@Composable
fun BoreholeCard(
    entry    : BoreholeEntry,
    darkMode : Boolean  = false,
    modifier : Modifier = Modifier
) {
    val color = riskColor(entry.waterStatus)
    val bg    = if (darkMode) CardDark else CardWhite

    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = bg),
        border    = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(if (darkMode) 0.dp else 2.dp)
    ) {
        Row(
            modifier              = Modifier.padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Status indicator strip
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        entry.district,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color      = if (darkMode) TextOnDark else TextPrimary
                        )
                    )
                    StatusBadge(entry.waterStatus)
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MiniStat("Depth", "${entry.depth} ft", darkMode)
                    MiniStat("Yield", "${entry.yield} gph", darkMode)
                    MiniStat("Year",  entry.yearOfDig, darkMode)
                }
            }
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String, darkMode: Boolean) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (darkMode) TextOnDarkSub else TextHint
            )
        )
        Text(
            value,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                color      = if (darkMode) TextOnDark else TextPrimary
            )
        )
    }
}

// ── Section Header ────────────────────────────────────────────────────────────
@Composable
fun SectionHeader(
    title    : String,
    subtitle : String  = "",
    darkMode : Boolean = false,
    action   : (@Composable () -> Unit)? = null
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color      = if (darkMode) TextOnDark else TextPrimary
                )
            )
            if (subtitle.isNotBlank()) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (darkMode) TextOnDarkSub else TextSecondary
                    )
                )
            }
        }
        action?.invoke()
    }
}

// ── Premium Top App Bar ────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumTopBar(
    title    : String,
    subtitle : String = "",
    darkMode : Boolean = false,
    onBack   : (() -> Unit)? = null,
    actions  : (@Composable RowScope.() -> Unit)? = null
) {
    val bg = if (darkMode) Navy900 else PrimaryDark

    TopAppBar(
        title = {
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color      = White
                    )
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = White.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = White
                    )
                }
            }
        },
        actions = { actions?.invoke(this) },
        colors  = TopAppBarDefaults.topAppBarColors(containerColor = bg)
    )
}
