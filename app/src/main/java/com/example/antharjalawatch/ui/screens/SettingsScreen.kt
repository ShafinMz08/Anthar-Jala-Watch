package com.example.antharjalawatch.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.antharjalawatch.ui.components.*
import com.example.antharjalawatch.ui.theme.*
import com.example.antharjalawatch.viewmodel.AppViewModel

/**
 * SettingsScreen — app preferences, dark mode toggle, and navigation to About.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel : AppViewModel,
    onBack    : () -> Unit,
    onAbout   : () -> Unit
) {
    val dark = viewModel.darkMode

    var notificationsEnabled by remember { mutableStateOf(true) }
    var autoRefreshEnabled   by remember { mutableStateOf(true) }
    var analyticsEnabled     by remember { mutableStateOf(false) }
    var highAccuracyMaps     by remember { mutableStateOf(true) }
    var selectedUnit         by remember { mutableStateOf("Metric (ft / gph)") }
    var unitMenuOpen         by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            PremiumTopBar(
                title    = "Settings",
                subtitle = "Preferences & Configuration",
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

            // ── Appearance ──────────────────────────────────────────────────
            SettingsSection(title = "Appearance", darkMode = dark) {
                SettingsToggleRow(
                    icon     = Icons.Filled.DarkMode,
                    label    = "Dark Mode",
                    sublabel = "Futuristic dark navy theme",
                    checked  = dark,
                    color    = PrimaryMain,
                    darkMode = dark,
                    onToggle = { viewModel.toggleDarkMode() }
                )
            }

            // ── Notifications ───────────────────────────────────────────────
            SettingsSection(title = "Notifications", darkMode = dark) {
                SettingsToggleRow(
                    icon     = Icons.Filled.NotificationsActive,
                    label    = "Critical Alerts",
                    sublabel = "Notify when districts reach critical status",
                    checked  = notificationsEnabled,
                    color    = StatusCritical,
                    darkMode = dark,
                    onToggle = { notificationsEnabled = it }
                )
                SettingsDivider(dark)
                SettingsToggleRow(
                    icon     = Icons.Filled.Refresh,
                    label    = "Auto Refresh Data",
                    sublabel = "Refresh groundwater data every 30 minutes",
                    checked  = autoRefreshEnabled,
                    color    = Emerald500,
                    darkMode = dark,
                    onToggle = { autoRefreshEnabled = it }
                )
            }

            // ── Data & Maps ─────────────────────────────────────────────────
            SettingsSection(title = "Data & Maps", darkMode = dark) {
                val fieldBg = if (dark) Navy700 else White

                ExposedDropdownMenuBox(
                    expanded         = unitMenuOpen,
                    onExpandedChange = { unitMenuOpen = !unitMenuOpen },
                    modifier         = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value         = selectedUnit,
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Measurement Units") },
                        leadingIcon   = {
                            Icon(
                                Icons.Filled.Straighten,
                                contentDescription = null,
                                tint     = PrimaryMain,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(unitMenuOpen) },
                        modifier      = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape         = RoundedCornerShape(12.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor      = PrimaryMain,
                            unfocusedBorderColor    = if (dark) Navy500 else Color(0xFFCDD5DF),
                            focusedContainerColor   = fieldBg,
                            unfocusedContainerColor = fieldBg,
                            focusedTextColor        = if (dark) TextOnDark else TextPrimary,
                            unfocusedTextColor      = if (dark) TextOnDark else TextPrimary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded         = unitMenuOpen,
                        onDismissRequest = { unitMenuOpen = false }
                    ) {
                        listOf(
                            "Metric (ft / gph)",
                            "SI (m / L/hr)",
                            "Imperial (ft / gal/min)"
                        ).forEach { unit ->
                            DropdownMenuItem(
                                text    = { Text(unit) },
                                onClick = {
                                    selectedUnit = unit
                                    unitMenuOpen = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                SettingsToggleRow(
                    icon     = Icons.Filled.Map,
                    label    = "High Accuracy Maps",
                    sublabel = "Uses more data — recommended for field use",
                    checked  = highAccuracyMaps,
                    color    = Emerald500,
                    darkMode = dark,
                    onToggle = { highAccuracyMaps = it }
                )
                SettingsDivider(dark)
                SettingsToggleRow(
                    icon     = Icons.Filled.Analytics,
                    label    = "Anonymous Analytics",
                    sublabel = "Help improve AI model accuracy",
                    checked  = analyticsEnabled,
                    color    = Aqua400,
                    darkMode = dark,
                    onToggle = { analyticsEnabled = it }
                )
            }

            // ── About ────────────────────────────────────────────────────────
            SettingsSection(title = "About", darkMode = dark) {
                SettingsNavRow(
                    icon     = Icons.Filled.Info,
                    label    = "About the Project",
                    sublabel = "Version, team, and technology stack",
                    color    = PrimaryMain,
                    darkMode = dark,
                    onClick  = onAbout
                )
                SettingsDivider(dark)
                SettingsNavRow(
                    icon     = Icons.Filled.Policy,
                    label    = "Privacy Policy",
                    sublabel = "Data usage and storage policy",
                    color    = Teal400,
                    darkMode = dark,
                    onClick  = {}
                )
                SettingsDivider(dark)
                SettingsNavRow(
                    icon     = Icons.Filled.BugReport,
                    label    = "Report a Bug",
                    sublabel = "Submit feedback to improve the app",
                    color    = StatusWarning,
                    darkMode = dark,
                    onClick  = {}
                )
            }

            // ── Version Footer ───────────────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(14.dp),
                colors    = CardDefaults.cardColors(
                    containerColor = if (dark) Navy800 else Color(0xFFF0F4FF)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Anthar-Jala Watch",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color      = PrimaryMain
                        )
                    )
                    Text(
                        "Version 2.0.0  —  AI Groundwater Intelligence Platform",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (dark) TextOnDarkSub else TextHint
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "© 2025 Karnataka Natural Resource Monitoring System",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (dark) TextOnDarkSub else TextHint
                        )
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

// ── Private composables ───────────────────────────────────────────────────────

@Composable
private fun SettingsSection(
    title    : String,
    darkMode : Boolean,
    content  : @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(
            text     = title.uppercase(),
            modifier = Modifier.padding(bottom = 8.dp),
            style    = MaterialTheme.typography.labelSmall.copy(
                fontWeight    = FontWeight.Bold,
                color         = PrimaryMain,
                letterSpacing = 1.5.sp
            )
        )
        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(
                containerColor = if (darkMode) CardDark else CardWhite
            ),
            elevation = CardDefaults.cardElevation(if (darkMode) 0.dp else 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(4.dp),
                content  = content
            )
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon     : ImageVector,
    label    : String,
    sublabel : String,
    checked  : Boolean,
    color    : Color,
    darkMode : Boolean,
    onToggle : (Boolean) -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier         = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = label,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Medium,
                    color      = if (darkMode) TextOnDark else TextPrimary
                )
            )
            Text(
                text  = sublabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (darkMode) TextOnDarkSub else TextHint
                )
            )
        }
        Switch(
            checked         = checked,
            onCheckedChange = onToggle,
            colors          = SwitchDefaults.colors(
                checkedThumbColor   = Color.White,
                checkedTrackColor   = color,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = if (darkMode) Navy600 else Color(0xFFCDD5DF)
            )
        )
    }
}

@Composable
private fun SettingsNavRow(
    icon     : ImageVector,
    label    : String,
    sublabel : String,
    color    : Color,
    darkMode : Boolean,
    onClick  : () -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier         = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = label,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Medium,
                    color      = if (darkMode) TextOnDark else TextPrimary
                )
            )
            Text(
                text  = sublabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (darkMode) TextOnDarkSub else TextHint
                )
            )
        }
        Icon(
            imageVector        = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint               = if (darkMode) TextOnDarkSub else TextHint,
            modifier           = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun SettingsDivider(darkMode: Boolean) {
    HorizontalDivider(
        modifier  = Modifier.padding(start = 62.dp),
        color     = if (darkMode) Navy600 else Color(0xFFEEF2FF),
        thickness = 1.dp
    )
}
