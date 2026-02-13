package com.example.aroura.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.aroura.data.api.UserProfileData
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.theme.*
import com.example.aroura.ui.viewmodels.ProfileState
import com.example.aroura.ui.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    onBack: () -> Unit, 
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    badgeEarned: Boolean = false
) {
    val context = LocalContext.current
    val profileState by profileViewModel.profileState.collectAsState()
    val userProfile by profileViewModel.userProfile.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    val uploadProgress by profileViewModel.uploadProgress.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    var showPictureOptions by remember { mutableStateOf(false) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { profileViewModel.uploadProfilePicture(it) }
    }
    
    // Load profile on first composition
    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()
    }
    
    // Handle profile state changes
    LaunchedEffect(profileState) {
        when (val state = profileState) {
            is ProfileState.PictureUploaded -> {
                snackbarHostState.showSnackbar("Profile picture updated!")
                profileViewModel.resetState()
            }
            is ProfileState.PictureDeleted -> {
                snackbarHostState.showSnackbar("Profile picture removed")
                profileViewModel.resetState()
            }
            is ProfileState.Updated -> {
                snackbarHostState.showSnackbar("Profile updated!")
                profileViewModel.resetState()
            }
            is ProfileState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                profileViewModel.resetState()
            }
            else -> {}
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        ArouraBackground()
        
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "Profile", 
                            color = OffWhite,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Light
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = OffWhite)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = ArouraSpacing.screenHorizontal.dp),
                verticalArrangement = Arrangement.spacedBy(ArouraSpacing.lg.dp),
                contentPadding = PaddingValues(bottom = ArouraSpacing.xxl.dp)
            ) {
                // User Header
                item {
                    ProfileHeader(
                        userProfile = userProfile,
                        isLoading = isLoading || uploadProgress,
                        onPictureClick = { showPictureOptions = true },
                        onNameClick = { showEditNameDialog = true },
                        badgeEarned = badgeEarned
                    )
                }

                item { 
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.05f),
                        thickness = 1.dp
                    ) 
                }

                // General Settings
                item { SettingsSectionTitle("General") }
                item { 
                    SettingsItem(
                        "Language", 
                        "English", 
                        Icons.Default.Info
                    ) { onNavigate("language") }
                }
                item { 
                    ToggleSettingsItem(
                        "AI Memory", 
                        "Allow AI to remember context", 
                        userProfile?.preferences?.aiMemory ?: true, 
                        Icons.Default.Settings
                    ) 
                }

                // Content Settings
                item { SettingsSectionTitle("Content") }
                item { 
                    SettingsItem(
                        "Devotional Preferences", 
                        userProfile?.preferences?.devotionalType?.replaceFirstChar { it.uppercase() } ?: "All Religions", 
                        Icons.Default.Favorite
                    ) { onNavigate("devotional") }
                }

                // Privacy Settings
                item { SettingsSectionTitle("Privacy & Safety") }
                item { 
                    SettingsItem(
                        "Privacy & Data", 
                        "Manage your data", 
                        Icons.Default.Lock
                    ) { onNavigate("privacy") }
                }
                item { 
                    SettingsItem(
                        "Ethics & Disclaimers", 
                        "Read our manifesto", 
                        Icons.Default.Info
                    ) { onNavigate("ethics") }
                }
                
                item { Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp)) }
                
                // Logout Button
                item {
                    PremiumLogoutButton(onClick = onLogout)
                }
            }
        }
        
        // Profile Picture Options Dialog
        if (showPictureOptions) {
            ProfilePictureOptionsDialog(
                hasExistingPicture = userProfile?.profilePicture != null,
                onDismiss = { showPictureOptions = false },
                onChoosePhoto = {
                    showPictureOptions = false
                    imagePickerLauncher.launch("image/*")
                },
                onRemovePhoto = {
                    showPictureOptions = false
                    profileViewModel.deleteProfilePicture()
                }
            )
        }
        
        // Edit Name Dialog
        if (showEditNameDialog) {
            EditNameDialog(
                currentName = userProfile?.displayName ?: "",
                onDismiss = { showEditNameDialog = false },
                onSave = { newName ->
                    showEditNameDialog = false
                    profileViewModel.updateProfile(displayName = newName)
                }
            )
        }
    }
}

@Composable
private fun PremiumLogoutButton(onClick: () -> Unit) {
    var showConfirmation by remember { mutableStateOf(false) }
    
    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            containerColor = DeepSurface,
            title = {
                Text(
                    "Log Out?",
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    "Are you sure you want to log out? You'll need to sign in again to continue.",
                    color = TextDarkSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmation = false
                        onClick()
                    }
                ) {
                    Text("Log Out", color = GentleError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmation = false }) {
                    Text("Cancel", color = MutedTeal)
                }
            }
        )
    }
    
    Button(
        onClick = { showConfirmation = true },
        colors = ButtonDefaults.buttonColors(
            containerColor = GentleError.copy(alpha = 0.12f), 
            contentColor = GentleError
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(ArouraSpacing.cardRadius.dp),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
            brush = androidx.compose.ui.graphics.SolidColor(GentleError.copy(alpha = 0.3f))
        )
    ) {
        Icon(
            Icons.Default.Close,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "Log Out",
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MutedTeal,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun SettingsItem(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = TextDarkSecondary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = OffWhite)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextDarkSecondary)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextDarkSecondary)
    }
}

@Composable
fun ToggleSettingsItem(title: String, subtitle: String, initialChecked: Boolean, icon: ImageVector) {
    var checked by remember { mutableStateOf(initialChecked) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = TextDarkSecondary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = OffWhite)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextDarkSecondary)
        }
        Switch(
            checked = checked, 
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MutedTeal,
                checkedTrackColor = MutedTeal.copy(alpha = 0.3f),
                uncheckedThumbColor = TextDarkSecondary,
                uncheckedTrackColor = DeepSurface
            )
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Profile Header Component
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ProfileHeader(
    userProfile: UserProfileData?,
    isLoading: Boolean,
    onPictureClick: () -> Unit,
    onNameClick: () -> Unit,
    badgeEarned: Boolean = false
) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ArouraSpacing.lg.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Avatar with edit option
        Box(
            modifier = Modifier
                .size(100.dp)
                .clickable(onClick = onPictureClick),
            contentAlignment = Alignment.Center
        ) {
            // Golden ring for badge earners
            if (badgeEarned) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent, CircleShape)
                        .border(
                            width = 3.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFFD700),
                                    Color(0xFFFFA500),
                                    Color(0xFFFFD700)
                                )
                            ),
                            shape = CircleShape
                        )
                )
            }
            
            if (isLoading) {
                // Loading state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MutedTeal.copy(alpha = 0.3f),
                                    MutedTeal.copy(alpha = 0.1f)
                                )
                            ),
                            CircleShape
                        )
                        .border(2.dp, MutedTeal.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MutedTeal,
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 2.dp
                    )
                }
            } else if (userProfile?.profilePicture != null) {
                // Profile picture from Cloudinary or OAuth provider
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(userProfile.profilePicture)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(2.dp, MutedTeal.copy(alpha = 0.6f), CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Default avatar
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MutedTeal.copy(alpha = 0.3f),
                                    MutedTeal.copy(alpha = 0.1f)
                                )
                            ),
                            CircleShape
                        )
                        .border(2.dp, MutedTeal.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person, 
                        null, 
                        tint = OffWhite, 
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            
            // Camera icon overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
                    .background(MutedTeal, CircleShape)
                    .border(2.dp, DeepSurface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Change picture",
                    tint = OffWhite,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
        
        // Display Name (clickable to edit)
        Row(
            modifier = Modifier.clickable(onClick = onNameClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                userProfile?.displayName ?: "Loading...", 
                style = MaterialTheme.typography.headlineSmall, 
                color = OffWhite,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.Edit,
                contentDescription = "Edit name",
                tint = TextDarkSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Email
        Text(
            userProfile?.email ?: "", 
            style = MaterialTheme.typography.bodyMedium, 
            color = TextDarkSecondary
        )
        
        // Auth provider badge
        userProfile?.authProvider?.let { provider ->
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = MutedTeal.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = when (provider) {
                        "google" -> "Connected with Google"
                        "facebook" -> "Connected with Facebook"
                        else -> "Email Account"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedTeal,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
        
        // Self-Aware Badge
        if (badgeEarned) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = Color(0xFFFFD700).copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🏆",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Self-Aware",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Profile Picture Options Dialog
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ProfilePictureOptionsDialog(
    hasExistingPicture: Boolean,
    onDismiss: () -> Unit,
    onChoosePhoto: () -> Unit,
    onRemovePhoto: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepSurface,
        title = {
            Text(
                "Profile Picture",
                color = OffWhite,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column {
                // Choose Photo Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onChoosePhoto)
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = MutedTeal,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        if (hasExistingPicture) "Change Photo" else "Choose Photo",
                        color = OffWhite,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                // Remove Photo Option (only if there's an existing picture)
                if (hasExistingPicture) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onRemovePhoto)
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = GentleError,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Remove Photo",
                            color = GentleError,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextDarkSecondary)
            }
        }
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// Edit Name Dialog
// ═══════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepSurface,
        title = {
            Text(
                "Edit Name",
                color = OffWhite,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Display Name") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MutedTeal,
                    unfocusedBorderColor = TextDarkSecondary.copy(alpha = 0.5f),
                    focusedLabelColor = MutedTeal,
                    unfocusedLabelColor = TextDarkSecondary,
                    cursorColor = MutedTeal,
                    focusedTextColor = OffWhite,
                    unfocusedTextColor = OffWhite
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name) },
                enabled = name.isNotBlank() && name != currentName
            ) {
                Text("Save", color = if (name.isNotBlank() && name != currentName) MutedTeal else TextDarkSecondary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextDarkSecondary)
            }
        }
    )
}
