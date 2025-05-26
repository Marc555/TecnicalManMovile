package es.tecnicalman.utils.room

import android.content.Context
import androidx.room.Room

object DatabaseInstance {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "tecnicalman_db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}