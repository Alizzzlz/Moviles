package com.example.moviles.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moviles.data.local.database.dao.AsistenciaDao
import com.example.moviles.data.local.database.entity.AsistenciaEntity

@Database(entities = [AsistenciaEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun asistenciaDao(): AsistenciaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "obra_check_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}