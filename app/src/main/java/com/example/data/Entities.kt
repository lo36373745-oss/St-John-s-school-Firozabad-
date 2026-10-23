package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val chatId: String,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val messageId: Long = 0,
    val chatId: String,
    val sender: String, // "user" or "model"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val memoryId: Long = 0,
    val content: String,
    val category: String = "Preference",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_tasks")
data class StudyTaskEntity(
    @PrimaryKey(autoGenerate = true) val taskId: Long = 0,
    val title: String,
    val subject: String,
    val classLevel: String,
    val dueDate: String,
    val isCompleted: Boolean = false
)

@Entity(tableName = "creations")
data class CreationEntity(
    @PrimaryKey(autoGenerate = true) val creationId: Long = 0,
    val type: String, // "Presentation" or "Video" or "LearningPack"
    val title: String,
    val details: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "workspaces")
data class WorkspaceEntity(
    @PrimaryKey(autoGenerate = true) val workspaceId: Long = 0,
    val name: String,
    val description: String,
    val category: String = "General",
    val createdAt: Long = System.currentTimeMillis()
)
