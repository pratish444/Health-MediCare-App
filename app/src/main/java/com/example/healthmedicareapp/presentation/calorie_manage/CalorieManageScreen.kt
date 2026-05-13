package com.example.healthmedicareapp.presentation.calorie_manage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthmedicareapp.domain.usecase.CalculateCaloriesUseCase
import com.example.healthmedicareapp.presentation.components.MediCareTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieManageScreen(
    onNavigateBack: () -> Unit,
    viewModel: CalorieManageViewModel = hiltViewModel()
) {
    val calorieData by viewModel.calorieData.collectAsState()

    var gender by remember { mutableStateOf("Male") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("Lightly Active (1-3 day/week)") }
    var activityExpanded by remember { mutableStateOf(false) }

    val genderOptions = listOf("Male", "Female")
    val activityOptions = listOf(
        "Sedentary (little/no exercise)",
        "Lightly Active (1-3 day/week)",
        "Moderately Active (3-5 day/week)",
        "Very Active (6-7 day/week)",
        "Extremely Active (athlete)"
    )

    Scaffold(
        topBar = { MediCareTopBar(title = "Calorie Manager", onBack = onNavigateBack) },
        containerColor = Color(0xFFF0F4FF)
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            // Input card
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Your Details", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A2E))
                    Spacer(Modifier.height(16.dp))

                    val green = Color(0xFF2E7D32)
                    val colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = green, focusedLabelColor = green)

                    // Gender chips
                    Text("Gender", fontSize = 13.sp, color = Color.Gray)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        genderOptions.forEach { option ->
                            FilterChip(selected = gender == option, onClick = { gender = option }, label = { Text(option) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = green, selectedLabelColor = Color.White))
                        }
                    }
                    Spacer(Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = colors, singleLine = true)
                        OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height (cm)") },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = colors, singleLine = true)
                    }
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = colors, singleLine = true)
                    Spacer(Modifier.height(12.dp))

                    // Activity level dropdown
                    ExposedDropdownMenuBox(expanded = activityExpanded, onExpandedChange = { activityExpanded = it }) {
                        OutlinedTextField(
                            value = activityLevel, onValueChange = {}, readOnly = true, label = { Text("Activity Level") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = activityExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = colors
                        )
                        ExposedDropdownMenu(expanded = activityExpanded, onDismissRequest = { activityExpanded = false }) {
                            activityOptions.forEach { option ->
                                DropdownMenuItem(text = { Text(option, fontSize = 13.sp) }, onClick = { activityLevel = option; activityExpanded = false })
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.calculateCalories(gender, weight.toIntOrNull() ?: 0, height.toIntOrNull() ?: 0, age.toIntOrNull() ?: 0, activityLevel)
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = green),
                        enabled = weight.isNotBlank() && height.isNotBlank() && age.isNotBlank()
                    ) {
                        Text("Calculate Calories", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Results card
            if (calorieData != null) {
                Spacer(Modifier.height(20.dp))
                val data = calorieData!!
                Card(
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D47A1)),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Your Results", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        Spacer(Modifier.height(16.dp))
                        CalorieResultRow("🔥 BMR", "${data.bmr} kcal", "Base Metabolic Rate")
                        Divider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 10.dp))
                        CalorieResultRow("⚖️ Maintenance", "${data.maintenanceCalories} kcal", "To maintain weight")
                        Divider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 10.dp))
                        CalorieResultRow("📉 Weight Loss", "${data.weightLossCalories} kcal", "-0.5 kg/week")
                        Divider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 10.dp))
                        CalorieResultRow("📈 Weight Gain", "${data.weightGainCalories} kcal", "+0.5 kg/week")
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CalorieResultRow(label: String, value: String, subtitle: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(label, fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 15.sp)
            Text(subtitle, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
        }
        Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF82B1FF))
    }
}
