package com.example.aroura.ui.screens

import android.util.Patterns
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.components.ArouraPrimaryButton
import com.example.aroura.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Login / Signup Screen
 * 
 * Premium social-first authentication flow
 * Features:
 * - Initial view: Social auth options (Google, Facebook, Email)
 * - "Continue with Email" reveals email form with smooth animation
 * - Continuous aurora background (foreground remains stable)
 * - Toggle between Login/Signup modes
 * - Calm, never rushed transitions
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit = {}
) {
    // ═══════════════════════════════════════════════════════════════════════════
    // STATE MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════════
    
    // View state: "social" shows social buttons, "email" shows email form
    var viewState by remember { mutableStateOf("social") }
    var isLogin by remember { mutableStateOf(true) }
    
    // Form state
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    // Error state
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    
    // UI state
    var isVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(Unit) {
        delay(200)
        isVisible = true
    }
    
    // Reset errors when switching modes
    LaunchedEffect(isLogin, viewState) {
        emailError = null
        passwordError = null
        nameError = null
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // VALIDATION
    // ═══════════════════════════════════════════════════════════════════════════
    
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
            nameError = "Name helps us personalize your experience"
            isValid = false
        } else {
            nameError = null
        }
        
        return isValid
    }
    
    fun handleAuth() {
        if (validate()) {
            scope.launch {
                isLoading = true
                delay(1200) // Simulate API call
                snackbarHostState.showSnackbar(
                    message = if (isLogin) "Welcome back!" else "Account created!",
                    duration = SnackbarDuration.Short
                )
                delay(400)
                isLoading = false
                onLoginSuccess()
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // UI LAYOUT
    // ═══════════════════════════════════════════════════════════════════════════
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Premium Aurora Background (continuous, calming)
        ArouraBackground()
        
        Scaffold(
            snackbarHost = { 
                SnackbarHost(snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = DeepSurface,
                        contentColor = OffWhite,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            },
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.systemBars
        ) { paddingValues ->
            
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800)) + slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec = tween(800, easing = FastOutSlowInEasing)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = ArouraSpacing.screenHorizontal.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(ArouraSpacing.xxl.dp))
                    
                    // ═══════════════════════════════════════════════════════════════
                    // HEADER
                    // ═══════════════════════════════════════════════════════════════
                    
                    AnimatedContent(
                        targetState = viewState,
                        transitionSpec = {
                            fadeIn(tween(400)) togetherWith fadeOut(tween(300))
                        },
                        label = "headerTransition"
                    ) { state ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (state == "email") {
                                // Back button
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.Start)
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(DeepSurface.copy(alpha = 0.6f))
                                        .clickable { viewState = "social" },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = OffWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
                            }
                            
                            Text(
                                text = when {
                                    state == "social" -> "Welcome"
                                    isLogin -> "Welcome Back"
                                    else -> "Join A.R.O.U.R.A"
                                },
                                style = MaterialTheme.typography.headlineLarge,
                                color = OffWhite,
                                fontWeight = FontWeight.Light
                            )
                            
                            Spacer(modifier = Modifier.height(ArouraSpacing.xs.dp))
                            
                            Text(
                                text = "Take a deep breath. You are safe here.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextDarkSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(ArouraSpacing.xxl.dp))
                    
                    // ═══════════════════════════════════════════════════════════════
                    // CONTENT AREA
                    // ═══════════════════════════════════════════════════════════════
                    
                    AnimatedContent(
                        targetState = viewState,
                        transitionSpec = {
                            if (targetState == "email") {
                                // Entering email view
                                (fadeIn(tween(500, delayMillis = 100)) + 
                                 slideInVertically(
                                     initialOffsetY = { it / 4 },
                                     animationSpec = spring(
                                         dampingRatio = Spring.DampingRatioLowBouncy,
                                         stiffness = Spring.StiffnessLow
                                     )
                                 )) togetherWith 
                                (fadeOut(tween(300)) + slideOutVertically(
                                    targetOffsetY = { -it / 4 },
                                    animationSpec = tween(300)
                                ))
                            } else {
                                // Going back to social
                                (fadeIn(tween(400)) + slideInVertically(
                                    initialOffsetY = { -it / 4 },
                                    animationSpec = tween(400)
                                )) togetherWith 
                                (fadeOut(tween(200)) + slideOutVertically(
                                    targetOffsetY = { it / 4 },
                                    animationSpec = tween(200)
                                ))
                            }
                        },
                        label = "contentTransition",
                        modifier = Modifier.weight(1f)
                    ) { state ->
                        when (state) {
                            "social" -> SocialAuthContent(
                                onGoogleClick = { onLoginSuccess() },
                                onFacebookClick = { onLoginSuccess() },
                                onEmailClick = { viewState = "email" }
                            )
                            "email" -> EmailAuthContent(
                                isLogin = isLogin,
                                onToggleMode = { isLogin = !isLogin },
                                email = email,
                                onEmailChange = { email = it; emailError = null },
                                emailError = emailError,
                                password = password,
                                onPasswordChange = { password = it; passwordError = null },
                                passwordError = passwordError,
                                passwordVisible = passwordVisible,
                                onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
                                name = name,
                                onNameChange = { name = it; nameError = null },
                                nameError = nameError,
                                isLoading = isLoading,
                                onSubmit = { handleAuth() }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SOCIAL AUTH CONTENT
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SocialAuthContent(
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit,
    onEmailClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp)
    ) {
        Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
        
        // Google Button
        SocialAuthButton(
            text = "Continue with Google",
            iconContent = {
                // Google Icon (simplified)
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "G",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFF4285F4),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            onClick = onGoogleClick
        )
        
        // Facebook Button
        SocialAuthButton(
            text = "Continue with Facebook",
            iconContent = {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(Color(0xFF1877F2), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "f",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            onClick = onFacebookClick
        )
        
        // Email Button
        SocialAuthButton(
            text = "Continue with Email",
            iconContent = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = OffWhite,
                    modifier = Modifier.size(22.dp)
                )
            },
            onClick = onEmailClick
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Footer
        Text(
            text = "By continuing, you agree to our Terms of Service\nand Privacy Policy",
            style = MaterialTheme.typography.bodySmall,
            color = TextDarkTertiary,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
        
        Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
    }
}

@Composable
private fun SocialAuthButton(
    text: String,
    iconContent: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "socialScale"
    )
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(29.dp),
        color = DeepSurface.copy(alpha = 0.7f),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.1f),
                    Color.White.copy(alpha = 0.03f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = ArouraSpacing.lg.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            iconContent()
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = OffWhite,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// EMAIL AUTH CONTENT
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun EmailAuthContent(
    isLogin: Boolean,
    onToggleMode: () -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordError: String?,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    nameError: String?,
    isLoading: Boolean,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp)
    ) {
        // Mode Toggle
        AuthModeToggle(
            isLogin = isLogin,
            onToggle = onToggleMode
        )
        
        Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
        
        // Email Field
        ArouraTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email Address",
            error = emailError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        
        // Name Field (Signup only)
        AnimatedVisibility(
            visible = !isLogin,
            enter = expandVertically(
                animationSpec = spring(stiffness = Spring.StiffnessLow)
            ) + fadeIn(),
            exit = shrinkVertically(
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            ) + fadeOut()
        ) {
            ArouraTextField(
                value = name,
                onValueChange = onNameChange,
                label = "Your Name",
                error = nameError,
                modifier = Modifier.padding(top = ArouraSpacing.md.dp)
            )
        }
        
        // Password Field
        ArouraTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Password",
            error = passwordError,
            isPassword = true,
            passwordVisible = passwordVisible,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        
        // Forgot Password (Login only)
        AnimatedVisibility(visible = isLogin) {
            TextButton(
                onClick = { /* TODO: Forgot password */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedTeal
                )
            }
        }
        
        Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
        
        // Submit Button
        ArouraPrimaryButton(
            text = if (isLogin) "Sign In" else "Create Account",
            onClick = onSubmit,
            isLoading = isLoading
        )
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun AuthModeToggle(
    isLogin: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        color = DeepSurface.copy(alpha = 0.7f),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.wrapContentWidth()
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            AuthToggleTab(
                text = "Login",
                isSelected = isLogin,
                onClick = { if (!isLogin) onToggle() }
            )
            Spacer(modifier = Modifier.width(4.dp))
            AuthToggleTab(
                text = "Sign Up",
                isSelected = !isLogin,
                onClick = { if (isLogin) onToggle() }
            )
        }
    }
}

@Composable
private fun AuthToggleTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MutedTeal else Color.Transparent,
        animationSpec = tween(300),
        label = "tabBg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MidnightCharcoal else TextDarkSecondary,
        animationSpec = tween(300),
        label = "tabContent"
    )
    
    Box(
        modifier = Modifier
            .width(110.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
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
private fun ArouraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String? = null,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    val isError = error != null
    
    Column(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { 
                Text(
                    text = label,
                    color = if (isError) GentleError else TextDarkSecondary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DeepSurface.copy(alpha = 0.8f),
                unfocusedContainerColor = DeepSurface.copy(alpha = 0.5f),
                errorContainerColor = DeepSurface.copy(alpha = 0.5f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                cursorColor = MutedTeal,
                focusedTextColor = OffWhite,
                unfocusedTextColor = OffWhite
            ),
            singleLine = true,
            isError = isError,
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = keyboardOptions,
            trailingIcon = if (isPassword && onTogglePasswordVisibility != null) {
                {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Clear else Icons.Default.Lock,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = TextDarkSecondary
                        )
                    }
                }
            } else null
        )
        
        AnimatedVisibility(
            visible = isError,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Text(
                text = error ?: "",
                color = GentleError,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 6.dp)
            )
        }
    }
}
