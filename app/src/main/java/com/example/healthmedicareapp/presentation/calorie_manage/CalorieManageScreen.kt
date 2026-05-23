package com.example.healthmedicareapp.presentation.calorie_manage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthmedicareapp.presentation.components.MediCareTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieManageScreen(
    onNavigateBack: (() -> Unit)? = null,
    viewModel: CalorieManageViewModel = hiltViewModel()
) {
    val calorieData by viewModel.calorieData.collectAsState()

    var gender          by remember { mutableStateOf("Male") }
    var weight          by remember { mutableStateOf("") }
    var height          by remember { mutableStateOf("") }
    var age             by remember { mutableStateOf("") }
    var activityLevel   by remember { mutableStateOf("Lightly Active (1-3 day/week)") }
    var activityExpanded by remember { mutableStateOf(false) }

    val genderOptions = listOf("Male", "Female")
    val activityOptions = listOf(
        "Sedentary (little/no exercise)",
        "Lightly Active (1-3 day/week)",
        "Moderately Active (3-5 day/week)",
        "Very Active (6-7 day/week)",
        "Extremely Active (athlete)"
    )

    val green = Color(0xFF11998E)

    Scaffold(
        topBar         = { if (onNavigateBack != null) MediCareTopBar(title = "Calorie Manager", onBack = onNavigateBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // ── Title row (when shown as tab, no TopBar) ──────────────────────
            if (onNavigateBack == null) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier         = Modifier
                            .size(44.dp)
                            .background(
                                Brush.linearGradient(listOf(Color(0xFF11998E), Color(0xFF38EF7D))),
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.LocalDining, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Calorie Manager", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colorScheme.onBackground)
                        Text("Calculate your daily needs", fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f))
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // ── Input card ────────────────────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Your Details", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(16.dp))

                    val fieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = green,
                        focusedLabelColor  = green
                    )

                    // Gender chips
                    Text("Gender", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        genderOptions.forEach { option ->
                            FilterChip(
                                selected = gender == option,
                                onClick  = { gender = option },
                                label    = { Text(option) },
                                leadingIcon = if (gender == option) ({
                                    Icon(Icons.Rounded.Check, null, modifier = Modifier.size(16.dp))
                                }) else null,
                                colors   = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = green,
                                    selectedLabelColor     = Color.White,
                                    selectedLeadingIconColor = Color.White
                                )
                            )
                        }
                    }
                    Spacer(Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value         = weight,
                            onValueChange = { weight = it },
                            label         = { Text("Weight (kg)") },
                            leadingIcon   = { Icon(Icons.Rounded.FitnessCenter, null, tint = green, modifier = Modifier.size(20.dp)) },
                            modifier      = Modifier.weight(1f),
                            shape         = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors        = fieldColors,
                            singleLine    = true
                        )
                        OutlinedTextField(
                            value         = height,
                            onValueChange = { height = it },
                            label         = { Text("Height (cm)") },
                            leadingIcon   = { Icon(Icons.Rounded.Height, null, tint = green, modifier = Modifier.size(20.dp)) },
                            modifier      = Modifier.weight(1f),
                            shape         = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors        = fieldColors,
                            singleLine    = true
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value         = age,
                        onValueChange = { age = it },
                        label         = { Text("Age") },
                        leadingIcon   = { Icon(Icons.Rounded.Cake, null, tint = green, modifier = Modifier.size(20.dp)) },
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors        = fieldColors,
                        singleLine    = true
                    )
                    Spacer(Modifier.height(12.dp))

                    ExposedDropdownMenuBox(expanded = activityExpanded, onExpandedChange = { activityExpanded = it }) {
                        OutlinedTextField(
                            value        = activityLevel,
                            onValueChange = {},
                            readOnly     = true,
                            label        = { Text("Activity Level") },
                            leadingIcon  = { Icon(Icons.Rounded.DirectionsRun, null, tint = green, modifier = Modifier.size(20.dp)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = activityExpanded) },
                            modifier     = Modifier.menuAnchor().fillMaxWidth(),
                            shape        = RoundedCornerShape(12.dp),
                            colors       = fieldColors
                        )
                        ExposedDropdownMenu(expanded = activityExpanded, onDismissRequest = { activityExpanded = false }) {
                            activityOptions.forEach { option ->
                                DropdownMenuItem(
                                    text    = { Text(option, fontSize = 13.sp) },
                                    onClick = { activityLevel = option; activityExpanded = false },
                                    leadingIcon = if (option == activityLevel) ({
                                        Icon(Icons.Rounded.Check, null, tint = green, modifier = Modifier.size(18.dp))
                                    }) else null
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick  = {
                            viewModel.calculateCalories(
                                gender,
                                weight.toIntOrNull() ?: 0,
                                height.toIntOrNull() ?: 0,
                                age.toIntOrNull() ?: 0,
                                activityLevel
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        enabled  = weight.isNotBlank() && height.isNotBlank() && age.isNotBlank()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF11998E), Color(0xFF38EF7D)),
                                        start = Offset(0f, 0f),
                                        end   = Offset(Float.POSITIVE_INFINITY, 0f)
                                    ),
                                    RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Calculate, null, tint = Color.White)
                                Spacer(Modifier.width(8.dp))
                                Text("Calculate Calories", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // ── Results card ──────────────────────────────────────────────────
            if (calorieData != null) {
                Spacer(Modifier.height(20.dp))
                val data = calorieData!!
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(20.dp),
                    colors    = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF0D47A1), Color(0xFF1565C0), Color(0xFF1976D2)),
                                    start = Offset(0f, 0f),
                                    end   = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                                ),
                                RoundedCornerShape(20.dp)
                            )
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.BarChart, null, tint = Color.White, modifier = Modifier.size(22.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Your Results", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                            }
                            Spacer(Modifier.height(16.dp))
                            CalorieResultRow("🔥 BMR",          "${data.bmr} kcal",                "Base Metabolic Rate")
                            Divider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 10.dp))
                            CalorieResultRow("⚖️ Maintenance",   "${data.maintenanceCalories} kcal", "To maintain weight")
                            Divider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 10.dp))
                            CalorieResultRow("📉 Weight Loss",   "${data.weightLossCalories} kcal",  "-0.5 kg/week")
                            Divider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 10.dp))
                            CalorieResultRow("📈 Weight Gain",   "${data.weightGainCalories} kcal",  "+0.5 kg/week")
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CalorieResultRow(label: String, value: String, subtitle: String) {
    Row(
        modifier             = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment    = Alignment.CenterVertically
    ) {
        Column {
            Text(label,    fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 15.sp)
            Text(subtitle, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
        }
        Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF82B1FF))
    }
}
