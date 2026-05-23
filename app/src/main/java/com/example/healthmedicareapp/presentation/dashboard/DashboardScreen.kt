package com.example.healthmedicareapp.presentation.dashboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.healthmedicareapp.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────
private fun timeGreeting(): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11  -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..20 -> "Good Evening"
        else      -> "Good Night"
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToCalorie : () -> Unit,
    onNavigateToSleep   : () -> Unit,
    onNavigateToArticles: () -> Unit,
    onNavigateToMaps    : () -> Unit,
    isDarkMode          : Boolean,
    onToggleDarkMode    : () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState      by viewModel.uiState.collectAsState()
    val currentUser  = FirebaseAuth.getInstance().currentUser
    val displayName  = currentUser?.displayName?.takeIf { it.isNotBlank() } ?: "User"
    val firstName    = displayName.split(" ").firstOrNull() ?: displayName
    val photoUrl     = currentUser?.photoUrl
    val greeting     = timeGreeting()

    var showProfileSheet by remember { mutableStateOf(false) }

    // ── Staggered entrance animations ────────────────────────────────────────
    val cardAlphas  = remember { List(4) { Animatable(0f) } }
    val cardOffsets = remember { List(4) { Animatable(40f) } }

    LaunchedEffect(Unit) {
        cardAlphas.forEachIndexed { i, anim ->
            launch {
                delay(i * 90L)
                launch { anim.animateTo(1f, tween(400, easing = FastOutSlowInEasing)) }
                launch { cardOffsets[i].animateTo(0f, tween(400, easing = FastOutSlowInEasing)) }
            }
        }
    }

    val bgColor = MaterialTheme.colorScheme.background

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ── Gradient Header ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            GradientHeader,
                            start = Offset(0f, 0f),
                            end   = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile avatar (clickable)
                    ProfileAvatar(
                        photoUrl    = photoUrl?.toString(),
                        displayName = displayName,
                        onClick     = { showProfileSheet = true }
                    )

                    Spacer(Modifier.width(14.dp))

                    // Greeting text
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "$greeting, $firstName 👋",
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                        Text(
                            "Stay healthy today!",
                            fontSize = 13.sp,
                            color    = Color.White.copy(alpha = 0.75f)
                        )
                    }

                    // Dark mode toggle
                    IconButton(onClick = onToggleDarkMode) {
                        Icon(
                            imageVector        = if (isDarkMode) Icons.Rounded.WbSunny else Icons.Rounded.DarkMode,
                            contentDescription = "Toggle dark mode",
                            tint               = Color.White,
                            modifier           = Modifier.size(24.dp)
                        )
                    }

                    // Map / hospital quick-access
                    IconButton(onClick = onNavigateToMaps) {
                        Icon(
                            imageVector        = Icons.Rounded.LocationOn,
                            contentDescription = "Find hospital",
                            tint               = Color.White,
                            modifier           = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Health Stats ─────────────────────────────────────────────────
            when (val state = uiState) {
                is DashboardUiState.Success -> {
                    val med = state.medicalDetails
                    if (med != null) {
                        Row(
                            modifier             = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AnimatedStatCard(
                                label    = "BMI",
                                value    = String.format("%.1f", med.bmi),
                                subtitle = med.weightStatus,
                                gradient = listOf(Blue700, Blue300),
                                icon     = Icons.Rounded.MonitorWeight,
                                modifier = Modifier.weight(1f),
                                alpha    = cardAlphas[0].value,
                                offsetY  = cardOffsets[0].value
                            )
                            AnimatedStatCard(
                                label    = "Blood",
                                value    = med.bloodGroup,
                                subtitle = "Group",
                                gradient = listOf(Red700, Color(0xFFEF9A9A)),
                                icon     = Icons.Rounded.Favorite,
                                modifier = Modifier.weight(1f),
                                alpha    = cardAlphas[1].value,
                                offsetY  = cardOffsets[1].value
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier             = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AnimatedStatCard(
                                label    = "BP",
                                value    = "${med.systolicBP}/${med.diastolicBP}",
                                subtitle = "mmHg",
                                gradient = listOf(Purple700, Indigo400),
                                icon     = Icons.Rounded.Bloodtype,
                                modifier = Modifier.weight(1f),
                                alpha    = cardAlphas[2].value,
                                offsetY  = cardOffsets[2].value
                            )
                            AnimatedStatCard(
                                label    = "Age",
                                value    = "${med.age}",
                                subtitle = "years",
                                gradient = listOf(Teal500, Green400),
                                icon     = Icons.Rounded.Cake,
                                modifier = Modifier.weight(1f),
                                alpha    = cardAlphas[3].value,
                                offsetY  = cardOffsets[3].value
                            )
                        }
                    }
                }
                is DashboardUiState.Loading -> {
                    Box(
                        Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                else -> {}
            }

            Spacer(Modifier.height(28.dp))

            // ── Section heading ───────────────────────────────────────────────
            Text(
                "Health Features",
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp,
                color      = MaterialTheme.colorScheme.onBackground,
                modifier   = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(14.dp))

            // ── Feature Cards ─────────────────────────────────────────────────
            Row(
                modifier             = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientFeatureCard(
                    icon     = Icons.Rounded.LocalDining,
                    title    = "Calorie\nManager",
                    gradient = GradientCalorie,
                    modifier = Modifier.weight(1f),
                    onClick  = onNavigateToCalorie
                )
                GradientFeatureCard(
                    icon     = Icons.Rounded.Bedtime,
                    title    = "Sleep\nTracker",
                    gradient = GradientSleep,
                    modifier = Modifier.weight(1f),
                    onClick  = onNavigateToSleep
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier             = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientFeatureCard(
                    icon     = Icons.Rounded.Article,
                    title    = "Health\nArticles",
                    gradient = GradientArticle,
                    modifier = Modifier.weight(1f),
                    onClick  = onNavigateToArticles
                )
                GradientFeatureCard(
                    icon     = Icons.Rounded.LocalHospital,
                    title    = "Find\nHospital",
                    gradient = GradientHospital,
                    modifier = Modifier.weight(1f),
                    onClick  = onNavigateToMaps
                )
            }

            Spacer(Modifier.height(32.dp))
        }

        // ── Profile Bottom Sheet ──────────────────────────────────────────────
        if (showProfileSheet) {
            ModalBottomSheet(
                onDismissRequest = { showProfileSheet = false },
                containerColor   = MaterialTheme.colorScheme.surface,
                shape            = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                ProfileBottomSheetContent(
                    displayName = displayName,
                    email       = currentUser?.email ?: "—",
                    photoUrl    = photoUrl?.toString(),
                    onDismiss   = { showProfileSheet = false }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Profile Avatar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ProfileAvatar(
    photoUrl   : String?,
    displayName: String,
    onClick    : () -> Unit
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .shadow(4.dp, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!photoUrl.isNullOrBlank()) {
            AsyncImage(
                model              = photoUrl,
                contentDescription = "Profile photo",
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize().clip(CircleShape)
            )
        } else {
            // Initials fallback
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF42A5F5), Color(0xFF1565C0))),
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Animated stat card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AnimatedStatCard(
    label   : String,
    value   : String,
    subtitle: String,
    gradient: List<Color>,
    icon    : ImageVector,
    modifier: Modifier = Modifier,
    alpha   : Float,
    offsetY : Float
) {
    val isDark = LocalDarkMode.current.isDark
    val cardBg = if (isDark) Surface2Dark else SurfaceLight

    Card(
        modifier  = modifier
            .alpha(alpha)
            .offset(y = offsetY.dp),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(if (isDark) 0.dp else 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier         = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f))
            Text(
                value,
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 22.sp,
                color      = MaterialTheme.colorScheme.onSurface
            )
            Text(
                subtitle,
                fontSize   = 11.sp,
                color      = gradient.first(),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Gradient feature card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun GradientFeatureCard(
    icon    : ImageVector,
    title   : String,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    onClick : () -> Unit
) {
    Card(
        modifier  = modifier
            .clickable(onClick = onClick)
            .aspectRatio(1f),
        shape     = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        gradient,
                        start = Offset(0f, 0f),
                        end   = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier            = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier         = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                }
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = Color.White,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Profile bottom sheet
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ProfileBottomSheetContent(
    displayName: String,
    email      : String,
    photoUrl   : String?,
    onDismiss  : () -> Unit
) {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Drag handle
        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
        )
        Spacer(Modifier.height(20.dp))

        // Avatar (large)
        Box(
            modifier         = Modifier.size(80.dp).clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (!photoUrl.isNullOrBlank()) {
                AsyncImage(
                    model              = photoUrl,
                    contentDescription = null,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(GradientHeader)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                        fontSize   = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(displayName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(4.dp))
        Text(email, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Spacer(Modifier.height(28.dp))

        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                onDismiss()
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape    = RoundedCornerShape(14.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = Red700)
        ) {
            Icon(Icons.Rounded.Logout, null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Sign Out", fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(Modifier.height(24.dp))
    }
}
