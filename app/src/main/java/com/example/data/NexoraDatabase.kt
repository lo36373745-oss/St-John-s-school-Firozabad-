package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ChatEntity::class, MessageEntity::class, MemoryEntity::class, StudyTaskEntity::class, CreationEntity::class, WorkspaceEntity::class],
    version = 3,
    exportSchema = false
)
abstract class NexoraDatabase : RoomDatabase() {
    abstract fun nexoraDao(): NexoraDao

    companion object {
        @Volatile
        private var INSTANCE: NexoraDatabase? = null

        fun getDatabase(context: Context): NexoraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NexoraDatabase::class.java,
                    "nexora_ai_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
