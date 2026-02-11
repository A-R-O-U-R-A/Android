@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.example.aroura.ui.screens.mood

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

/**
 * Track Your Mood - Premium Journaling Experience
 * 
 * A gentle, emotionally safe self-reflection flow that users can return to daily.
 * This is not a form. This is a calm, personal, supportive experience.
 * 
 * Flow Structure:
 * 1. Entry Screen - "What's on your mind?"
 * 2. Mood Slider - Horizontal slider with friendly character
 * 3. Feelings Picker Modal - Emotion chips (Positive/Negative)
 * 4. Activities Picker Modal - Icon grid
 * 5. Photo Attachment (Optional)
 * 6. Save Entry
 */

// ═══════════════════════════════════════════════════════════════════════════════
// DATA MODELS
// ═══════════════════════════════════════════════════════════════════════════════

data class MoodEntry(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = "",
    val moodLevel: Float = 0.5f, // 0 = Unhappy, 0.5 = Normal, 1 = Happy
    val feelings: List<Feeling> = emptyList(),
    val activities: List<Activity> = emptyList(),
    val photoUri: String? = null
)

data class Feeling(
    val id: String,
    val label: String,
    val isPositive: Boolean
)

data class Activity(
    val id: String,
    val label: String,
    val emoji: String
)

// ═══════════════════════════════════════════════════════════════════════════════
// PREDEFINED DATA
// ═══════════════════════════════════════════════════════════════════════════════

val negativeFeelings = listOf(
    Feeling("1", "disappointed", false),
    Feeling("2", "nervous", false),
    Feeling("3", "lonely", false),
    Feeling("4", "tired", false),
    Feeling("5", "confused", false),
    Feeling("6", "anxious", false),
    Feeling("7", "sad", false),
    Feeling("8", "overwhelmed", false),
    Feeling("9", "frustrated", false),
    Feeling("10", "stressed", false),
    Feeling("11", "irritated", false),
    Feeling("12", "hopeless", false)
)

val positiveFeelings = listOf(
    Feeling("13", "calm", true),
    Feeling("14", "grateful", true),
    Feeling("15", "motivated", true),
    Feeling("16", "joyful", true),
    Feeling("17", "confident", true),
    Feeling("18", "peaceful", true),
    Feeling("19", "hopeful", true),
    Feeling("20", "content", true),
    Feeling("21", "excited", true),
    Feeling("22", "loved", true),
    Feeling("23", "relaxed", true),
    Feeling("24", "proud", true)
)

val allActivities = listOf(
    Activity("1", "Work", "💼"),
    Activity("2", "Study", "📚"),
    Activity("3", "Friends", "👥"),
    Activity("4", "Family", "👨‍👩‍👧"),
    Activity("5", "Music", "🎵"),
    Activity("6", "Movies", "🎬"),
    Activity("7", "Relaxation", "🧘"),
    Activity("8", "Walking", "🚶"),
    Activity("9", "Gym", "💪"),
    Activity("10", "Sleep", "😴"),
    Activity("11", "Reading", "📖"),
    Activity("12", "Internet", "🌐"),
    Activity("13", "Games", "🎮"),
    Activity("14", "Shopping", "🛍️"),
    Activity("15", "Pets", "🐾"),
    Activity("16", "Cooking", "🍳"),
    Activity("17", "Nature", "🌳"),
    Activity("18", "Art", "🎨"),
    Activity("19", "Travel", "✈️"),
    Activity("20", "Meditation", "🧠")
)

// ═══════════════════════════════════════════════════════════════════════════════
// MAIN FLOW SCREEN
// ═══════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodJournalFlowScreen(
    onClose: () -> Unit,
    onSaveComplete: () -> Unit = {}
) {
    // Entry state
    var entry by remember { mutableStateOf(MoodEntry()) }
    var hasMoodBeenSet by remember { mutableStateOf(false) }
    
    // Modal states
    var showFeelingsModal by remember { mutableStateOf(false) }
    var showActivitiesModal by remember { mutableStateOf(false) }
    
    // Animation state
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }
    
    // Current date/time
    val dateFormatter = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    val currentDate = remember { dateFormatter.format(Date()) }
    val currentTime = remember { timeFormatter.format(Date()) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Premium background
        MoodJournalBackground()
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Top bar
            MoodJournalTopBar(
                onClose = onClose,
                date = currentDate,
                time = currentTime
            )
            
            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // ═══════════════════════════════════════════════════════════════
                // SECTION 1: NOTE INPUT - "What's on your mind?"
                // ═══════════════════════════════════════════════════════════════
                
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600)) + slideInVertically(
                        initialOffsetY = { 20 },
                        animationSpec = tween(600, easing = EaseOutCubic)
                    )
                ) {
                    NoteInputSection(
                        note = entry.note,
                        onNoteChanged = { entry = entry.copy(note = it) }
                    )
                }
                
                Spacer(modifier = Modifier.height(28.dp))
                
                // ═══════════════════════════════════════════════════════════════
                // SECTION 2: MOOD SLIDER
                // ═══════════════════════════════════════════════════════════════
                
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600, delayMillis = 100)) + slideInVertically(
                        initialOffsetY = { 20 },
                        animationSpec = tween(600, delayMillis = 100, easing = EaseOutCubic)
                    )
                ) {
                    MoodSliderSection(
                        moodLevel = entry.moodLevel,
                        onMoodChanged = { 
                            entry = entry.copy(moodLevel = it)
                            hasMoodBeenSet = true
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(28.dp))
                
                // ═══════════════════════════════════════════════════════════════
                // SECTION 3: FEELINGS PICKER TRIGGER
                // ═══════════════════════════════════════════════════════════════
                
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600, delayMillis = 200)) + slideInVertically(
                        initialOffsetY = { 20 },
                        animationSpec = tween(600, delayMillis = 200, easing = EaseOutCubic)
                    )
                ) {
                    AddSectionCard(
                        title = "Pick words that match your feelings",
                        selectedItems = entry.feelings.map { it.label },
                        accentColor = CalmingLavender,
                        onClick = { showFeelingsModal = true }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // ═══════════════════════════════════════════════════════════════
                // SECTION 4: ACTIVITIES PICKER TRIGGER
                // ═══════════════════════════════════════════════════════════════
                
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600, delayMillis = 300)) + slideInVertically(
                        initialOffsetY = { 20 },
                        animationSpec = tween(600, delayMillis = 300, easing = EaseOutCubic)
                    )
                ) {
                    AddSectionCard(
                        title = "What have you been up to?",
                        selectedItems = entry.activities.map { "${it.emoji} ${it.label}" },
                        accentColor = MutedTeal,
                        onClick = { showActivitiesModal = true }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // ═══════════════════════════════════════════════════════════════
                // SECTION 5: PHOTO ATTACHMENT (OPTIONAL)
                // ═══════════════════════════════════════════════════════════════
                
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600, delayMillis = 400)) + slideInVertically(
                        initialOffsetY = { 20 },
                        animationSpec = tween(600, delayMillis = 400, easing = EaseOutCubic)
                    )
                ) {
                    PhotoAttachmentSection(
                        photoUri = entry.photoUri,
                        onPhotoSelected = { entry = entry.copy(photoUri = it) },
                        onPhotoRemoved = { entry = entry.copy(photoUri = null) }
                    )
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
            
            // ═══════════════════════════════════════════════════════════════════
            // SECTION 6: SAVE BUTTON
            // ═══════════════════════════════════════════════════════════════════
            
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600, delayMillis = 500)) + slideInVertically(
                    initialOffsetY = { 30 },
                    animationSpec = tween(600, delayMillis = 500, easing = EaseOutCubic)
                )
            ) {
                SaveEntrySection(
                    enabled = hasMoodBeenSet,
                    onSave = {
                        // Save the entry (would persist to database)
                        onSaveComplete()
                        onClose()
                    }
                )
            }
        }
        
        // ═══════════════════════════════════════════════════════════════════════
        // MODALS
        // ═══════════════════════════════════════════════════════════════════════
        
        // Feelings Modal
        if (showFeelingsModal) {
            FeelingsPickerModal(
                selectedFeelings = entry.feelings,
                onSelectionChanged = { entry = entry.copy(feelings = it) },
                onDismiss = { showFeelingsModal = false }
            )
        }
        
        // Activities Modal
        if (showActivitiesModal) {
            ActivitiesPickerModal(
                selectedActivities = entry.activities,
                onSelectionChanged = { entry = entry.copy(activities = it) },
                onDismiss = { showActivitiesModal = false }
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TOP BAR
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun MoodJournalTopBar(
    onClose: () -> Unit,
    date: String,
    time: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Close button
        MoodIconButton(
            icon = Icons.Default.Close,
            onClick = onClose
        )
        
        // Date & Time
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = date,
                style = MaterialTheme.typography.titleSmall,
                color = OffWhite,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = time,
                style = MaterialTheme.typography.labelSmall,
                color = TextDarkSecondary
            )
        }
        
        // Placeholder for balance
        Spacer(modifier = Modifier.size(44.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 1: NOTE INPUT
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun NoteInputSection(
    note: String,
    onNoteChanged: (String) -> Unit
) {
    Column {
        Text(
            text = "Your note",
            style = MaterialTheme.typography.titleMedium,
            color = OffWhite,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(DeepSurface.copy(alpha = 0.5f))
                .border(
                    1.dp,
                    Color.White.copy(alpha = 0.05f),
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            BasicTextField(
                value = note,
                onValueChange = onNoteChanged,
                textStyle = TextStyle(
                    color = OffWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    lineHeight = 26.sp
                ),
                cursorBrush = SolidColor(MutedTeal),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box {
                        if (note.isEmpty()) {
                            Text(
                                text = "What's on your mind?",
                                style = TextStyle(
                                    color = TextDarkTertiary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Light
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 2: MOOD SLIDER
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun MoodSliderSection(
    moodLevel: Float,
    onMoodChanged: (Float) -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    
    // Mood label based on level
    val moodLabel = when {
        moodLevel < 0.2f -> "Sad"
        moodLevel < 0.4f -> "Low"
        moodLevel < 0.6f -> "Normal"
        moodLevel < 0.8f -> "Content"
        else -> "Happy"
    }
    
    // Character expression based on mood
    val characterEmoji = when {
        moodLevel < 0.2f -> "😢"
        moodLevel < 0.4f -> "😔"
        moodLevel < 0.6f -> "😐"
        moodLevel < 0.8f -> "🙂"
        else -> "😊"
    }
    
    // Animated mood color
    val moodColor by animateColorAsState(
        targetValue = when {
            moodLevel < 0.3f -> GentleError.copy(alpha = 0.6f)
            moodLevel < 0.7f -> SoftBlue
            else -> CalmingGreen
        },
        animationSpec = tween(400, easing = EaseOutCubic),
        label = "moodColor"
    )
    
    // Animated scale for character
    val characterScale by animateFloatAsState(
        targetValue = 1f + (moodLevel * 0.2f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "characterScale"
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DeepSurface.copy(alpha = 0.6f),
                        DeepSurface.copy(alpha = 0.4f)
                    )
                )
            )
            .border(
                1.dp,
                moodColor.copy(alpha = 0.2f),
                RoundedCornerShape(24.dp)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Character with glow
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            // Subtle glow behind character
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .blur(20.dp)
                    .alpha(0.4f)
                    .background(moodColor, CircleShape)
            )
            
            Text(
                text = characterEmoji,
                fontSize = 48.sp,
                modifier = Modifier.scale(characterScale)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Mood label
        Text(
            text = moodLabel,
            style = MaterialTheme.typography.titleMedium,
            color = moodColor,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Slider labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Unhappy",
                style = MaterialTheme.typography.labelSmall,
                color = TextDarkTertiary
            )
            Text(
                text = "Happy",
                style = MaterialTheme.typography.labelSmall,
                color = TextDarkTertiary
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Custom mood slider
        MoodSlider(
            value = moodLevel,
            onValueChange = { 
                onMoodChanged(it)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            },
            accentColor = moodColor
        )
    }
}

@Composable
private fun MoodSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    accentColor: Color
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        colors = SliderDefaults.colors(
            thumbColor = accentColor,
            activeTrackColor = accentColor.copy(alpha = 0.8f),
            inactiveTrackColor = DeepSurface
        )
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 3 & 4: ADD SECTION CARD (Reusable trigger)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun AddSectionCard(
    title: String,
    selectedItems: List<String>,
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "cardScale"
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(DeepSurface.copy(alpha = 0.5f))
            .border(
                1.dp,
                if (selectedItems.isNotEmpty()) accentColor.copy(alpha = 0.3f)
                else Color.White.copy(alpha = 0.05f),
                RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = OffWhite,
                fontWeight = FontWeight.Light,
                modifier = Modifier.weight(1f)
            )
            
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        
        // Show selected items if any
        if (selectedItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedItems.take(5).forEach { item ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.labelMedium,
                            color = accentColor
                        )
                    }
                }
                
                if (selectedItems.size > 5) {
                    Text(
                        text = "+${selectedItems.size - 5} more",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextDarkSecondary
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 5: PHOTO ATTACHMENT
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun PhotoAttachmentSection(
    photoUri: String?,
    onPhotoSelected: (String) -> Unit,
    onPhotoRemoved: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "photoCardScale"
    )
    
    if (photoUri == null) {
        // Add photo button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .clip(RoundedCornerShape(20.dp))
                .background(DeepSurface.copy(alpha = 0.3f))
                .border(
                    1.dp,
                    Color.White.copy(alpha = 0.05f),
                    RoundedCornerShape(20.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        // Would open photo picker
                        // For demo, we'll use a placeholder
                        onPhotoSelected("placeholder_photo")
                    }
                )
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = TextDarkSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Add a photo from your day",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDarkSecondary,
                    fontWeight = FontWeight.Light
                )
            }
            
            Text(
                text = "optional",
                style = MaterialTheme.typography.labelSmall,
                color = TextDarkTertiary
            )
        }
    } else {
        // Photo preview card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            SoftBlue.copy(alpha = 0.1f),
                            CalmingLavender.copy(alpha = 0.1f)
                        )
                    )
                )
                .border(
                    1.dp,
                    SoftBlue.copy(alpha = 0.2f),
                    RoundedCornerShape(20.dp)
                )
        ) {
            // Placeholder for photo preview
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo placeholder
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DeepSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = TextDarkSecondary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Photo added",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OffWhite
                    )
                    Text(
                        text = "Tap to change",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextDarkSecondary
                    )
                }
                
                // Remove button
                MoodIconButton(
                    icon = Icons.Default.Close,
                    onClick = onPhotoRemoved
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 6: SAVE BUTTON
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SaveEntrySection(
    enabled: Boolean,
    onSave: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "saveBtnScale"
    )
    
    val buttonAlpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.5f,
        animationSpec = tween(300),
        label = "saveBtnAlpha"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MidnightCharcoal.copy(alpha = 0.9f)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Button(
            onClick = onSave,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .scale(scale)
                .alpha(buttonAlpha),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MutedTeal,
                disabledContainerColor = MutedTeal.copy(alpha = 0.3f)
            ),
            interactionSource = interactionSource
        ) {
            Text(
                text = "Save Entry",
                style = MaterialTheme.typography.titleMedium,
                color = if (enabled) MidnightCharcoal else TextDarkTertiary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// FEELINGS PICKER MODAL
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun FeelingsPickerModal(
    selectedFeelings: List<Feeling>,
    onSelectionChanged: (List<Feeling>) -> Unit,
    onDismiss: () -> Unit
) {
    var showPositive by remember { mutableStateOf(true) }
    var localSelection by remember { mutableStateOf(selectedFeelings.toMutableList()) }
    
    ModalOverlay(
        title = "How do you feel?",
        onDismiss = onDismiss,
        onSave = {
            onSelectionChanged(localSelection)
            onDismiss()
        }
    ) {
        // Toggle tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DeepSurface)
                .padding(4.dp)
        ) {
            FeelingTabButton(
                text = "Positive",
                isSelected = showPositive,
                accentColor = CalmingGreen,
                onClick = { showPositive = true },
                modifier = Modifier.weight(1f)
            )
            
            FeelingTabButton(
                text = "Negative",
                isSelected = !showPositive,
                accentColor = GentleError.copy(alpha = 0.8f),
                onClick = { showPositive = false },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Feelings chips
        val feelings = if (showPositive) positiveFeelings else negativeFeelings
        
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            feelings.forEach { feeling ->
                val isSelected = localSelection.any { it.id == feeling.id }
                
                FeelingChip(
                    text = feeling.label,
                    isSelected = isSelected,
                    accentColor = if (showPositive) CalmingGreen else CalmingLavender,
                    onClick = {
                        localSelection = if (isSelected) {
                            localSelection.filter { it.id != feeling.id }.toMutableList()
                        } else {
                            (localSelection + feeling).toMutableList()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FeelingTabButton(
    text: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) accentColor.copy(alpha = 0.2f) else Color.Transparent,
        animationSpec = tween(200),
        label = "tabBg"
    )
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) accentColor else TextDarkSecondary,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
private fun FeelingChip(
    text: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "chipScale"
    )
    
    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) accentColor.copy(alpha = 0.2f)
                else DeepSurface.copy(alpha = 0.6f)
            )
            .border(
                1.dp,
                if (isSelected) accentColor.copy(alpha = 0.4f)
                else Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) accentColor else TextDarkSecondary,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Light
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// ACTIVITIES PICKER MODAL
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ActivitiesPickerModal(
    selectedActivities: List<Activity>,
    onSelectionChanged: (List<Activity>) -> Unit,
    onDismiss: () -> Unit
) {
    var localSelection by remember { mutableStateOf(selectedActivities.toMutableList()) }
    
    ModalOverlay(
        title = "What have you been up to?",
        onDismiss = onDismiss,
        onSave = {
            onSelectionChanged(localSelection)
            onDismiss()
        }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(320.dp)
        ) {
            items(allActivities) { activity ->
                val isSelected = localSelection.any { it.id == activity.id }
                
                ActivityButton(
                    emoji = activity.emoji,
                    label = activity.label,
                    isSelected = isSelected,
                    onClick = {
                        localSelection = if (isSelected) {
                            localSelection.filter { it.id != activity.id }.toMutableList()
                        } else {
                            (localSelection + activity).toMutableList()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ActivityButton(
    emoji: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "activityScale"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MutedTeal.copy(alpha = 0.2f) else DeepSurface.copy(alpha = 0.6f),
        animationSpec = tween(200),
        label = "activityBg"
    )
    
    Column(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                1.dp,
                if (isSelected) MutedTeal.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.05f),
                RoundedCornerShape(16.dp)
            )
            .clickable {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MutedTeal else TextDarkSecondary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// MODAL OVERLAY
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ModalOverlay(
    title: String,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(300)
            ) + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(MidnightCharcoal)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // Prevent clicks from dismissing
                    )
                    .padding(24.dp)
            ) {
                // Handle bar
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(TextDarkTertiary)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = OffWhite,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Content
                content()
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Save button
                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MutedTeal
                    )
                ) {
                    Text(
                        text = "Done",
                        style = MaterialTheme.typography.titleMedium,
                        color = MidnightCharcoal,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// REUSABLE COMPONENTS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun MoodJournalBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "moodBg")
    
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(45000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bgGradient"
    )
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bgGlow"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightCharcoal)
    ) {
        // Subtle gradient layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0E1116),
                            Color(0xFF0A0D11),
                            Color(0xFF0C0F14)
                        ),
                        startY = gradientOffset * 150
                    )
                )
        )
        
        // Soft glow orbs
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-80).dp, y = 100.dp)
                .size(280.dp)
                .blur(70.dp)
                .alpha(glowAlpha)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SoftBlue.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
        
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 100.dp, y = 50.dp)
                .size(220.dp)
                .blur(60.dp)
                .alpha(glowAlpha * 0.7f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            CalmingLavender.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 80.dp)
                .size(300.dp)
                .blur(80.dp)
                .alpha(glowAlpha * 0.6f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MutedTeal.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
    }
}

@Composable
private fun MoodIconButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "iconBtnScale"
    )
    
    Box(
        modifier = Modifier
            .size(44.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(DeepSurface.copy(alpha = 0.5f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = TextDarkSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}
