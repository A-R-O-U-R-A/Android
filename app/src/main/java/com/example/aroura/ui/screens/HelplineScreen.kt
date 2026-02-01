package com.example.aroura.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aroura.ui.components.AdvancedAuroraBackground
import com.example.aroura.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelplineScreen(onBack: () -> Unit) {
    val helplines = listOf(
        Pair("Vandrevala Foundation", "1860-266-2345"),
        Pair("iCall (Tata Institute)", "022-25521111"),
        Pair("Kiran Mental Health", "1800-599-0019"),
        Pair("Samaritans Mumbai", "+91 84229 84528"),
        Pair("Sneha Foundation", "044-24640050")
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AdvancedAuroraBackground()
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Helplines", color = OffWhite) },
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
                items(helplines) { (name, number) ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DeepSurface.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().clickable { /* Call */ }
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(name, style = MaterialTheme.typography.titleMedium, color = OffWhite, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(number, style = MaterialTheme.typography.bodyMedium, color = MutedTeal)
                            }
                            Icon(Icons.Default.Phone, null, tint = MutedTeal)
                        }
                    }
                }
            }
        }
    }
}
