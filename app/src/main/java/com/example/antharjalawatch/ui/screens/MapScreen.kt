package com.example.antharjalawatch.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.antharjalawatch.data.model.*
import com.example.antharjalawatch.ui.components.*
import com.example.antharjalawatch.ui.theme.*
import com.example.antharjalawatch.viewmodel.AppViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel  : AppViewModel,
    onBack     : () -> Unit,
    onInsights : () -> Unit
) {
    val dark      = viewModel.darkMode
    val allEntries = DemoData.borewells

    var selectedEntry   by remember { mutableStateOf<BoreholeEntry?>(null) }
    var activeFilter    by remember { mutableStateOf("All") }
    var showDistrictMenu by remember { mutableStateOf(false) }

    val filteredEntries = remember(activeFilter) {
        if (activeFilter == "All") allEntries
        else allEntries.filter { it.district == activeFilter }
    }

    val centerLat = filteredEntries.map { it.latitude }.average().takeIf { !it.isNaN() } ?: 12.9716
    val centerLon = filteredEntries.map { it.longitude }.average().takeIf { !it.isNaN() } ?: 77.5946

    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(centerLat, centerLon), 9f)
    }

    LaunchedEffect(activeFilter) {
        cameraState.position = CameraPosition.fromLatLngZoom(
            LatLng(centerLat, centerLon), if (activeFilter == "All") 9f else 11f
        )
    }

    Scaffold(
        topBar = {
            PremiumTopBar(
                title    = "Groundwater Map",
                subtitle = "${filteredEntries.size} borewells • ${if (activeFilter == "All") "All Districts" else activeFilter}",
                darkMode = dark,
                onBack   = onBack,
                actions  = {
                    IconButton(onClick = onInsights) {
                        Icon(Icons.Filled.Insights, null, tint = White)
                    }
                }
            )
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            // ── Google Map ────────────────────────────────────────────────
            GoogleMap(
                modifier            = Modifier.fillMaxSize(),
                cameraPositionState = cameraState,
                uiSettings          = MapUiSettings(
                    zoomControlsEnabled = false,
                    mapToolbarEnabled   = false
                ),
                properties = MapProperties(mapType = MapType.NORMAL)
            ) {
                filteredEntries.forEach { entry ->
                    val hue = when (entry.waterStatus) {
                        "safe"     -> BitmapDescriptorFactory.HUE_GREEN
                        "moderate" -> BitmapDescriptorFactory.HUE_YELLOW
                        "warning"  -> BitmapDescriptorFactory.HUE_ORANGE
                        else       -> BitmapDescriptorFactory.HUE_RED
                    }
                    Marker(
                        state   = MarkerState(LatLng(entry.latitude, entry.longitude)),
                        title   = entry.district,
                        snippet = "Depth: ${entry.depth}ft  |  Yield: ${entry.yield}gph  |  ${riskLabel(entry.waterStatus)}",
                        icon    = BitmapDescriptorFactory.defaultMarker(hue),
                        onClick = { selectedEntry = entry; false }
                    )
                }
            }

            // ── District Filter Chips (top overlay) ───────────────────────
            Card(
                modifier  = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .fillMaxWidth(0.85f),
                shape     = RoundedCornerShape(14.dp),
                colors    = CardDefaults.cardColors(
                    containerColor = if (dark) Navy800.copy(alpha = 0.95f) else White.copy(alpha = 0.96f)
                ),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipItem("All", activeFilter, dark) { activeFilter = "All" }
                    DemoData.districts.forEach { d ->
                        FilterChipItem(d.name, activeFilter, dark) { activeFilter = d.name }
                    }
                }
            }

            // ── Legend ─────────────────────────────────────────────────────
            Card(
                modifier  = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(
                    containerColor = if (dark) Navy800.copy(alpha = 0.95f) else White.copy(alpha = 0.96f)
                ),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        "STATUS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color      = if (dark) TextOnDarkSub else TextHint
                        )
                    )
                    LegendRow(StatusSafe,     "Safe",     dark)
                    LegendRow(StatusModerate, "Moderate", dark)
                    LegendRow(StatusWarning,  "Warning",  dark)
                    LegendRow(StatusCritical, "Critical", dark)
                }
            }

            // ── Zoom Controls (custom) ─────────────────────────────────────
            Column(
                modifier            = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MapControlButton(icon = Icons.Filled.Add) {
                    val pos = cameraState.position
                    cameraState.position = CameraPosition.fromLatLngZoom(pos.target, (pos.zoom + 1).coerceAtMost(20f))
                }
                MapControlButton(icon = Icons.Filled.Remove) {
                    val pos = cameraState.position
                    cameraState.position = CameraPosition.fromLatLngZoom(pos.target, (pos.zoom - 1).coerceAtLeast(4f))
                }
            }

            // ── Stats Bar (bottom of map) ──────────────────────────────────
            Card(
                modifier  = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = if (selectedEntry != null) 228.dp else 12.dp),
                shape     = RoundedCornerShape(14.dp),
                colors    = CardDefaults.cardColors(
                    containerColor = if (dark) Navy800.copy(alpha = 0.95f) else White.copy(alpha = 0.96f)
                ),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val safe     = filteredEntries.count { it.waterStatus == "safe" }
                    val moderate = filteredEntries.count { it.waterStatus == "moderate" }
                    val warning  = filteredEntries.count { it.waterStatus == "warning" }
                    val critical = filteredEntries.count { it.waterStatus == "critical" }
                    MapStatItem("🟢 Safe",     "$safe",     StatusSafe,     dark)
                    MapStatItem("🟡 Moderate", "$moderate", StatusModerate, dark)
                    MapStatItem("🟠 Warning",  "$warning",  StatusWarning,  dark)
                    MapStatItem("🔴 Critical", "$critical", StatusCritical, dark)
                }
            }

            // ── Marker Detail Card (bottom sheet style) ────────────────────
            AnimatedVisibility(
                visible = selectedEntry != null,
                enter   = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit    = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                selectedEntry?.let { entry ->
                    MarkerDetailCard(
                        entry    = entry,
                        darkMode = dark,
                        onDismiss = { selectedEntry = null }
                    )
                }
            }

            // ── Insights FAB ───────────────────────────────────────────────
            ExtendedFloatingActionButton(
                onClick          = onInsights,
                modifier         = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end    = 12.dp,
                        bottom = if (selectedEntry != null) 240.dp else 70.dp
                    ),
                containerColor   = PrimaryMain,
                contentColor     = White,
                icon             = { Icon(Icons.Filled.Insights, null) },
                text             = { Text("AI Insights") }
            )
        }
    }
}

// ── Private composables ───────────────────────────────────────────────────────

@Composable
private fun FilterChipItem(
    label    : String,
    active   : String,
    darkMode : Boolean,
    onClick  : () -> Unit
) {
    val isSelected = label == active
    FilterChip(
        selected = isSelected,
        onClick  = onClick,
        label    = {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = PrimaryMain,
            selectedLabelColor     = White,
            containerColor         = if (darkMode) Navy700 else Color(0xFFF0F4FF),
            labelColor             = if (darkMode) TextOnDarkSub else TextSecondary
        )
    )
}

@Composable
private fun LegendRow(color: Color, label: String, darkMode: Boolean) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (darkMode) TextOnDark else TextSecondary
            )
        )
    }
}

@Composable
private fun MapControlButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier  = Modifier.size(36.dp),
        shape     = CircleShape,
        colors    = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            IconButton(onClick = onClick, modifier = Modifier.fillMaxSize()) {
                Icon(icon, null, tint = PrimaryDark, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun MapStatItem(label: String, value: String, color: Color, darkMode: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = color)
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (darkMode) TextOnDarkSub else TextSecondary
            )
        )
    }
}

@Composable
private fun MarkerDetailCard(
    entry    : BoreholeEntry,
    darkMode : Boolean,
    onDismiss: () -> Unit
) {
    val color   = riskColor(entry.waterStatus)
    val bgColor = if (darkMode) Navy800 else White

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = bgColor),
        border    = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Handle bar
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (darkMode) Navy500 else Color(0xFFCDD5DF))
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(14.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.LocationOn, null, tint = color, modifier = Modifier.size(18.dp))
                    Text(
                        entry.district,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color      = if (darkMode) TextOnDark else TextPrimary
                        )
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    StatusBadge(entry.waterStatus)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.Close, null, tint = if (darkMode) TextOnDarkSub else TextHint, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Data pills
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DetailPill("Depth",   "${entry.depth} ft",     PrimaryMain,    darkMode, Modifier.weight(1f))
                DetailPill("Yield",   "${entry.yield} gph",    Emerald500,     darkMode, Modifier.weight(1f))
                DetailPill("Year",    entry.yearOfDig,         StatusModerate, darkMode, Modifier.weight(1f))
            }

            Spacer(Modifier.height(12.dp))

            // AI insight
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier              = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.Top
                ) {
                    Icon(Icons.Filled.Psychology, null, tint = color, modifier = Modifier.size(14.dp))
                    Text(
                        entry.generateAIInsight(),
                        style     = MaterialTheme.typography.labelSmall.copy(color = color),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailPill(label: String, value: String, color: Color, darkMode: Boolean, modifier: Modifier) {
    Column(
        modifier            = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            value,
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
