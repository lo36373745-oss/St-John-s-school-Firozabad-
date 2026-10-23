package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun ToolsScreen(viewModel: NexoraViewModel) {
    var selectedTool by remember { mutableStateOf("Writing Assistant") }
    var inputQuery by remember { mutableStateOf("") }
    var outputResult by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val toolsList = listOf(
        Triple("Writing Assistant", "Essays, emails, proofreading & summaries", Icons.Default.Edit),
        Triple("Code Assistant", "Debug, explain & generate code snippets", Icons.Default.Code),
        Triple("Math Solver", "Step-by-step mathematical problem solving", Icons.Default.Calculate),
        Triple("Brainstorming", "Creative ideas, project planning & research", Icons.Default.Lightbulb)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Nexora Productivity Tools", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NexoraTextPrimary)
                        Text(text = "Writing, Coding, Mathematics & Reasoning", fontSize = 11.sp, color = NexoraTextSecondary)
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
            // Tools selector cards
            item {
                Text(text = "Select Tool", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    toolsList.forEach { (name, desc, icon) ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTool = name },
                            color = if (selectedTool == name) NexoraPrimary.copy(alpha = 0.2f) else NexoraDarkSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = if (selectedTool == name) androidx.compose.foundation.BorderStroke(1.dp, NexoraPrimary) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(icon, contentDescription = null, tint = NexoraPrimary, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = name, fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 15.sp)
                                    Text(text = desc, color = NexoraTextSecondary, fontSize = 12.sp)
                                }
                                if (selectedTool == name) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NexoraPrimary)
                                }
                            }
                        }
                    }
                }
            }

            // Input for selected tool
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Input for $selectedTool", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = inputQuery,
                    onValueChange = { inputQuery = it },
                    placeholder = {
                        Text(
                            when (selectedTool) {
                                "Writing Assistant" -> "Enter topic, email draft, or text to proofread..."
                                "Code Assistant" -> "Paste code snippet to debug or describe feature..."
                                "Math Solver" -> "Enter mathematical equation or word problem..."
                                else -> "Enter topic to brainstorm or plan..."
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NexoraPrimary,
                        unfocusedBorderColor = NexoraBorder,
                        focusedTextColor = NexoraTextPrimary,
                        unfocusedTextColor = NexoraTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (inputQuery.isNotBlank()) {
                            isLoading = true
                            scope.launch {
                                val ai = com.example.ai.GeminiAiService()
                                val systemPrompt = when (selectedTool) {
                                    "Writing Assistant" -> "You are an expert writing assistant. Help write, proofread, or summarize professionally."
                                    "Code Assistant" -> "You are an expert software engineer. Debug, explain, or write clean code."
                                    "Math Solver" -> "You are an expert math tutor. Provide step-by-step solutions."
                                    else -> "You are a creative brainstorming partner."
                                }
                                outputResult = ai.generateResponse(inputQuery, systemPrompt)
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NexoraPrimary),
                    enabled = !isLoading && inputQuery.isNotBlank()
                ) {
                    Text("Run $selectedTool")
                }
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NexoraPrimary)
                    }
                }
            } else if (outputResult != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NexoraDarkSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NexoraPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "$selectedTool Result", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 15.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = outputResult!!, color = NexoraTextPrimary, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
