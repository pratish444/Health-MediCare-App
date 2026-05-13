package com.example.healthmedicareapp.presentation.sleep_track

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthmedicareapp.presentation.components.MediCareTopBar

@Composable
fun SleepTrackScreen(
    onNavigateBack: () -> Unit,
    viewModel: SleepTrackViewModel = hiltViewModel()
) {
    val isTracking by viewModel.isTracking.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()

    Scaffold(
        topBar = { MediCareTopBar(title = "Sleep Tracker", onBack = onNavigateBack) },
        containerColor = Color(0xFF0D1B2A)
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // Sleep moon illustration
            Box(
                modifier = Modifier.size(160.dp).clip(CircleShape)
                    .background(Brush.radialGradient(listOf(Color(0xFF3949AB), Color(0xFF1A237E)))),
                contentAlignment = Alignment.Center
            ) {
                Text(if (isTracking) "😴" else "🌙", fontSize = 72.sp)
            }

            Spacer(Modifier.height(24.dp))
            Text(
                if (isTracking) "Sleep in Progress..." else "Ready to Sleep?",
                fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White
            )
            Text(
                if (isTracking) "Tracking your sleep duration" else "Tap Start to begin tracking",
                fontSize = 14.sp, color = Color.White.copy(alpha = 0.6f)
            )

            Spacer(Modifier.height(32.dp))

            // Current track info
            if (currentTrack != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A3A))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Today's Sleep", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Bedtime, null, tint = Color(0xFF7C83E0), modifier = Modifier.size(28.dp))
                                Text("Slept", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                                Text(formatTime(currentTrack!!.startTime), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    if (currentTrack!!.endTime != null) calculateDuration(currentTrack!!.startTime, currentTrack!!.endTime!!) else "Ongoing",
                                    fontWeight = FontWeight.ExtraBold, color = Color(0xFF7C83E0), fontSize = 20.sp
                                )
                                Text("Duration", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.WbSunny, null, tint = Color(0xFFFFD54F), modifier = Modifier.size(28.dp))
                                Text("Woke up", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                                Text(if (currentTrack!!.endTime != null) formatTime(currentTrack!!.endTime!!) else "--:--", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // Start/Stop button
            if (!isTracking) {
                Button(
                    onClick = { viewModel.startTracking() },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3949AB))
                ) {
                    Icon(Icons.Default.Bedtime, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Start Sleep Tracking", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            } else {
                Button(
                    onClick = { viewModel.stopTracking() },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Icon(Icons.Default.WbSunny, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Wake Up / Stop Tracking", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Tips card
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A3A))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("💡 Sleep Tips", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    Spacer(Modifier.height(8.dp))
                    listOf("Adults need 7-9 hours of sleep per night", "Maintain a consistent sleep schedule", "Avoid screens 1 hour before bed", "Keep your room cool and dark").forEach { tip ->
                        Text("• $tip", fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f), modifier = Modifier.padding(vertical = 3.dp))
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun formatTime(millis: Long): String {
    val hours = (millis / 3600000) % 24
    val minutes = (millis / 60000) % 60
    val amPm = if (hours < 12) "AM" else "PM"
    val displayHour = if (hours % 12 == 0L) 12 else hours % 12
    return "$displayHour:${minutes.toString().padStart(2, '0')} $amPm"
}

private fun calculateDuration(start: Long, end: Long): String {
    val diff = end - start
    val hours = diff / 3600000
    val minutes = (diff % 3600000) / 60000
    return "${hours}h ${minutes}m"
}
