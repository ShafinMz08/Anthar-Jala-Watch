package com.example.antharjalawatch.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import com.example.antharjalawatch.data.model.BoreholeEntry
import com.example.antharjalawatch.data.model.DemoData
import com.example.antharjalawatch.ui.components.*
import com.example.antharjalawatch.ui.theme.*
import com.example.antharjalawatch.viewmodel.AppViewModel
import com.example.antharjalawatch.viewmodel.SubmitState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogDataScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val dark = viewModel.darkMode

    var depth by remember { mutableStateOf("") }
    var yield by remember { mutableStateOf("") }
    var yearOfDig by remember { mutableStateOf("") }

    var selectedDistrict by remember {
        mutableStateOf(
            viewModel.selectedDistrict?.name
                ?: DemoData.districts.first().name
        )
    }

    var districtMenuOpen by remember { mutableStateOf(false) }

    val submitState = viewModel.submitState

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetSubmitState()
        }
    }

    Scaffold(
        topBar = {
            PremiumTopBar(
                title = "Log Borewell Data",
                subtitle = "Contribute to groundwater intelligence",
                darkMode = dark,
                onBack = onBack
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Info Card
            GlassCard(darkMode = dark) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = null,
                        tint = PrimaryMain,
                        modifier = Modifier.size(18.dp)
                    )

                    Text(
                        text = "Your borewell data feeds our AI groundwater model. Each submission improves prediction accuracy for your district.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (dark) TextOnDarkSub else TextSecondary
                        ),
                        lineHeight = 18.sp
                    )
                }
            }

            // Borewell Details
            GlassCard(
                darkMode = dark,
                borderGlow = true
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.WaterDrop,
                            contentDescription = null,
                            tint = PrimaryMain,
                            modifier = Modifier.size(18.dp)
                        )

                        Text(
                            text = "Borewell Details",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryMain
                            )
                        )
                    }

                    // District Dropdown
                    ExposedDropdownMenuBox(
                        expanded = districtMenuOpen,
                        onExpandedChange = {
                            districtMenuOpen = !districtMenuOpen
                        }
                    ) {

                        OutlinedTextField(
                            value = selectedDistrict,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("District") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = districtMenuOpen
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = premiumTextFieldColors(dark)
                        )

                        ExposedDropdownMenu(
                            expanded = districtMenuOpen,
                            onDismissRequest = {
                                districtMenuOpen = false
                            }
                        ) {
                            DemoData.districts.forEach { district ->

                                DropdownMenuItem(
                                    text = {
                                        Text(district.name)
                                    },
                                    onClick = {
                                        selectedDistrict = district.name
                                        districtMenuOpen = false
                                    }
                                )
                            }
                        }
                    }

                    PremiumTextField(
                        value = depth,
                        onChange = { depth = it },
                        label = "Borewell Depth",
                        placeholder = "e.g. 350",
                        suffix = "feet",
                        icon = Icons.Filled.UnfoldMore,
                        dark = dark
                    )

                    PremiumTextField(
                        value = yield,
                        onChange = { yield = it },
                        label = "Water Yield",
                        placeholder = "e.g. 120",
                        suffix = "gal/hr",
                        icon = Icons.Filled.Waves,
                        dark = dark
                    )

                    PremiumTextField(
                        value = yearOfDig,
                        onChange = { yearOfDig = it },
                        label = "Year of Digging",
                        placeholder = "e.g. 2019",
                        suffix = "",
                        icon = Icons.Filled.CalendarToday,
                        dark = dark
                    )
                }
            }

            // Location Card
            GlassCard(darkMode = dark) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                PrimaryMain.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = PrimaryMain,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {

                        Text(
                            text = "Auto-captured Location",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryMain
                            )
                        )

                        Text(
                            text = "Lat: 12.9716  •  Lon: 77.5946",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (dark) TextOnDarkSub else TextSecondary
                            )
                        )

                        Text(
                            text = "📍 $selectedDistrict, Karnataka",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (dark) TextOnDarkSub else TextHint
                            )
                        )
                    }
                }
            }

            // AI Preview
            val previewStatus =
                BoreholeEntry(
                    depth = depth,
                    yield = yield
                ).computeStatus()

            if (depth.isNotBlank() && yield.isNotBlank()) {

                val previewColor = riskColor(previewStatus)

                val previewBg =
                    if (dark) CardDark
                    else riskBg(previewStatus)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = previewBg
                    ),
                    border = BorderStroke(
                        1.dp,
                        previewColor.copy(alpha = 0.4f)
                    )
                ) {

                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            Icons.Filled.Psychology,
                            contentDescription = null,
                            tint = previewColor,
                            modifier = Modifier.size(18.dp)
                        )

                        Column {

                            Text(
                                text = "AI Status Preview: ${riskLabel(previewStatus)}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = previewColor
                                )
                            )

                            val insightText = when (previewStatus) {
                                "SAFE" ->
                                    "Groundwater levels appear stable in this area."

                                "MODERATE" ->
                                    "Moderate groundwater availability detected."

                                "CRITICAL" ->
                                    "Low groundwater availability predicted. Use caution."

                                else ->
                                    "AI analysis unavailable."
                            }

                            Text(
                                text = insightText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (dark) TextOnDarkSub else TextSecondary
                                ),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Success Banner
            AnimatedVisibility(
                visible = submitState is SubmitState.Success,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = StatusSafeLight
                    ),
                    border = BorderStroke(
                        1.dp,
                        StatusSafe.copy(alpha = 0.5f)
                    )
                ) {

                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = StatusSafe,
                            modifier = Modifier.size(20.dp)
                        )

                        Text(
                            text = "✅ Borewell data submitted successfully.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = StatusSafe,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Error Banner
            AnimatedVisibility(
                visible = submitState is SubmitState.Error,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {

                val message =
                    (submitState as? SubmitState.Error)?.message ?: ""

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = StatusCriticalLight
                    ),
                    border = BorderStroke(
                        1.dp,
                        StatusCritical.copy(alpha = 0.4f)
                    )
                ) {

                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {

                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = null,
                            tint = StatusCritical,
                            modifier = Modifier.size(18.dp)
                        )

                        Text(
                            text = "⚠️ $message",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = StatusCritical
                            )
                        )
                    }
                }
            }

            val isLoading =
                submitState is SubmitState.Loading

            Button(
                onClick = {

                    viewModel.submitEntry(
                        BoreholeEntry(
                            depth = depth.trim(),
                            yield = yield.trim(),
                            yearOfDig = yearOfDig.trim(),
                            latitude = 12.9716,
                            longitude = 77.5946,
                            district = selectedDistrict
                        )
                    )

                    depth = ""
                    yield = ""
                    yearOfDig = ""
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),

                enabled = !isLoading,

                shape = RoundedCornerShape(14.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryMain
                )
            ) {

                if (isLoading) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = White,
                        strokeWidth = 2.dp
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Submitting...",
                        color = White
                    )

                } else {

                    Icon(
                        Icons.Filled.CloudUpload,
                        contentDescription = null,
                        tint = White
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Submit to Groundwater Model",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PremiumTextField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    placeholder: String,
    suffix: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    dark: Boolean
) {

    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },

        placeholder = {
            Text(
                placeholder,
                color = TextHint
            )
        },

        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                tint = PrimaryMain,
                modifier = Modifier.size(18.dp)
            )
        },

        suffix =
            if (suffix.isNotBlank()) {
                {
                    Text(
                        suffix,
                        color = TextHint
                    )
                }
            } else null,

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(12.dp),

        colors = premiumTextFieldColors(dark),

        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun premiumTextFieldColors(
    dark: Boolean
) = OutlinedTextFieldDefaults.colors(

    focusedBorderColor = PrimaryMain,

    unfocusedBorderColor =
        if (dark) Navy500
        else Color(0xFFCDD5DF),

    focusedLabelColor = PrimaryMain,

    cursorColor = PrimaryMain,

    focusedContainerColor =
        if (dark) Navy700
        else White,

    unfocusedContainerColor =
        if (dark) Navy800
        else White,

    focusedTextColor =
        if (dark) TextOnDark
        else TextPrimary,

    unfocusedTextColor =
        if (dark) TextOnDark
        else TextPrimary
)