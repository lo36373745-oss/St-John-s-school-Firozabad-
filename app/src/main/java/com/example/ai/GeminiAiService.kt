package com.example.ai

import android.graphics.Bitmap
import android.util.Log
import com.example.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

class GeminiAiService {
    companion object {
        private const val TAG = "GeminiAiService"
    }

    private val apiKey = BuildConfig.GEMINI_API_KEY.ifEmpty { "AIzaSyPlaceholderKeyForBuild" }

    private val model by lazy {
        GenerativeModel(
            modelName = "gemini-3.8-flash",
            apiKey = apiKey
        )
    }

    suspend fun generateResponse(prompt: String, systemInstruction: String? = null): String {
        return try {
            withTimeout(120_000L) {
                val effectiveModel = if (!systemInstruction.isNullOrBlank()) {
                    GenerativeModel(
                        modelName = "gemini-3.8-flash",
                        apiKey = apiKey,
                        systemInstruction = content { text(systemInstruction) }
                    )
                } else {
                    model
                }
                val response = effectiveModel.generateContent(prompt)
                response.text ?: "I am sorry, I couldn't generate a response at the moment."
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Generation timed out", e)
            "Request timed out after 120 seconds. Please check your network and try again."
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gemini generation error", e)
            "Error communicating with Gemini AI: ${e.localizedMessage ?: "Unknown error"}. Please ensure your GEMINI_API_KEY is set in the Secrets panel."
        }
    }

    suspend fun generateStreamResponse(
        prompt: String,
        systemInstruction: String? = null,
        onChunk: (String) -> Unit
    ): String {
        val accumulated = StringBuilder()
        try {
            withTimeout(120_000L) {
                val effectiveModel = if (!systemInstruction.isNullOrBlank()) {
                    GenerativeModel(
                        modelName = "gemini-3.8-flash",
                        apiKey = apiKey,
                        systemInstruction = content { text(systemInstruction) }
                    )
                } else {
                    model
                }
                effectiveModel.generateContentStream(prompt).collect { chunk ->
                    val chunkText = chunk.text.orEmpty()
                    accumulated.append(chunkText)
                    onChunk(accumulated.toString())
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Streaming timed out, falling back to unary generateContent", e)
            // Fallback to unary generation so user gets a response
            try {
                val fallbackResponse = generateResponse(prompt, systemInstruction)
                accumulated.clear()
                accumulated.append(fallbackResponse)
                onChunk(fallbackResponse)
            } catch (fallbackEx: Exception) {
                accumulated.append("\n[Request timed out and fallback failed]")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Streaming error", e)
            accumulated.append("\n[Error: ${e.localizedMessage}]")
        }
        return accumulated.toString()
    }

    suspend fun generateMultimodalResponse(prompt: String, bitmap: Bitmap): String {
        return try {
            withTimeout(120_000L) {
                val response = model.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )
                response.text ?: "Could not analyze the image."
            }
        } catch (e: TimeoutCancellationException) {
            "Image analysis timed out."
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Multimodal error", e)
            "Failed to analyze image: ${e.localizedMessage}"
        }
    }
}
