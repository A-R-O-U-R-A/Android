package com.example.aroura.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.theme.*

@Composable
fun CalmFilterScreen(
    onClose: () -> Unit,
    onApply: (String, String) -> Unit // religion, language
) {
    var selectedReligion by remember { mutableStateOf("All") }
    var selectedLanguage by remember { mutableStateOf("English") }

    val religions = listOf("All", "Hindu", "Islamic", "Christian", "Sikh", "Buddhist")
    val languages = listOf("English", "Hindi", "Sanskrit", "Arabic", "Punjabi")

    // Overlay background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Bottom Sheet Content
        Surface(
            color = DeepSurface,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false) {} // Catch clicks
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filter Content", style = MaterialTheme.typography.headlineSmall, color = OffWhite)
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, null, tint = TextDarkSecondary)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                // Religion Filter
                Text("Religion / Spirituality", style = MaterialTheme.typography.titleMedium, color = MutedTeal)
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(religions) { religion ->
                        FilterChip(
                            label = religion,
                            selected = religion == selectedReligion,
                            onClick = { selectedReligion = religion }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Language Filter
                Text("Language", style = MaterialTheme.typography.titleMedium, color = MutedTeal)
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(languages) { language ->
                        FilterChip(
                            label = language,
                            selected = language == selectedLanguage,
                            onClick = { selectedLanguage = language }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Apply Button
                Button(
                    onClick = { onApply(selectedReligion, selectedLanguage) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MutedTeal, contentColor = MidnightCharcoal),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Apply Filters", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (selected) MutedTeal else Color.Transparent,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, TextDarkSecondary),
        shape = RoundedCornerShape(20.dp),
        onClick = onClick
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            color = if (selected) MidnightCharcoal else TextDarkSecondary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
