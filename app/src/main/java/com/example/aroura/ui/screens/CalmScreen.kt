package com.example.aroura.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.theme.*

// Data Model
data class CalmMediaItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String, // "Devotional", "AudioBook", "Relaxation"
    val startColor: Color,
    val endColor: Color
)

// Mock Data
val devotionalItems = listOf(
    CalmMediaItem("1", "Krishna Bhajan", "Divine Flute", "Devotional", Color(0xFF1E88E5), Color(0xFF5E35B1)),
    CalmMediaItem("2", "Mantras & Chants", "Om Chanting", "Devotional", Color(0xFFFFB300), Color(0xFFFF6F00)),
    CalmMediaItem("3", "Islamic Nasheeds", "Peaceful Sufi", "Devotional", Color(0xFF00897B), Color(0xFF004D40))
)

val audioBookItems = listOf(
    CalmMediaItem("4", "Mahabharata", "Epic Saga", "AudioBook", Color(0xFFD84315), Color(0xFF4E342E)),
    CalmMediaItem("5", "Bhagavad Gita", "Sacred Song", "AudioBook", Color(0xFFFFD54F), Color(0xFFFF6F00)),
    CalmMediaItem("6", "Holy Quran", "Recitation", "AudioBook", Color(0xFF4DB6AC), Color(0xFF00695C))
)

val relaxationItems = listOf(
    CalmMediaItem("7", "Nature Sounds", "Forest Rain", "Relaxation", Color(0xFF66BB6A), Color(0xFF1B5E20)),
    CalmMediaItem("8", "Calm Music", "Deep Sleep", "Relaxation", Color(0xFF7E57C2), Color(0xFF311B92))
)

@Composable
fun CalmScreen(onItemClick: (CalmMediaItem) -> Unit, onProfileClick: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp) // Bottom padding for nav bar
    ) {
        // Header
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = onProfileClick,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                         Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(DeepSurface, CircleShape)
                                .border(1.dp, MutedTeal.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, "Profile", tint = OffWhite, modifier = Modifier.size(16.dp))
                        }
                    }

                    Text(
                        text = "Calm",
                        style = MaterialTheme.typography.headlineMedium,
                        color = OffWhite,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    IconButton(
                        onClick = { /* Search */ },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = OffWhite)
                    }
                }
                Text(
                    text = "Let your heart quiet down.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDarkSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Devotional Section
        item {
            SectionHeader("Devotional Songs")
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(devotionalItems) { item ->
                    SquareMediaCard(item, onItemClick)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Audio Books Section
        item {
            SectionHeader("Audio Books")
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(audioBookItems) { item ->
                    SquareMediaCard(item, onItemClick)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Relaxation Section
        item {
            SectionHeader("Relaxation Sounds")
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(relaxationItems) { item ->
            WideMediaCard(item, onItemClick)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = OffWhite,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "View All >",
            style = MaterialTheme.typography.labelMedium,
            color = TextDarkSecondary
        )
    }
}

@Composable
fun SquareMediaCard(item: CalmMediaItem, onClick: (CalmMediaItem) -> Unit) {
    Box(
        modifier = Modifier
            .size(160.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(item.startColor.copy(alpha = 0.6f), item.endColor.copy(alpha = 0.8f))
                )
            )
            .clickable { onClick(item) }
    ) {
        // Decorative Shine
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = (-20).dp)
                .size(80.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        )
        
        // Play Button Visual
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .size(32.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = OffWhite,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = item.title,
            style = MaterialTheme.typography.titleSmall,
            color = OffWhite,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
                .padding(end = 36.dp), // Avoid play button
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun WideMediaCard(item: CalmMediaItem, onClick: (CalmMediaItem) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(item.startColor.copy(alpha = 0.4f), item.endColor.copy(alpha = 0.6f))
                )
            )
            .clickable { onClick(item) }
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = OffWhite,
                modifier = Modifier.weight(1f)
            )
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = OffWhite
                )
            }
        }
    }
}