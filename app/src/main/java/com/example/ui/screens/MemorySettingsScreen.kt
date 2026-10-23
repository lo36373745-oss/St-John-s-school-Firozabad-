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
fun MemorySettingsScreen(viewModel: NexoraViewModel) {
    val memories by viewModel.allMemories.collectAsState()
    var newMemoryText by remember { mutableStateOf("") }
    var memoryCategory by remember { mutableStateOf("Preference") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Personal Memory & Settings", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NexoraTextPrimary)
                        Text(text = "Manage your persistent context & app preferences", fontSize = 11.sp, color = NexoraTextSecondary)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Explanation Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NexoraDarkSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Memory, contentDescription = null, tint = NexoraPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "AI Personal Memory", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NexoraTextPrimary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nexora AI can retain important context and preferences across your conversations. Your memories are securely stored locally on your device.",
                            fontSize = 13.sp,
                            color = NexoraTextSecondary
                        )
                    }
                }
            }

            // Add Memory
            item {
                Text(text = "Add New Memory", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newMemoryText,
                    onValueChange = { newMemoryText = it },
                    placeholder = { Text("e.g. I am a Class 10 student preparing for board exams.") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NexoraPrimary,
                        unfocusedBorderColor = NexoraBorder,
                        focusedTextColor = NexoraTextPrimary,
                        unfocusedTextColor = NexoraTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (newMemoryText.isNotBlank()) {
                            viewModel.addMemory(newMemoryText, memoryCategory)
                            newMemoryText = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NexoraPrimary),
                    enabled = newMemoryText.isNotBlank()
                ) {
                    Text("Save Memory")
                }
            }

            // Saved Memories List
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Saved Memories (${memories.size})", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                if (memories.isEmpty()) {
                    Text(text = "No saved memories yet.", color = NexoraTextSecondary, fontSize = 13.sp)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        memories.forEach { memory ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = NexoraDarkSurface,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Bookmark, contentDescription = null, tint = NexoraPrimary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = memory.content, color = NexoraTextPrimary, fontSize = 14.sp)
                                        Text(text = "Category: ${memory.category}", color = NexoraTextSecondary, fontSize = 11.sp)
                                    }
                                    IconButton(onClick = { viewModel.deleteMemory(memory.memoryId) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NexoraTextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
