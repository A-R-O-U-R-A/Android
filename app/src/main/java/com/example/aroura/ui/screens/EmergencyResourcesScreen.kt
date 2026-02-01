package com.example.aroura.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.components.AdvancedAuroraBackground
import com.example.aroura.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyResourcesScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AdvancedAuroraBackground()
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Emergency Resources", color = OffWhite) },
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
                modifier = Modifier.padding(padding).padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Services", style = MaterialTheme.typography.titleMedium, color = MutedTeal)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                item {
                    ResourceCard("Police", "100", Icons.Default.Phone)
                }
                item {
                    ResourceCard("Ambulance", "102", Icons.Default.Phone)
                }
                item {
                    ResourceCard("Fire", "101", Icons.Default.Phone)
                }
                
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Nearby Hospitals", style = MaterialTheme.typography.titleMedium, color = MutedTeal)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                item {
                    ResourceCard("City General Hospital", "2.4 km away", Icons.Default.LocationOn)
                }
                item {
                    ResourceCard("Apollo Medical Center", "5.1 km away", Icons.Default.LocationOn)
                }
            }
        }
    }
}

@Composable
fun ResourceCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DeepSurface.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().height(72.dp).clickable { }
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = OffWhite)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, color = OffWhite, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextDarkSecondary)
            }
        }
    }
}
