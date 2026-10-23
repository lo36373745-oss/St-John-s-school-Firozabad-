package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.NexoraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCreationsScreen(viewModel: NexoraViewModel) {
    val creations by viewModel.allCreations.collectAsState()
    var selectedCreationDetails by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "My Creations", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NexoraTextPrimary)
                        Text(text = "Saved Presentations, Video Scripts & Learning Packs", fontSize = 11.sp, color = NexoraTextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NexoraDarkSurface)
            )
        },
        containerColor = NexoraDarkBackground
    ) { paddingVals ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (creations.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, tint = NexoraTextSecondary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "No saved creations yet.", color = NexoraTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Generate a presentation, video script, or learning pack to see it here.", color = NexoraTextSecondary, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
            } else {
                items(creations) { creation ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NexoraDarkSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Icon(
                                    imageVector = when (creation.type) {
                                        "Presentation" -> Icons.Default.Slideshow
                                        "Video" -> Icons.Default.Videocam
                                        else -> Icons.Default.School
                                    },
                                    contentDescription = null,
                                    tint = NexoraPrimary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = creation.title, fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 15.sp)
                                    Text(text = "Type: ${creation.type}", color = NexoraTextSecondary, fontSize = 11.sp)
                                }
                                IconButton(onClick = { viewModel.deleteCreation(creation.creationId) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NexoraTextSecondary)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = creation.details,
                                color = NexoraTextPrimary,
                                fontSize = 13.sp,
                                maxLines = 4
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = { selectedCreationDetails = creation.details }
                            ) {
                                Text("View Full Details", color = NexoraPrimary)
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedCreationDetails != null) {
        AlertDialog(
            onDismissRequest = { selectedCreationDetails = null },
            confirmButton = {
                TextButton(onClick = { selectedCreationDetails = null }) {
                    Text("Close", color = NexoraPrimary)
                }
            },
            title = { Text("Creation Details", color = NexoraTextPrimary) },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    item {
                        Text(text = selectedCreationDetails!!, color = NexoraTextPrimary, fontSize = 13.sp)
                    }
                }
            },
            containerColor = NexoraDarkSurface
        )
    }
}
