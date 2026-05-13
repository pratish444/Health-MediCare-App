package com.example.healthmedicareapp.presentation.medical_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MedicalDetailsScreen(
    onNavigateToDashboard: () -> Unit,
    viewModel: MedicalDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var profession by remember { mutableStateOf("") }
    var systolicBP by remember { mutableStateOf("") }
    var diastolicBP by remember { mutableStateOf("") }

    val genderOptions = listOf("Male", "Female", "Other")
    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    LaunchedEffect(uiState) {
        when (uiState) {
            is MedicalDetailsUiState.Success -> onNavigateToDashboard()
            is MedicalDetailsUiState.Error -> {
                snackbarHostState.showSnackbar((uiState as MedicalDetailsUiState.Error).message)
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = { MediCareTopBar(title = "Medical Details") },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF0F4FF))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Personal Health Info", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A2E))
                    Text("We use this to personalize your experience", fontSize = 13.sp, color = Color.Gray)
                    Spacer(Modifier.height(20.dp))

                    val blue = Color(0xFF1565C0)
                    val colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = blue, focusedLabelColor = blue)

                    // Gender selector
                    Text("Gender", fontWeight = FontWeight.Medium, color = Color(0xFF333333))
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        genderOptions.forEach { option ->
                            FilterChip(
                                selected = gender == option,
                                onClick = { gender = option },
                                label = { Text(option) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = blue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height (cm)") },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = colors, singleLine = true)
                        OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = colors, singleLine = true)
                    }
                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = colors, singleLine = true)
                        OutlinedTextField(value = bloodGroup, onValueChange = { bloodGroup = it }, label = { Text("Blood Group") },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = colors, singleLine = true,
                            placeholder = { Text("e.g. A+") })
                    }
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(value = profession, onValueChange = { profession = it }, label = { Text("Profession") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Work, null, tint = blue) }, colors = colors, singleLine = true)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location / City") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = blue) }, colors = colors, singleLine = true)
                    Spacer(Modifier.height(12.dp))

                    Text("Blood Pressure", fontWeight = FontWeight.Medium, color = Color(0xFF333333))
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = systolicBP, onValueChange = { systolicBP = it }, label = { Text("Systolic") },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = colors, singleLine = true,
                            placeholder = { Text("e.g. 120") })
                        OutlinedTextField(value = diastolicBP, onValueChange = { diastolicBP = it }, label = { Text("Diastolic") },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = colors, singleLine = true,
                            placeholder = { Text("e.g. 80") })
                    }

                    Spacer(Modifier.height(28.dp))

                    Button(
                        onClick = {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                            viewModel.saveMedicalDetails(
                                userId = userId,
                                height = height.toIntOrNull() ?: 0,
                                weight = weight.toIntOrNull() ?: 0,
                                age = age.toIntOrNull() ?: 0,
                                bloodGroup = bloodGroup, location = location, gender = gender,
                                profession = profession,
                                systolicBP = systolicBP.toIntOrNull() ?: 0,
                                diastolicBP = diastolicBP.toIntOrNull() ?: 0
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = blue),
                        enabled = uiState !is MedicalDetailsUiState.Loading && height.isNotBlank() && weight.isNotBlank() && age.isNotBlank()
                    ) {
                        if (uiState is MedicalDetailsUiState.Loading)
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        else Text("Save & Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
