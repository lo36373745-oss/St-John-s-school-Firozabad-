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
fun WorkspaceScreen(viewModel: NexoraViewModel) {
    val workspaces by viewModel.allWorkspaces.collectAsState()
    var newWorkspaceName by remember { mutableStateOf("") }
    var newWorkspaceDesc by remember { mutableStateOf("") }
    var workspaceCategory by remember { mutableStateOf("General") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "AI Workspace & Projects", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NexoraTextPrimary)
                        Text(text = "Organize conversations, notes, files & creations into projects", fontSize = 11.sp, color = NexoraTextSecondary)
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
                            Icon(Icons.Default.FolderSpecial, contentDescription = null, tint = NexoraPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Create New Project Folder", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                        }

                        OutlinedTextField(
                            value = newWorkspaceName,
                            onValueChange = { newWorkspaceName = it },
                            label = { Text("Project Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NexoraPrimary, unfocusedBorderColor = NexoraBorder, focusedTextColor = NexoraTextPrimary, unfocusedTextColor = NexoraTextPrimary)
                        )

                        OutlinedTextField(
                            value = newWorkspaceDesc,
                            onValueChange = { newWorkspaceDesc = it },
                            label = { Text("Description / Goals") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NexoraPrimary, unfocusedBorderColor = NexoraBorder, focusedTextColor = NexoraTextPrimary, unfocusedTextColor = NexoraTextPrimary)
                        )

                        Button(
                            onClick = {
                                if (newWorkspaceName.isNotBlank()) {
                                    viewModel.addWorkspace(newWorkspaceName, newWorkspaceDesc, workspaceCategory)
                                    newWorkspaceName = ""
                                    newWorkspaceDesc = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = NexoraPrimary),
                            enabled = newWorkspaceName.isNotBlank()
                        ) {
                            Text("Create Project Workspace")
                        }
                    }
                }
            }

            item {
                Text(text = "My Project Workspaces (${workspaces.size})", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                if (workspaces.isEmpty()) {
                    Text(text = "No project workspaces created yet.", color = NexoraTextSecondary, fontSize = 13.sp)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        workspaces.forEach { ws ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = NexoraDarkSurface,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Folder, contentDescription = null, tint = NexoraPrimary, modifier = Modifier.size(28.dp))
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = ws.name, fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 15.sp)
                                        Text(text = ws.description, color = NexoraTextSecondary, fontSize = 12.sp)
                                    }
                                    IconButton(onClick = { viewModel.deleteWorkspace(ws.workspaceId) }) {
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
