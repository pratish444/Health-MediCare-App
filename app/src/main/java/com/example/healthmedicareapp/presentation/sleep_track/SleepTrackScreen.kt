package com.example.healthmedicareapp.presentation.sleep_track

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
    onNavigateBack: (() -> Unit)? = null,
    viewModel: SleepTrackViewModel = hiltViewModel()
) {
    val isTracking   by viewModel.isTracking.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()

    val darkBg      = Color(0xFF0D1B2A)
    val cardBg      = Color(0xFF1E2A3A)
    val indigoPrimary = Color(0xFF3949AB)

    Scaffold(
        topBar         = {
            if (onNavigateBack != null) MediCareTopBar(title = "Sleep Tracker", onBack = onNavigateBack)
        },
        containerColor = darkBg
    ) { padding ->
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Inline header when used as tab
            if (onNavigateBack == null) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier         = Modifier
                            .size(44.dp)
                            .background(
                                Brush.linearGradient(listOf(Color(0xFF4776E6), Color(0xFF8E54E9))),
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Bedtime, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Sleep Tracker", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
                        Text("Track your rest patterns", fontSize = 13.sp, color = Color.White.copy(alpha = 0.55f))
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // ── Moon / Sleep illustration ─────────────────────────────────────
            Box(
                modifier         = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(Color(0xFF3949AB), Color(0xFF1A237E)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = if (isTracking) Icons.Rounded.SelfImprovement else Icons.Rounded.Bedtime,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(80.dp)
                )
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

            // ── Current track card ────────────────────────────────────────────
            if (currentTrack != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(20.dp),
                    colors   = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Nightlight, null, tint = Color(0xFF7C83E0), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Today's Sleep", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier             = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SleepTimeChip(Icons.Rounded.Bedtime, "Slept", formatTime(currentTrack!!.startTime), Color(0xFF7C83E0))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    if (currentTrack!!.endTime != null)
                                        calculateDuration(currentTrack!!.startTime, currentTrack!!.endTime!!)
                                    else "Ongoing",
                                    fontWeight = FontWeight.ExtraBold, color = Color(0xFF7C83E0), fontSize = 22.sp
                                )
                                Text("Duration", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                            }
                            SleepTimeChip(Icons.Rounded.WbSunny, "Woke up",
                                if (currentTrack!!.endTime != null) formatTime(currentTrack!!.endTime!!) else "--:--",
                                Color(0xFFFFD54F))
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // ── Start / Stop button ───────────────────────────────────────────
            Button(
                onClick  = { if (!isTracking) viewModel.startTracking() else viewModel.stopTracking() },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape    = RoundedCornerShape(18.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = if (!isTracking) indigoPrimary else Color(0xFFD32F2F)
                )
            ) {
                Icon(
                    if (!isTracking) Icons.Rounded.Bedtime else Icons.Rounded.WbSunny,
                    null, tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (!isTracking) "Start Sleep Tracking" else "Wake Up / Stop",
                    fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Sleep tips ────────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.TipsAndUpdates, null, tint = Color(0xFFFFD54F), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Sleep Tips", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    listOf(
                        "Adults need 7–9 hours of sleep per night",
                        "Maintain a consistent sleep schedule",
                        "Avoid screens 1 hour before bed",
                        "Keep your room cool and dark"
                    ).forEach { tip ->
                        Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Rounded.CheckCircle, null, tint = Color(0xFF7C83E0), modifier = Modifier.size(14.dp).padding(top = 2.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(tip, fontSize = 13.sp, color = Color.White.copy(alpha = 0.75f))
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SleepTimeChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, time: String, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(28.dp))
        Text(label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
        Text(time,  fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
    }
}

private fun formatTime(millis: Long): String {
    val hours       = (millis / 3600000) % 24
    val minutes     = (millis / 60000) % 60
    val amPm        = if (hours < 12) "AM" else "PM"
    val displayHour = if (hours % 12 == 0L) 12 else hours % 12
    return "$displayHour:${minutes.toString().padStart(2, '0')} $amPm"
}

private fun calculateDuration(start: Long, end: Long): String {
    val diff    = end - start
    val hours   = diff / 3600000
    val minutes = (diff % 3600000) / 60000
    return "${hours}h ${minutes}m"
}
