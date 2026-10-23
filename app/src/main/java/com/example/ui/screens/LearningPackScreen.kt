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
fun LearningPackScreen(viewModel: NexoraViewModel) {
    val context = LocalContext.current
    var classLevel by remember { mutableStateOf("10") }
    var subject by remember { mutableStateOf("Science") }
    var chapter by remember { mutableStateOf("Chemical Reactions and Equations") }

    var packResult by remember { mutableStateOf<String?>(null) }
    var isGenerating by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "NCERT Learning Pack", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NexoraTextPrimary)
                        Text(text = "Instant presentation, video lesson, notes, flashcards & quiz", fontSize = 11.sp, color = NexoraTextSecondary)
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
                            Icon(Icons.Default.School, contentDescription = null, tint = NexoraPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Generate Complete Learning Pack", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                        }

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

                        OutlinedTextField(
                            value = chapter,
                            onValueChange = { chapter = it },
                            label = { Text("Chapter Title") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NexoraPrimary, unfocusedBorderColor = NexoraBorder, focusedTextColor = NexoraTextPrimary, unfocusedTextColor = NexoraTextPrimary)
                        )

                        Button(
                            onClick = {
                                if (chapter.isNotBlank()) {
                                    isGenerating = true
                                    viewModel.generateLearningPack(
                                        classLevel = classLevel,
                                        subject = subject,
                                        chapter = chapter
                                    ) { result ->
                                        packResult = result
                                        isGenerating = false
                                        Toast.makeText(context, "Learning Pack generated and saved to My Creations!", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = NexoraPrimary),
                            enabled = !isGenerating && chapter.isNotBlank()
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Compiling Complete Learning Pack...")
                            } else {
                                Text("Generate Learning Pack (Notes, Quiz, Flashcards)")
                            }
                        }
                    }
                }
            }

            if (packResult != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NexoraDarkSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NexoraSuccess)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "NCERT Learning Pack Bundle", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = packResult!!, color = NexoraTextPrimary, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
