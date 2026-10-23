package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.viewmodel.NexoraViewModel
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Chat : Screen("chat", "AI Chat", Icons.Outlined.Chat)
    object CameraVision : Screen("camera_vision", "Camera Vision", Icons.Outlined.PhotoCamera)
    object SchoolAssistant : Screen("school", "School Assistant", Icons.Outlined.Business)
    object StudyCentre : Screen("study", "NCERT Tutor", Icons.Outlined.MenuBook)
    object Presentation : Screen("presentation", "Presentation Maker", Icons.Outlined.Slideshow)
    object Video : Screen("video", "AI Video Studio", Icons.Outlined.Videocam)
    object LearningPack : Screen("learning_pack", "File Analysis", Icons.Outlined.Description)
    object Creations : Screen("creations", "My Creations", Icons.Outlined.FolderOpen)
    object Workspace : Screen("workspace", "Workspace", Icons.Outlined.FolderSpecial)
    object Memory : Screen("memory", "Memory", Icons.Outlined.Psychology)
    object Tasks : Screen("tasks", "Tasks & Reminders", Icons.Outlined.TaskAlt)
    object Settings : Screen("settings", "Settings", Icons.Outlined.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: NexoraViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Chat) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val chats by viewModel.allChats.collectAsState()
    val currentChatId by viewModel.currentChatId.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = NexoraDarkSurface,
                modifier = Modifier.width(320.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Logo Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(38.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = NexoraPrimary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "N", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Nexora AI", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NexoraTextPrimary)
                            Text(text = "Think • Create • Explore", fontSize = 11.sp, color = NexoraTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val navItems = listOf(
                        Screen.Chat,
                        Screen.CameraVision,
                        Screen.SchoolAssistant,
                        Screen.StudyCentre,
                        Screen.Presentation,
                        Screen.Video,
                        Screen.LearningPack,
                        Screen.Creations,
                        Screen.Workspace,
                        Screen.Memory,
                        Screen.Tasks,
                        Screen.Settings
                    )

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(navItems) { screen ->
                            val isSelected = currentScreen == screen
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentScreen = screen
                                        scope.launch { drawerState.close() }
                                    },
                                color = if (isSelected) NexoraPrimary.copy(alpha = 0.25f) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(screen.icon, contentDescription = null, tint = if (isSelected) NexoraPrimary else NexoraTextSecondary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = screen.title,
                                        color = if (isSelected) NexoraTextPrimary else NexoraTextSecondary,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = NexoraBorder)
                            Spacer(modifier = Modifier.height(12.dp))
                            // User Profile Card
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = NexoraDarkBackground,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorder)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = NexoraPrimary) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("LK", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(text = "Love Kush", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 13.sp)
                                            Text(text = "Free Plan • 0 / 50 msgs", fontSize = 10.sp, color = NexoraTextSecondary)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Made by Love Kush Rathore\n11c student of St John's School",
                                color = NexoraTextSecondary,
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = NexoraDarkSurface,
                    tonalElevation = 8.dp
                ) {
                    val bottomItems = listOf(Screen.Chat, Screen.CameraVision, Screen.StudyCentre, Screen.Presentation, Screen.Creations)
                    bottomItems.forEach { screen ->
                        val selected = currentScreen == screen
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title, fontSize = 9.sp) },
                            selected = selected,
                            onClick = { currentScreen = screen },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NexoraPrimary,
                                selectedTextColor = NexoraPrimary,
                                unselectedIconColor = NexoraTextSecondary,
                                unselectedTextColor = NexoraTextSecondary,
                                indicatorColor = NexoraPrimary.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            },
            containerColor = NexoraDarkBackground
        ) { paddingVals ->
            Box(modifier = Modifier.padding(paddingVals)) {
                when (currentScreen) {
                    is Screen.Chat -> ChatScreen(viewModel = viewModel, onOpenDrawer = { scope.launch { drawerState.open() } })
                    is Screen.CameraVision -> ToolsScreen(viewModel = viewModel) // Or dedicated camera screen
                    is Screen.SchoolAssistant -> SchoolAssistantScreen(viewModel = viewModel)
                    is Screen.StudyCentre -> StudyCentreScreen(viewModel = viewModel)
                    is Screen.Presentation -> PresentationScreen(viewModel = viewModel)
                    is Screen.Video -> VideoMakerScreen(viewModel = viewModel)
                    is Screen.LearningPack -> LearningPackScreen(viewModel = viewModel)
                    is Screen.Creations -> MyCreationsScreen(viewModel = viewModel)
                    is Screen.Workspace -> WorkspaceScreen(viewModel = viewModel)
                    is Screen.Memory -> MemorySettingsScreen(viewModel = viewModel)
                    is Screen.Tasks -> StudyCentreScreen(viewModel = viewModel)
                    is Screen.Settings -> MemorySettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}
