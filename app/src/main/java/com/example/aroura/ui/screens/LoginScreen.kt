package com.example.aroura.ui.screens

import android.util.Patterns
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.aroura.ui.components.AdvancedAuroraBackground
import com.example.aroura.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var isLogin by remember { mutableStateOf(true) }
    
    // Form State
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    
    // Error State
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    
    // UI State
    var isVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    // Validation Logic
    fun validate(): Boolean {
        var isValid = true
        
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Please enter a valid email"
            isValid = false
        } else {
            emailError = null
        }

        if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        } else {
            passwordError = null
        }

        if (!isLogin && name.isBlank()) {
            nameError = "Name helps us know you better"
            isValid = false
        } else {
            nameError = null
        }

        return isValid
    }

    fun handleAuth() {
        if (validate()) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = if (isLogin) "Welcome back!" else "Account created!",
                    duration = SnackbarDuration.Short
                )
                delay(500)
                onLoginSuccess()
            }
        }
    }

    // Reset errors when switching modes
    LaunchedEffect(isLogin) {
        emailError = null
        passwordError = null
        nameError = null
    }

    // Fix: Background must be behind the Scaffold and fill the entire screen (ignoring system bars padding initially)
    Box(modifier = Modifier.fillMaxSize()) {
        AdvancedAuroraBackground()

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent,
            // We handle insets manually in the content or let Scaffold handle them via paddingValues
            contentWindowInsets = WindowInsets.systemBars // Default
        ) { paddingValues ->
            
            // Content Container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Content respects system bars
            ) {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically(
                        initialOffsetY = { 40 },
                        animationSpec = tween(800, easing = EaseOutExpo)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp), // Additional internal padding
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Header
                        Crossfade(targetState = isLogin, label = "header") { login ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (login) "Welcome Back" else "Join A.R.O.U.R.A",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = OffWhite,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Take a deep breath. You are safe here.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextDarkSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        // Segmented Control
                        Surface(
                            color = DeepSurface.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                AuthTab(
                                    text = "Login",
                                    isSelected = isLogin,
                                    onClick = { isLogin = true }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                AuthTab(
                                    text = "Sign Up",
                                    isSelected = !isLogin,
                                    onClick = { isLogin = false }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Inputs
                        Column(
                            modifier = Modifier.fillMaxWidth().animateContentSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ModernTextField(
                                value = email,
                                onValueChange = { 
                                    email = it
                                    if (emailError != null) emailError = null 
                                },
                                label = "Email Address",
                                error = emailError,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                            )

                            AnimatedVisibility(
                                visible = !isLogin,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                ModernTextField(
                                    value = name,
                                    onValueChange = { 
                                        name = it
                                        if (nameError != null) nameError = null
                                    },
                                    label = "Your Name (Optional)",
                                    error = nameError,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }

                            ModernTextField(
                                value = password,
                                onValueChange = { 
                                    password = it
                                    if (passwordError != null) passwordError = null
                                },
                                label = "Password",
                                isPassword = true,
                                error = passwordError,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))

                        // Action Button
                        val buttonText = if (isLogin) "Sign In" else "Create Account"
                        PulsingButtonLogin(
                            text = buttonText,
                            onClick = { handleAuth() }
                        )
                        
                        AnimatedVisibility(visible = isLogin) {
                            TextButton(onClick = { /* TODO */ }, modifier = Modifier.padding(top = 8.dp)) {
                                Text(
                                    text = "Forgot Password?",
                                    color = TextDarkSecondary,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Social Options
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = DeepSurface)
                            Text(
                                text = "OR CONTINUE WITH",
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = TextDarkSecondary.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.labelSmall
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = DeepSurface)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            AnimatedSocialButton(text = "Google", modifier = Modifier.weight(1f), onClick = onLoginSuccess)
                            AnimatedSocialButton(text = "Facebook", modifier = Modifier.weight(1f), onClick = onLoginSuccess)
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PulsingButtonLogin(text: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "scale")

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = MutedTeal,
            contentColor = MidnightCharcoal
        ),
        shape = RoundedCornerShape(16.dp),
        interactionSource = interactionSource
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AnimatedSocialButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "scale")

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(50.dp)
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DeepSurface),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = OffWhite,
            containerColor = Color.Transparent
        ),
        interactionSource = interactionSource
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun AuthTab(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MutedTeal else Color.Transparent,
        label = "tabBg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MidnightCharcoal else TextDarkSecondary,
        label = "tabContent"
    )
    
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(44.dp)
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = contentColor,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    error: String? = null,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val isError = error != null
    
    Column(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, color = if(isError) MaterialTheme.colorScheme.error else TextDarkSecondary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DeepSurface.copy(alpha = 0.8f),
                unfocusedContainerColor = DeepSurface.copy(alpha = 0.5f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorContainerColor = DeepSurface.copy(alpha = 0.5f),
                cursorColor = MutedTeal,
                focusedTextColor = OffWhite,
                unfocusedTextColor = OffWhite
            ),
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            isError = isError
        )
        
        AnimatedVisibility(visible = isError) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
