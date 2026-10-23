package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.NcertBook
import com.example.viewmodel.NexoraViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyCentreScreen(viewModel: NexoraViewModel) {
    var selectedClass by remember { mutableStateOf(10) }
    var selectedBook by remember { mutableStateOf<NcertBook?>(null) }
    var selectedChapter by remember { mutableStateOf<String?>(null) }
    var aiTutorResponse by remember { mutableStateOf<String?>(null) }
    var isLoadingTutor by remember { mutableStateOf(false) }

    // Advanced Study Planner state
    var examDateInput by remember { mutableStateOf("Next Month") }
    var dailyHoursInput by remember { mutableStateOf("2 Hours") }
    var aiPlanOutput by remember { mutableStateOf<String?>(null) }
    var isGeneratingPlan by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val books = viewModel.ncertBooks.filter { it.classLevel == selectedClass }
    val studyTasks by viewModel.allStudyTasks.collectAsState()

    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskSubject by remember { mutableStateOf("Mathematics") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "NCERT Study Centre & Advanced Planner", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NexoraTextPrimary)
                        Text(text = "Classes 1 to 12 • Official Curriculum & AI Coach", fontSize = 11.sp, color = NexoraTextSecondary)
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
            // Class selector
            item {
                Text(text = "Select Class", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items((1..12).toList()) { classNum ->
                        FilterChip(
                            selected = selectedClass == classNum,
                            onClick = {
                                selectedClass = classNum
                                selectedBook = null
                                selectedChapter = null
                                aiTutorResponse = null
                            },
                            label = { Text("Class $classNum") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NexoraPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = NexoraDarkSurface,
                                labelColor = NexoraTextSecondary
                            )
                        )
                    }
                }
            }

            // Books for selected class
            item {
                Text(text = "Textbooks & Subjects", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                if (books.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = NexoraDarkSurface,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "No textbooks indexed for Class $selectedClass yet. AI can still tutor you on any topic!", color = NexoraTextSecondary, fontSize = 13.sp)
                        }
                    }
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(books) { book ->
                            Card(
                                onClick = {
                                    selectedBook = book
                                    selectedChapter = null
                                    aiTutorResponse = null
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedBook == book) NexoraPrimary.copy(alpha = 0.2f) else NexoraDarkSurface
                                ),
                                border = if (selectedBook == book) androidx.compose.foundation.BorderStroke(1.dp, NexoraPrimary) else null,
                                modifier = Modifier.width(200.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Icon(Icons.Default.Book, contentDescription = null, tint = NexoraPrimary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = book.title, fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Language: ${book.language}", fontSize = 11.sp, color = NexoraTextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            // Chapters for selected book
            if (selectedBook != null) {
                item {
                    Text(text = "Chapters in ${selectedBook!!.title}", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        selectedBook!!.chapters.forEach { chapter ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedChapter = chapter
                                        isLoadingTutor = true
                                        scope.launch {
                                            val ai = com.example.ai.GeminiAiService()
                                            aiTutorResponse = ai.generateResponse("Provide a comprehensive NCERT summary and key formulas/concepts for Class $selectedClass ${selectedBook!!.subject}, Chapter: $chapter.")
                                            isLoadingTutor = false
                                        }
                                    },
                                color = if (selectedChapter == chapter) NexoraPrimary.copy(alpha = 0.3f) else NexoraDarkSurface,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = NexoraPrimary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(text = chapter, color = NexoraTextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = NexoraTextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            // AI Tutor Workspace for Selected Chapter
            if (selectedChapter != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NexoraDarkSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NexoraPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Nexora AI Tutor: $selectedChapter", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            if (isLoadingTutor) {
                                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = NexoraPrimary)
                                }
                            } else {
                                Text(text = aiTutorResponse ?: "Select a chapter to start AI tutoring.", color = NexoraTextPrimary, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            isLoadingTutor = true
                                            scope.launch {
                                                val ai = com.example.ai.GeminiAiService()
                                                aiTutorResponse = ai.generateResponse("Generate 3 practice questions with detailed solutions for Class $selectedClass ${selectedBook?.subject}, Chapter: $selectedChapter.")
                                                isLoadingTutor = false
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NexoraPrimary)
                                    ) {
                                        Text("Generate Practice Quiz", fontSize = 12.sp)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            isLoadingTutor = true
                                            scope.launch {
                                                val ai = com.example.ai.GeminiAiService()
                                                aiTutorResponse = ai.generateResponse("Create concise revision notes and formulas for Class $selectedClass ${selectedBook?.subject}, Chapter: $selectedChapter.")
                                                isLoadingTutor = false
                                            }
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NexoraPrimary)
                                    ) {
                                        Text("Revision Notes", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Advanced AI Study Planner Generator
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = NexoraDarkSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = NexoraPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Advanced AI Study Planner Generator", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Generate a customized, milestone-based preparation schedule powered by Gemini.", color = NexoraTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = examDateInput,
                                onValueChange = { examDateInput = it },
                                label = { Text("Exam Date / Timeline") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NexoraPrimary,
                                    unfocusedBorderColor = NexoraBorder,
                                    focusedTextColor = NexoraTextPrimary,
                                    unfocusedTextColor = NexoraTextPrimary
                                )
                            )
                            OutlinedTextField(
                                value = dailyHoursInput,
                                onValueChange = { dailyHoursInput = it },
                                label = { Text("Daily Study Hours") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NexoraPrimary,
                                    unfocusedBorderColor = NexoraBorder,
                                    focusedTextColor = NexoraTextPrimary,
                                    unfocusedTextColor = NexoraTextPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                isGeneratingPlan = true
                                viewModel.generateAiStudyPlan(
                                    classLevel = selectedClass.toString(),
                                    subject = selectedBook?.subject ?: "Mathematics",
                                    examDate = examDateInput,
                                    dailyHours = dailyHoursInput
                                ) { plan ->
                                    aiPlanOutput = plan
                                    isGeneratingPlan = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = NexoraPrimary),
                            enabled = !isGeneratingPlan
                        ) {
                            if (isGeneratingPlan) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generating AI Study Plan...")
                            } else {
                                Text("Generate Personalized AI Study Schedule")
                            }
                        }

                        if (aiPlanOutput != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = NexoraDarkBackground,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = "Your AI Study Schedule:", fontWeight = FontWeight.Bold, color = NexoraPrimary, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = aiPlanOutput!!, color = NexoraTextPrimary, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Study Planner & Tasks Checklist
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "My Study Task Checklist", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        placeholder = { Text("Task / Chapter to study...") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NexoraPrimary,
                            unfocusedBorderColor = NexoraBorder,
                            focusedTextColor = NexoraTextPrimary,
                            unfocusedTextColor = NexoraTextPrimary
                        )
                    )
                    Button(
                        onClick = {
                            if (newTaskTitle.isNotBlank()) {
                                viewModel.addStudyTask(newTaskTitle, selectedBook?.subject ?: "General", "Class $selectedClass", "Upcoming")
                                newTaskTitle = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NexoraPrimary)
                    ) {
                        Text("Add")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                studyTasks.forEach { task ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        color = NexoraDarkSurface,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { viewModel.updateTaskCompletion(task.taskId, it) },
                                colors = CheckboxDefaults.colors(checkedColor = NexoraPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    color = NexoraTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${task.classLevel} • ${task.subject}",
                                    color = NexoraTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            IconButton(onClick = { viewModel.deleteStudyTask(task.taskId) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NexoraTextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}
