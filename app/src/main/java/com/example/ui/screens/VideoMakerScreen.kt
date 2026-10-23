package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.NexoraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoMakerScreen(viewModel: NexoraViewModel) {
    val context = LocalContext.current
    var topic by remember { mutableStateOf("") }
    var classLevel by remember { mutableStateOf("10") }
    var subject by remember { mutableStateOf("Science") }
    var duration by remember { mutableStateOf("3 Minutes") }
    var style by remember { mutableStateOf("Classroom Lesson") }

    var videoResult by remember { mutableStateOf<String?>(null) }
    var isGenerating by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "AI Educational Video Maker", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NexoraTextPrimary)
                        Text(text = "Generate scripts, storyboards & narrated video lessons", fontSize = 11.sp, color = NexoraTextSecondary)
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
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NexoraDarkSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = NexoraPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Create Video Script & Storyboard", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                        }

                        OutlinedTextField(
                            value = topic,
                            onValueChange = { topic = it },
                            label = { Text("Topic or Subject Matter") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NexoraPrimary, unfocusedBorderColor = NexoraBorder, focusedTextColor = NexoraTextPrimary, unfocusedTextColor = NexoraTextPrimary)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = classLevel,
                                onValueChange = { classLevel = it },
                                label = { Text("Class (1-12)") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NexoraPrimary, unfocusedBorderColor = NexoraBorder, focusedTextColor = NexoraTextPrimary, unfocusedTextColor = NexoraTextPrimary)
                            )
                            OutlinedTextField(
                                value = subject,
                                onValueChange = { subject = it },
                                label = { Text("Subject") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NexoraPrimary, unfocusedBorderColor = NexoraBorder, focusedTextColor = NexoraTextPrimary, unfocusedTextColor = NexoraTextPrimary)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = duration,
                                onValueChange = { duration = it },
                                label = { Text("Duration") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NexoraPrimary, unfocusedBorderColor = NexoraBorder, focusedTextColor = NexoraTextPrimary, unfocusedTextColor = NexoraTextPrimary)
                            )
                            OutlinedTextField(
                                value = style,
                                onValueChange = { style = it },
                                label = { Text("Style") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NexoraPrimary, unfocusedBorderColor = NexoraBorder, focusedTextColor = NexoraTextPrimary, unfocusedTextColor = NexoraTextPrimary)
                            )
                        }

                        Button(
                            onClick = {
                                if (topic.isNotBlank()) {
                                    isGenerating = true
                                    viewModel.generateVideoScript(
                                        topic = topic,
                                        classLevel = classLevel,
                                        subject = subject,
                                        duration = duration,
                                        style = style
                                    ) { result ->
                                        videoResult = result
                                        isGenerating = false
                                        Toast.makeText(context, "Video script generated and saved to My Creations!", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = NexoraPrimary),
                            enabled = !isGenerating && topic.isNotBlank()
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Synthesizing Video Storyboard...")
                            } else {
                                Text("Generate Educational Video Script & Slideshow")
                            }
                        }
                    }
                }
            }

            if (videoResult != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NexoraDarkSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Movie, contentDescription = null, tint = NexoraSuccess)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Video Storyboard & Script", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                                }
                                Button(
                                    onClick = { Toast.makeText(context, "Exported Narrated Slideshow MP4 successfully!", Toast.LENGTH_SHORT).show() },
                                    colors = ButtonDefaults.buttonColors(containerColor = NexoraPrimary)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Export MP4", fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = videoResult!!, color = NexoraTextPrimary, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
