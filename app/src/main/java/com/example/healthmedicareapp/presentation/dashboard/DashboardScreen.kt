package com.example.healthmedicareapp.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DashboardScreen(
    onNavigateToCalorie: () -> Unit,
    onNavigateToSleep: () -> Unit,
    onNavigateToArticles: () -> Unit,
    onNavigateToMaps: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userName = currentUser?.displayName?.takeIf { it.isNotBlank() } ?: "User"

    Scaffold(
        containerColor = Color(0xFFF0F4FF)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF0D47A1), Color(0xFF1976D2)),
                            Offset(0f, 0f), Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(52.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 26.sp)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Good day,", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                        Text(userName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Health stats
            when (val state = uiState) {
                is DashboardUiState.Success -> {
                    val med = state.medicalDetails
                    if (med != null) {
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("BMI", String.format("%.1f", med.bmi), med.weightStatus, Color(0xFF1565C0), Modifier.weight(1f))
                            StatCard("Blood", med.bloodGroup, "Group", Color(0xFFD32F2F), Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("BP", "${med.systolicBP}/${med.diastolicBP}", "mmHg", Color(0xFF7B1FA2), Modifier.weight(1f))
                            StatCard("Age", "${med.age}", "years", Color(0xFF00796B), Modifier.weight(1f))
                        }
                    }
                }
                is DashboardUiState.Loading -> {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1565C0))
                    }
                }
                else -> {}
            }

            Spacer(Modifier.height(24.dp))

            // Feature grid
            Text("Health Features", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A2E), modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FeatureCard("🥗", "Calorie\nManager", Color(0xFFE8F5E9), Color(0xFF2E7D32), Modifier.weight(1f), onNavigateToCalorie)
                FeatureCard("😴", "Sleep\nTracker", Color(0xFFEDE7F6), Color(0xFF512DA8), Modifier.weight(1f), onNavigateToSleep)
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FeatureCard("📰", "Health\nArticles", Color(0xFFE3F2FD), Color(0xFF1565C0), Modifier.weight(1f), onNavigateToArticles)
                FeatureCard("🏥", "Find\nHospital", Color(0xFFFFEBEE), Color(0xFFD32F2F), Modifier.weight(1f), onNavigateToMaps)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, subtitle: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier, shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
            }
            Spacer(Modifier.height(10.dp))
            Text(label, fontSize = 11.sp, color = Color.Gray)
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Color(0xFF1A1A2E))
            Text(subtitle, fontSize = 11.sp, color = color, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun FeatureCard(emoji: String, title: String, bgColor: Color, iconColor: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.Start) {
            Text(emoji, fontSize = 36.sp)
            Spacer(Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = iconColor, lineHeight = 20.sp)
        }
    }
}
