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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolAssistantScreen(viewModel: NexoraViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var assistantAnswer by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "St. John's School Assistant", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NexoraTextPrimary)
                        Text(text = "Firozabad • Official Portal & AI Assistant", fontSize = 11.sp, color = NexoraTextSecondary)
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
            // Header Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NexoraDarkSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, contentDescription = null, tint = NexoraPrimary, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "St. John's School, Firozabad", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NexoraTextPrimary)
                                Text(text = "stjohnsfirozabad.in", fontSize = 12.sp, color = NexoraPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Ask anything about admissions, academic calendar, notices, fee structures, faculty, or school facilities.",
                            fontSize = 13.sp,
                            color = NexoraTextSecondary
                        )
                    }
                }
            }

            // Quick Info Topics
            item {
                Text(text = "Quick School Information", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    viewModel.schoolInfo.forEach { (title, desc) ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = NexoraDarkSurface,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = title, fontWeight = FontWeight.Bold, color = NexoraPrimary, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = desc, color = NexoraTextPrimary, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Ask AI Assistant about the school
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Ask School AI Assistant", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("e.g. What are the admission criteria for class 11?") },
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
                        if (searchQuery.isNotBlank()) {
                            isLoading = true
                            scope.launch {
                                val ai = com.example.ai.GeminiAiService()
                                val contextInfo = viewModel.schoolInfo.entries.joinToString("\n") { "${it.key}: ${it.value}" }
                                assistantAnswer = ai.generateResponse("You are the official AI Assistant for St. John's School, Firozabad (stjohnsfirozabad.in). Using the school context below, answer the user's question accurately and professionally.\nSchool Context:\n$contextInfo\nUser Question: $searchQuery")
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NexoraPrimary),
                    enabled = !isLoading && searchQuery.isNotBlank()
                ) {
                    Text("Ask AI Assistant")
                }
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NexoraPrimary)
                    }
                }
            } else if (assistantAnswer != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NexoraDarkSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NexoraPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Assistant Response", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 15.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = assistantAnswer!!, color = NexoraTextPrimary, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
