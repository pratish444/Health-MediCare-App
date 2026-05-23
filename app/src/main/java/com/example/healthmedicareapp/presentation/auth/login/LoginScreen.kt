package com.example.healthmedicareapp.presentation.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthmedicareapp.ui.theme.GradientHeader

@Composable
fun LoginScreen(
    onNavigateToRegister      : () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess            : () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState          by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var email            by remember { mutableStateOf("") }
    var password         by remember { mutableStateOf("") }
    var passwordVisible  by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.Success -> onLoginSuccess()
            is LoginUiState.Error   -> {
                snackbarHostState.showSnackbar((uiState as LoginUiState.Error).message)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ── Gradient background ───────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        GradientHeader + listOf(Color(0xFF42A5F5)),
                        start = Offset(0f, 0f),
                        end   = Offset(0f, Float.POSITIVE_INFINITY)
                    )
                )
        )

        Column(
            modifier            = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))

            // ── Logo ──────────────────────────────────────────────────────────
            Box(
                modifier         = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.LocalHospital,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(52.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text("MediCare", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("Your Health Companion", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))

            Spacer(Modifier.height(40.dp))

            // ── Login card ────────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(28.dp),
                colors   = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(modifier = Modifier.padding(28.dp)) {
                    Text("Welcome Back", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Text("Sign in to continue", fontSize = 13.sp, color = Color(0xFF666666))
                    Spacer(Modifier.height(26.dp))

                    OutlinedTextField(
                        value         = email,
                        onValueChange = { email = it },
                        label         = { Text("Email") },
                        leadingIcon   = { Icon(Icons.Rounded.MailOutline, null, tint = Color(0xFF1565C0)) },
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1565C0),
                            focusedLabelColor  = Color(0xFF1565C0)
                        ),
                        singleLine = true
                    )
                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value         = password,
                        onValueChange = { password = it },
                        label         = { Text("Password") },
                        leadingIcon = { Icon(Icons.Rounded.Lock, null, tint = Color(0xFF1565C0)) },                        trailingIcon  = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                    null,
                                    tint = Color(0xFF888888)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1565C0),
                            focusedLabelColor  = Color(0xFF1565C0)
                        ),
                        singleLine = true
                    )

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text       = "Forgot Password?",
                        color      = Color(0xFF1565C0),
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier   = Modifier
                            .align(Alignment.End)
                            .clickable { onNavigateToForgotPassword() }
                    )
                    Spacer(Modifier.height(26.dp))

                    Button(
                        onClick  = { viewModel.signIn(email.trim(), password) },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp),
                        enabled  = uiState !is LoginUiState.Loading && email.isNotBlank() && password.isNotBlank()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF0D47A1), Color(0xFF1976D2)),
                                        start = Offset(0f, 0f),
                                        end   = Offset(Float.POSITIVE_INFINITY, 0f)
                                    ),
                                    RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState is LoginUiState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.Center) {
                Text("Don't have an account? ", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                Text(
                    text       = "Register",
                    color      = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 14.sp,
                    modifier   = Modifier.clickable { onNavigateToRegister() }
                )
            }
            Spacer(Modifier.height(32.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter)
        )
    }
}
