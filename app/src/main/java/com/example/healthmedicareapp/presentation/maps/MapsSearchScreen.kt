package com.example.healthmedicareapp.presentation.maps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthmedicareapp.presentation.components.MediCareTopBar
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapsSearchScreen(
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val defaultLocation = LatLng(20.5937, 78.9629) // India center
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 5f)
    }

    val sampleHospitals = listOf(
        Triple("City Hospital", LatLng(28.6139, 77.2090), "New Delhi"),
        Triple("MediCare Center", LatLng(19.0760, 72.8777), "Mumbai"),
        Triple("Apollo Clinic", LatLng(13.0827, 80.2707), "Chennai"),
        Triple("Health Plus", LatLng(12.9716, 77.5946), "Bangalore")
    )

    Scaffold(
        topBar = { MediCareTopBar(title = "Find Hospital", onBack = onNavigateBack) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = false),
                uiSettings = MapUiSettings(zoomControlsEnabled = true)
            ) {
                sampleHospitals.filter {
                    searchQuery.isBlank() || it.first.contains(searchQuery, ignoreCase = true) || it.third.contains(searchQuery, ignoreCase = true)
                }.forEach { (name, location, city) ->
                    Marker(
                        state = MarkerState(position = location),
                        title = name,
                        snippet = city
                    )
                }
            }

            // Search overlay
            Card(
                modifier = Modifier.fillMaxWidth().padding(12.dp).align(Alignment.TopCenter),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search hospitals, clinics...") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF1565C0)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, null, tint = Color.Gray)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1565C0),
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            // Info card at bottom
            Card(
                modifier = Modifier.fillMaxWidth().padding(12.dp).align(Alignment.BottomCenter),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nearby Healthcare", fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Text(
                        "Add your Google Maps API key in local.properties to enable full Places search and location features.",
                        fontSize = 12.sp, color = Color.Gray
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(onClick = {}, label = { Text("🏥 Hospitals") })
                        AssistChip(onClick = {}, label = { Text("💊 Pharmacy") })
                        AssistChip(onClick = {}, label = { Text("🩺 Clinic") })
                    }
                }
            }
        }
    }
}
