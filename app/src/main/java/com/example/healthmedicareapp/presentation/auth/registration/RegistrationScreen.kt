package com.example.healthmedicareapp.presentation.auth.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RegistrationScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMedicalDetails: () -> Unit,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        when (uiState) {
            is RegistrationUiState.Success -> onNavigateToMedicalDetails()
            is RegistrationUiState.Error -> {
                snackbarHostState.showSnackbar((uiState as RegistrationUiState.Error).message)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.linearGradient(
                    listOf(Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF66BB6A)),
                    Offset(0f, 0f), Offset(0f, Float.POSITIVE_INFINITY)
                )
            )
        )
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))
            Text("🩺", fontSize = 52.sp)
            Spacer(Modifier.height(8.dp))
            Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("Start your health journey", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
            Spacer(Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    val green = Color(0xFF2E7D32)
                    val fieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = green, focusedLabelColor = green
                    )

                    OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = green) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = fieldColors, singleLine = true)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = green) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = fieldColors, singleLine = true)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = green) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = fieldColors, singleLine = true)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password, onValueChange = { password = it }, label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = green) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = Color.Gray)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = fieldColors, singleLine = true)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirm Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = green) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        isError = confirmPassword.isNotEmpty() && password != confirmPassword,
                        supportingText = {
                            if (confirmPassword.isNotEmpty() && password != confirmPassword)
                                Text("Passwords don't match", color = Color.Red)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = fieldColors, singleLine = true)
                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = { if (password == confirmPassword) viewModel.signUp(email.trim(), password, fullName.trim(), mobile.trim()) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = green),
                        enabled = uiState !is RegistrationUiState.Loading && fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank() && password == confirmPassword
                    ) {
                        if (uiState is RegistrationUiState.Loading)
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        else Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.Center) {
                Text("Already have an account? ", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                Text("Sign In", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp,
                    modifier = Modifier.clickable { onNavigateToLogin() })
            }
            Spacer(Modifier.height(32.dp))
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}
