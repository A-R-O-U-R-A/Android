package com.example.aroura.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.aroura.ui.theme.*

/**
 * A.R.O.U.R.A Premium UI Components
 * Consistent, reusable, beautifully designed
 */

// ═══════════════════════════════════════════════════════════════════════════════
// PROFILE ICON - Consistent across all screens
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Unified Profile Icon Component
 * Size: 44dp (touch target compliant)
 * Style: Circular with subtle border glow
 * Supports profile picture URL or falls back to icon
 */
@Composable
fun ArouraProfileIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    profilePictureUrl: String? = null
) {
    val context = LocalContext.current
    
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        DeepSurface.copy(alpha = 0.9f),
                        ElevatedSurface.copy(alpha = 0.8f)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        MutedTeal.copy(alpha = 0.5f),
                        SoftBlue.copy(alpha = 0.3f)
                    )
                ),
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (profilePictureUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(profilePictureUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = OffWhite,
                modifier = Modifier.size(size * 0.5f)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SCREEN HEADER - Consistent header pattern
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Standard screen header with optional profile icon
 */
@Composable
fun ArouraHeader(
    title: String,
    subtitle: String? = null,
    onProfileClick: (() -> Unit)? = null,
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    centerTitle: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = ArouraSpacing.screenHorizontal.dp,
                vertical = ArouraSpacing.md.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (centerTitle) Arrangement.Center else Arrangement.SpaceBetween
    ) {
        // Left side: Back button or Title
        if (showBackButton && onBackClick != null) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = OffWhite
                )
            }
            
            Spacer(modifier = Modifier.width(ArouraSpacing.md.dp))
        }
        
        if (!centerTitle) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.Light
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDarkSecondary
                    )
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.Light
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDarkSecondary
                    )
                }
            }
        }
        
        // Right side: Profile icon
        if (onProfileClick != null) {
            ArouraProfileIcon(onClick = onProfileClick)
        } else if (showBackButton) {
            // Invisible spacer for symmetry
            Spacer(modifier = Modifier.size(44.dp))
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// PRIMARY BUTTON - Premium CTA button with glow effect
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun ArouraPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Simplified scale animation
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(100),
        label = "buttonScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .scale(scale)
    ) {
        // Removed infinite glow animation for performance
        
        // Button
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            enabled = enabled && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MutedTeal,
                contentColor = MidnightCharcoal,
                disabledContainerColor = MutedTeal.copy(alpha = 0.3f),
                disabledContentColor = MidnightCharcoal.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(30.dp),
            interactionSource = interactionSource,
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MidnightCharcoal,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SECONDARY BUTTON - Outlined style
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun ArouraSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
            brush = Brush.linearGradient(
                colors = listOf(
                    MutedTeal.copy(alpha = 0.5f),
                    SoftBlue.copy(alpha = 0.3f)
                )
            )
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = OffWhite,
            containerColor = Color.Transparent
        )
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// PREMIUM CARD - Glass morphism style card
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun ArouraCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            ),
        shape = RoundedCornerShape(ArouraSpacing.cardRadius.dp),
        color = DeepSurface.copy(alpha = 0.6f),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.08f),
                    Color.White.copy(alpha = 0.02f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(ArouraSpacing.cardPadding.dp),
            content = content
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION TITLE - Consistent section headers
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun ArouraSectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = OffWhite,
            fontWeight = FontWeight.SemiBold
        )
        
        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelMedium,
                color = MutedTeal,
                modifier = Modifier.clickable { onActionClick() }
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// PILL TAG - Category/status indicator
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun ArouraPill(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MutedTeal
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            letterSpacing = 1.5.sp
        )
    }
}

