package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiAiService
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class NcertBook(
    val classLevel: Int,
    val subject: String,
    val title: String,
    val language: String,
    val chapters: List<String>
)

class NexoraViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = NexoraDatabase.getDatabase(application).nexoraDao()
    private val aiService = GeminiAiService()

    // Chats & Messages State
    val allChats: StateFlow<List<ChatEntity>> = dao.getAllChats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentChatId = MutableStateFlow<String?>(null)
    val currentChatId: StateFlow<String?> = _currentChatId.asStateFlow()

    private val _currentMessages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val currentMessages: StateFlow<List<MessageEntity>> = _currentMessages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // Memories State
    val allMemories: StateFlow<List<MemoryEntity>> = dao.getAllMemories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Study Tasks State
    val allStudyTasks: StateFlow<List<StudyTaskEntity>> = dao.getAllStudyTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Creations State
    val allCreations: StateFlow<List<CreationEntity>> = dao.getAllCreations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Workspaces State
    val allWorkspaces: StateFlow<List<WorkspaceEntity>> = dao.getAllWorkspaces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // NCERT Catalogue State
    val ncertBooks: List<NcertBook> = listOf(
        NcertBook(10, "Mathematics", "Mathematics Class 10", "English/Hindi", listOf("Real Numbers", "Polynomials", "Pair of Linear Equations", "Quadratic Equations", "Arithmetic Progressions", "Triangles", "Coordinate Geometry", "Introduction to Trigonometry", "Some Applications of Trigonometry", "Circles", "Areas Related to Circles", "Surface Areas and Volumes", "Statistics", "Probability")),
        NcertBook(10, "Science", "Science Class 10", "English/Hindi", listOf("Chemical Reactions and Equations", "Acids, Bases and Salts", "Metals and Non-metals", "Carbon and its Compounds", "Life Processes", "Control and Coordination", "How do Organisms Reproduce?", "Heredity", "Light - Reflection and Refraction", "Human Eye and Colourful World", "Electricity", "Magnetic Effects of Electric Current", "Our Environment")),
        NcertBook(12, "Physics", "Physics Part 1 & 2", "English", listOf("Electric Charges and Fields", "Electrostatic Potential and Capacitance", "Current Electricity", "Moving Charges and Magnetism", "Magnetism and Matter", "Electromagnetic Induction", "Alternating Current", "Electromagnetic Waves", "Ray Optics", "Wave Optics", "Dual Nature of Radiation", "Atoms", "Nuclei", "Semiconductor Electronics")),
        NcertBook(12, "Chemistry", "Chemistry Part 1 & 2", "English", listOf("Solutions", "Electrochemistry", "Chemical Kinetics", "d- and f-Block Elements", "Coordination Compounds", "Haloalkanes and Haloarenes", "Alcohols, Phenols and Ethers", "Aldehydes, Ketones and Carboxylic Acids", "Amines", "Biomolecules")),
        NcertBook(12, "Biology", "Biology Class 12", "English", listOf("Sexual Reproduction in Flowering Plants", "Human Reproduction", "Reproductive Health", "Principles of Inheritance and Variation", "Molecular Basis of Inheritance", "Evolution", "Human Health and Disease", "Microbes in Human Welfare", "Biotechnology Principles", "Ecology"))
    )

    // School Assistant Knowledge base for St. John's School, Firozabad
    val schoolInfo = mapOf(
        "About" to "St. John's School, Firozabad is a premier institution committed to academic excellence, moral integrity, and holistic student development.",
        "Admissions" to "Admissions for the academic session open in January every year. Parents can register online via the school portal or visit the campus office between 9 AM and 2 PM.",
        "Academics" to "Affiliated with CBSE curriculum, offering Science, Commerce, and Humanities streams with state-of-the-art smart classes, computer labs, and science laboratories.",
        "Facilities" to "Comprehensive infrastructure including well-equipped science labs, computer center, extensive library, sports grounds for football, basketball, cricket, and indoor games.",
        "Contact" to "Location: Station Road, Firozabad, Uttar Pradesh. Phone: +91-5612-234567. Email: info@stjohnsfirozabad.in"
    )

    init {
        viewModelScope.launch {
            dao.getAllChats().collect { chats ->
                if (chats.isEmpty() && _currentChatId.value == null) {
                    createNewChat("Welcome Chat")
                } else if (_currentChatId.value == null && chats.isNotEmpty()) {
                    selectChat(chats.first().chatId)
                }
                return@collect
            }
        }
    }

    suspend fun createNewChat(title: String): String {
        val chatId = UUID.randomUUID().toString()
        val newChat = ChatEntity(chatId = chatId, title = title)
        dao.insertChat(newChat)
        selectChat(chatId)
        return chatId
    }

    fun selectChat(chatId: String) {
        _currentChatId.value = chatId
        viewModelScope.launch {
            dao.getMessagesForChat(chatId).collect { messages ->
                _currentMessages.value = messages
            }
        }
    }

    suspend fun deleteChat(chatId: String) {
        dao.deleteMessagesForChat(chatId)
        dao.deleteChat(chatId)
        if (_currentChatId.value == chatId) {
            val remaining = allChats.value.filter { it.chatId != chatId }
            if (remaining.isNotEmpty()) {
                selectChat(remaining.first().chatId)
            } else {
                createNewChat("New Chat")
            }
        }
    }

    fun sendMessage(prompt: String, systemInstruction: String? = null) {
        val chatId = _currentChatId.value ?: return
        if (prompt.isBlank()) return

        viewModelScope.launch {
            val userMsg = MessageEntity(chatId = chatId, sender = "user", text = prompt)
            dao.insertMessage(userMsg)

            val currentChatList = allChats.value
            val activeChat = currentChatList.find { it.chatId == chatId }
            if (activeChat != null && activeChat.title == "Welcome Chat" || activeChat?.title == "New Chat") {
                val updatedTitle = if (prompt.length > 25) prompt.take(25) + "..." else prompt
                dao.insertChat(activeChat.copy(title = updatedTitle, updatedAt = System.currentTimeMillis()))
            }

            _isGenerating.value = true
            val placeholderMsgId = System.currentTimeMillis()
            val assistantMsg = MessageEntity(messageId = placeholderMsgId, chatId = chatId, sender = "model", text = "Thinking...")
            dao.insertMessage(assistantMsg)

            val fullResponse = StringBuilder()
            aiService.generateStreamResponse(prompt, systemInstruction) { chunk ->
                fullResponse.clear()
                fullResponse.append(chunk)
            }

            val finalResponseText = if (fullResponse.isNotBlank()) fullResponse.toString() else "I have processed your request."
            dao.insertMessage(MessageEntity(chatId = chatId, sender = "model", text = finalResponseText))
            _isGenerating.value = false
        }
    }

    fun sendMultimodalMessage(prompt: String, bitmap: Bitmap) {
        val chatId = _currentChatId.value ?: return
        viewModelScope.launch {
            dao.insertMessage(MessageEntity(chatId = chatId, sender = "user", text = "[Image Attached] $prompt"))
            _isGenerating.value = true
            val responseText = aiService.generateMultimodalResponse(prompt, bitmap)
            dao.insertMessage(MessageEntity(chatId = chatId, sender = "model", text = responseText))
            _isGenerating.value = false
        }
    }

    // Memories management
    fun addMemory(content: String, category: String = "Preference") {
        viewModelScope.launch {
            dao.insertMemory(MemoryEntity(content = content, category = category))
        }
    }

    fun deleteMemory(memoryId: Long) {
        viewModelScope.launch {
            dao.deleteMemory(memoryId)
        }
    }

    // Study Tasks management
    fun addStudyTask(title: String, subject: String, classLevel: String, dueDate: String) {
        viewModelScope.launch {
            dao.insertStudyTask(StudyTaskEntity(title = title, subject = subject, classLevel = classLevel, dueDate = dueDate))
        }
    }

    fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            dao.updateTaskCompletion(taskId, isCompleted)
        }
    }

    fun deleteStudyTask(taskId: Long) {
        viewModelScope.launch {
            dao.deleteStudyTask(taskId)
        }
    }

    // Advanced AI Study Plan Generator
    fun generateAiStudyPlan(classLevel: String, subject: String, examDate: String, dailyHours: String, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            val prompt = "Create a structured, comprehensive daily study plan for Class $classLevel $subject leading up to exam date $examDate with $dailyHours hours daily. Include prioritized topics, milestone reviews, and practice goals."
            val response = aiService.generateResponse(prompt, "You are an expert NCERT curriculum study planner and academic coach.")
            dao.insertStudyTask(StudyTaskEntity(title = "AI Study Schedule: $subject", subject = subject, classLevel = "Class $classLevel", dueDate = examDate))
            onComplete(response)
        }
    }

    // Presentation Maker Generator
    fun generatePresentation(topic: String, classLevel: String, subject: String, slideCount: Int, language: String, audience: String, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            val prompt = "Generate a structured $slideCount-slide presentation for Class $classLevel $subject on topic: '$topic'. Language: $language. Audience: $audience. Include slide headings, key concepts, bullet points, and speaker notes for each slide."
            val response = aiService.generateResponse(prompt, "You are an expert educational presentation designer.")
            dao.insertCreation(CreationEntity(type = "Presentation", title = "$subject: $topic ($slideCount Slides)", details = response))
            onComplete(response)
        }
    }

    // AI Video Maker Generator
    fun generateVideoScript(topic: String, classLevel: String, subject: String, duration: String, style: String, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            val prompt = "Generate an educational video script, storyboard, narration, and visual descriptions for Class $classLevel $subject on topic: '$topic'. Duration: $duration. Style: $style. Provide scene-by-scene breakdown."
            val response = aiService.generateResponse(prompt, "You are an expert educational video director and storyboard artist.")
            dao.insertCreation(CreationEntity(type = "Video", title = "Video Script: $topic ($duration)", details = response))
            onComplete(response)
        }
    }

    // NCERT Learning Pack Generator
    fun generateLearningPack(classLevel: String, subject: String, chapter: String, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            val prompt = "Generate a complete NCERT Learning Pack for Class $classLevel $subject, Chapter: '$chapter'. Include: 1. Revision Notes, 2. Important Questions with answers, 3. Flashcards, 4. Practice Quiz with explanation."
            val response = aiService.generateResponse(prompt, "You are an expert NCERT tutor.")
            dao.insertCreation(CreationEntity(type = "LearningPack", title = "Learning Pack: $chapter ($subject)", details = response))
            onComplete(response)
        }
    }

    fun deleteCreation(creationId: Long) {
        viewModelScope.launch {
            dao.deleteCreation(creationId)
        }
    }

    // Workspace management
    fun addWorkspace(name: String, description: String, category: String) {
        viewModelScope.launch {
            dao.insertWorkspace(WorkspaceEntity(name = name, description = description, category = category))
        }
    }

    fun deleteWorkspace(workspaceId: Long) {
        viewModelScope.launch {
            dao.deleteWorkspace(workspaceId)
        }
    }
}
