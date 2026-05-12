package com.example.antharjalawatch.ui.screens

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
import com.example.antharjalawatch.ui.components.*
import com.example.antharjalawatch.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    // About screen doesn't need dark mode toggle; uses system default for simplicity
    val dark = false

    Scaffold(
        topBar = {
            PremiumTopBar(
                title    = "About the Project",
                subtitle = "Anthar-Jala Watch v2.0",
                darkMode = dark,
                onBack   = onBack
            )
        },
        containerColor = SurfaceLight
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ── Hero section ────────────────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(0.dp),
                border    = BorderStroke(1.dp, BorderGlow)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(listOf(PrimaryDark, PrimaryMain, PrimaryLight))
                        )
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier        = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.WaterDrop, null, tint = White, modifier = Modifier.size(40.dp))
                        }
                        Spacer(Modifier.height(14.dp))
                        Text(
                            "Anthar-Jala Watch",
                            style     = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold, color = White
                            ),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "AI-Powered Groundwater Intelligence Platform",
                            style     = MaterialTheme.typography.bodySmall.copy(
                                color = White.copy(alpha = 0.8f)
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                "Version 2.0.0  •  May 2025",
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                                style    = MaterialTheme.typography.labelSmall.copy(color = White)
                            )
                        }
                    }
                }
            }

            // ── Project Overview ─────────────────────────────────────────────
            AboutSection(title = "Project Overview") {
                Text(
                    "Anthar-Jala Watch (ಅಂತರ್ ಜಲ) is a community-driven Android application " +
                            "built to monitor and visualize groundwater conditions across Karnataka. " +
                            "Citizens log borewell data, and the AI engine analyzes patterns " +
                            "to generate actionable insights for conservation and policy.",
                    style     = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                    lineHeight = 22.sp
                )
            }

            // ── Key Features ─────────────────────────────────────────────────
            AboutSection(title = "Key Features") {
                val features = listOf(
                    Triple(Icons.Filled.Map,          PrimaryMain,    "Interactive Groundwater Map with color-coded status markers"),
                    Triple(Icons.Filled.Psychology,    Aqua400,        "AI-Powered risk analysis and predictive insight engine"),
                    Triple(Icons.Filled.BarChart,      StatusWarning,  "Enterprise analytics dashboard with real-time charts"),
                    Triple(Icons.Filled.CloudUpload,   Emerald500,     "Firebase Firestore real-time data sync"),
                    Triple(Icons.Filled.LocationOn,    StatusModerate, "District-wise filtering across 6 Karnataka districts"),
                    Triple(Icons.Filled.DarkMode,      PrimaryLight,   "Premium dark mode with futuristic navy theme"),
                    Triple(Icons.Filled.Notifications, StatusCritical, "Critical zone alerts and early warning system")
                )
                features.forEach { (icon, color, desc) ->
                    Row(
                        modifier              = Modifier.padding(vertical = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment     = Alignment.Top
                    ) {
                        Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
                        Text(
                            desc,
                            style     = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // ── Tech Stack ────────────────────────────────────────────────────
            AboutSection(title = "Technology Stack") {
                val stack = listOf(
                    "Kotlin"             to "Primary language — modern, concise Android development",
                    "Jetpack Compose"    to "Declarative UI framework — zero XML layouts",
                    "Material 3"         to "Google's latest design system with dynamic theming",
                    "Navigation Compose" to "Type-safe screen navigation with animated transitions",
                    "Firebase Firestore" to "Real-time NoSQL cloud database for borewell data",
                    "Google Maps SDK"    to "Interactive mapping with custom colored markers",
                    "Coroutines"         to "Async operations for Firebase & state management",
                    "MVVM Architecture"  to "Clean separation of UI, logic, and data layers"
                )
                stack.forEach { (tech, desc) ->
                    Row(
                        modifier          = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            tech,
                            style    = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color      = PrimaryMain
                            ),
                            modifier = Modifier.width(120.dp)
                        )
                        Text(
                            desc,
                            style     = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                            lineHeight = 17.sp,
                            modifier  = Modifier.weight(1f)
                        )
                    }
                }
            }

            // ── Architecture ──────────────────────────────────────────────────
            AboutSection(title = "Architecture") {
                ArchitectureDiagram()
            }

            // ── Future Roadmap ────────────────────────────────────────────────
            AboutSection(title = "Future Roadmap") {
                val roadmap = listOf(
                    "🛰️  IoT sensor integration for real-time borewell telemetry",
                    "📊  ML model trained on CGWB historical groundwater datasets",
                    "🔐  Firebase Authentication with role-based access control",
                    "📱  SMS/push notification system for critical zone alerts",
                    "🗣️  Multi-language support: Kannada, Hindi, Telugu",
                    "📤  CSV/PDF report export for government submissions",
                    "🗺️  State-level expansion beyond Karnataka"
                )
                roadmap.forEach { item ->
                    Text(
                        item,
                        style     = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        lineHeight = 20.sp,
                        modifier  = Modifier.padding(vertical = 3.dp)
                    )
                }
            }

            // ── Developer info ────────────────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = CardWhite),
                border    = BorderStroke(1.dp, BorderGlow),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Built by",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextHint)
                    )
                    Text(
                        "Student Intern — Android Development",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold, color = TextPrimary
                        )
                    )
                    Text(
                        "Karnataka Natural Resource Monitoring System",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(shape = RoundedCornerShape(20.dp), color = PrimaryMain.copy(alpha = 0.1f)) {
                            Text(
                                "Android",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style    = MaterialTheme.typography.labelSmall.copy(color = PrimaryMain, fontWeight = FontWeight.Bold)
                            )
                        }
                        Surface(shape = RoundedCornerShape(20.dp), color = Emerald500.copy(alpha = 0.1f)) {
                            Text(
                                "Firebase",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style    = MaterialTheme.typography.labelSmall.copy(color = Emerald500, fontWeight = FontWeight.Bold)
                            )
                        }
                        Surface(shape = RoundedCornerShape(20.dp), color = Aqua400.copy(alpha = 0.1f)) {
                            Text(
                                "AI/ML",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style    = MaterialTheme.typography.labelSmall.copy(color = Aqua400, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

// ── Sub-composables ───────────────────────────────────────────────────────────

@Composable
private fun AboutSection(
    title   : String,
    content : @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color      = PrimaryMain
            )
        )
        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), content = content)
        }
    }
}

@Composable
private fun ArchitectureDiagram() {
    val layers = listOf(
        Triple(PrimaryMain,    "UI Layer",         "Compose Screens + PremiumComponents"),
        Triple(Aqua400,        "ViewModel Layer",  "AppViewModel — State & Business Logic"),
        Triple(Emerald500,     "Repository Layer", "FirestoreRepository — Data Access"),
        Triple(StatusWarning,  "Firebase Layer",   "Firestore — Cloud NoSQL Database"),
        Triple(StatusModerate, "Maps Layer",       "Google Maps SDK — Geographic Visualization")
    )

    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        layers.forEachIndexed { idx, (color, label, desc) ->
            Row(verticalAlignment = Alignment.Top) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    if (idx < layers.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(28.dp)
                                .background(color.copy(alpha = 0.3f))
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.padding(bottom = if (idx < layers.lastIndex) 8.dp else 0.dp)) {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold, color = color
                        )
                    )
                    Text(
                        desc,
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                }
            }
        }
    }
}
