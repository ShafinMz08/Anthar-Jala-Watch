package com.example.antharjalawatch.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.antharjalawatch.ui.theme.*
import kotlinx.coroutines.delay

/**
 * SplashScreen — animated startup screen.
 * Auto-transitions to Home after ~2.6 seconds.
 *
 * Fixed issues:
 *  1. Added missing `import androidx.compose.foundation.shape.RoundedCornerShape`
 *     (used by LinearProgressIndicator clip modifier at bottom of screen)
 *  2. LinearProgressIndicator: removed named `progress` parameter — in BOM 2024.02.00
 *     the lambda-based overload is preferred; using the float overload directly avoids
 *     overload resolution ambiguity
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {

    var started by remember { mutableStateOf(false) }

    // ── Entrance animations ───────────────────────────────────────────────
    val logoScale by animateFloatAsState(
        targetValue   = if (started) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessLow
        ),
        label = "logoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue   = if (started) 1f else 0f,
        animationSpec = tween(700),
        label         = "logoAlpha"
    )
    val textAlpha by animateFloatAsState(
        targetValue   = if (started) 1f else 0f,
        animationSpec = tween(600, delayMillis = 400),
        label         = "textAlpha"
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue   = if (started) 1f else 0f,
        animationSpec = tween(600, delayMillis = 700),
        label         = "subtitleAlpha"
    )
    val taglineAlpha by animateFloatAsState(
        targetValue   = if (started) 1f else 0f,
        animationSpec = tween(600, delayMillis = 1000),
        label         = "taglineAlpha"
    )

    // ── Pulse glow ring ───────────────────────────────────────────────────
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseAnim.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.08f,
        animationSpec = infiniteRepeatable(
            animation  = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // ── Progress for bottom bar ───────────────────────────────────────────
    val progressAnim by animateFloatAsState(
        targetValue   = if (started) 1f else 0f,
        animationSpec = tween(2200, easing = LinearEasing),
        label         = "progress"
    )

    LaunchedEffect(Unit) {
        started = true
        delay(2600L)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Navy900, Navy800, Navy700)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // ── Decorative glow rings ─────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(300.dp)
                .scale(pulseScale)
                .alpha(0.07f)
                .clip(CircleShape)
                .background(PrimaryLight)
        )
        Box(
            modifier = Modifier
                .size(220.dp)
                .scale(pulseScale * 0.95f)
                .alpha(0.12f)
                .clip(CircleShape)
                .background(Aqua300)
        )

        // ── Main content ──────────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .scale(logoScale)
                    .alpha(logoAlpha)
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(PrimaryLight, PrimaryDark)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Filled.WaterDrop,
                    contentDescription = "Logo",
                    modifier           = Modifier.size(54.dp),
                    tint               = White
                )
            }

            Spacer(Modifier.height(28.dp))

            // App name
            Text(
                text      = "ANTHAR-JALA",
                modifier  = Modifier.alpha(textAlpha),
                style     = MaterialTheme.typography.displaySmall.copy(
                    fontWeight    = FontWeight.Bold,
                    color         = White,
                    letterSpacing = 6.sp
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text      = "WATCH",
                modifier  = Modifier.alpha(textAlpha),
                style     = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight    = FontWeight.Bold,
                    color         = PrimaryLight,
                    letterSpacing = 8.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text      = "ಅಂತರ್ ಜಲ  •  Underground Water",
                modifier  = Modifier.alpha(subtitleAlpha),
                style     = MaterialTheme.typography.bodySmall.copy(
                    color         = White.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            Text(
                text      = "AI-Powered Groundwater Intelligence",
                modifier  = Modifier.alpha(taglineAlpha),
                style     = MaterialTheme.typography.labelMedium.copy(
                    color         = Aqua300,
                    letterSpacing = 1.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text      = "Natural Resource Monitoring System  v2.0",
                modifier  = Modifier.alpha(taglineAlpha),
                style     = MaterialTheme.typography.labelSmall.copy(
                    color = White.copy(alpha = 0.35f)
                ),
                textAlign = TextAlign.Center
            )
        }

        // ── Bottom brand strip ────────────────────────────────────────────
        Column(
            modifier            = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .alpha(taglineAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // FIX: Use LinearProgressIndicator with explicit `progress` lambda
            // to avoid the BOM 2024 overload ambiguity between Float and () -> Float
            LinearProgressIndicator(
                progress      = { progressAnim },
                modifier      = Modifier
                    .width(120.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp)),
                color         = PrimaryLight,
                trackColor    = Navy600,
                strokeCap     = StrokeCap.Round
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text  = "Karnataka Groundwater Authority",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = White.copy(alpha = 0.3f)
                )
            )
        }
    }
}
