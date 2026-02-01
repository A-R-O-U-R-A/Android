package com.example.aroura.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.theme.*

@Composable
fun SupportScreen(
    onProfileClick: () -> Unit,
    onNavigate: (String) -> Unit,
    onOpenPanic: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
    ) {
        // Header with Profile
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Support",
                    style = MaterialTheme.typography.headlineMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.Light
                )
                IconButton(onClick = onProfileClick) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(DeepSurface, CircleShape)
                            .border(1.dp, MutedTeal.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, "Profile", tint = OffWhite, modifier = Modifier.size(20.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Panic Guidance
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFFD84315).copy(alpha = 0.8f), Color(0xFFFF8A65).copy(alpha = 0.8f))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Crisis Guidance", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("1. Breathe in for 4 seconds.\n2. Hold for 7 seconds.\n3. Exhale for 8 seconds.", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onOpenPanic, // Opens Panic Overlay
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFFD84315)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Activate Panic Mode", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Helplines
        item {
            Text("Mental Health Helplines", style = MaterialTheme.typography.titleMedium, color = OffWhite)
            Spacer(modifier = Modifier.height(16.dp))
            SupportOptionCard("View All Helplines", "Verified Numbers", Icons.Default.Phone) {
                onNavigate("helplines")
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Psychiatrist
        item {
            Text("Professional Help", style = MaterialTheme.typography.titleMedium, color = OffWhite)
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Icons safe replacement
                CompactSupportCard("Chat", Icons.Default.Email, Modifier.weight(1f)) { onNavigate("psychiatrist") }
                CompactSupportCard("Call", Icons.Default.Call, Modifier.weight(1f)) { onNavigate("psychiatrist") }
                CompactSupportCard("Video", Icons.Default.Face, Modifier.weight(1f)) { onNavigate("psychiatrist") }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Trusted Contacts
        item {
            Text("Trusted Contacts", style = MaterialTheme.typography.titleMedium, color = OffWhite)
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(DeepSurface.copy(alpha = 0.5f))
                    .clickable { onNavigate("trusted") }
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, null, tint = MutedTeal)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Add / Manage Trusted Contacts", color = MutedTeal, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // Emergency Resources
        item {
             Text("Emergency Resources", style = MaterialTheme.typography.titleMedium, color = OffWhite)
            Spacer(modifier = Modifier.height(16.dp))
            SupportOptionCard("Hospitals & Police", "Near you", Icons.Default.LocationOn) {
                onNavigate("emergency")
            }
        }
    }
}

@Composable
fun SupportOptionCard(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        color = DeepSurface.copy(alpha = 0.6f),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().height(72.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = OffWhite)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = OffWhite, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = TextDarkSecondary, style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = TextDarkSecondary)
        }
    }
}

@Composable
fun CompactSupportCard(text: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        color = DeepSurface.copy(alpha = 0.6f),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.height(80.dp),
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = MutedTeal)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text, color = OffWhite, style = MaterialTheme.typography.labelMedium)
        }
    }
}
