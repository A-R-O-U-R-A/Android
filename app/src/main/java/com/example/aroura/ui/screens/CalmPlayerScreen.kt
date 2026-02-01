package com.example.aroura.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.components.AdvancedAuroraBackground
import com.example.aroura.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalmPlayerScreen(item: CalmMediaItem, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AdvancedAuroraBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = OffWhite)
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) { Icon(Icons.Default.Share, "Share", tint = OffWhite) }
                        IconButton(onClick = {}) { Icon(Icons.Default.Notifications, "Alert", tint = OffWhite) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Album Art
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .shadow(32.dp, RoundedCornerShape(32.dp))
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(item.startColor, item.endColor)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Decorative Inner Circle
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    )
                    Text(
                        text = "ॐ", // Placeholder symbol
                        fontSize = 80.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Title & Subtitle
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = OffWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.subtitle, // e.g. "By A.R.O.U.R.A"
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDarkSecondary
                )

                Spacer(modifier = Modifier.weight(1f))

                // Progress Bar
                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = 0.3f,
                        onValueChange = {},
                        colors = SliderDefaults.colors(
                            thumbColor = OffWhite,
                            activeTrackColor = MutedTeal,
                            inactiveTrackColor = DeepSurface
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1:05", style = MaterialTheme.typography.labelSmall, color = TextDarkSecondary)
                        Text("-4:32", style = MaterialTheme.typography.labelSmall, color = TextDarkSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Using ArrowBack as SkipPrevious mock
                    IconButton(onClick = {}, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = OffWhite, modifier = Modifier.size(32.dp))
                    }
                    
                    // Play/Pause Button
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        // Reusing PlayArrow for visual state (mock)
                        Icon(Icons.Default.PlayArrow, null, tint = OffWhite, modifier = Modifier.size(32.dp))
                    }

                    // Using ArrowForward as SkipNext mock
                    IconButton(onClick = {}, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = OffWhite, modifier = Modifier.size(32.dp))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Volume (Optional visual)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, null, tint = TextDarkSecondary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Slider(
                        value = 0.7f,
                        onValueChange = {},
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Transparent,
                            activeTrackColor = TextDarkSecondary.copy(alpha = 0.5f),
                            inactiveTrackColor = DeepSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
