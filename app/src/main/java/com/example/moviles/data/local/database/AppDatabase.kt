package com.example.moviles.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.moviles.data.local.database.dao.AsistenciaDao
import com.example.moviles.data.local.database.dao.UsuarioDao
import com.example.moviles.data.local.database.entity.AsistenciaEntity
import com.example.moviles.data.local.entity.UsuarioEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UsuarioEntity::class, AsistenciaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun asistenciaDao(): AsistenciaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Mark_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                // Obtenemos la instancia válida a través de getDatabase
                                getDatabase(context).usuarioDao().insertarUsuario(
                                    UsuarioEntity(
                                        id = 1,
                                        nombres = "Vania Constanza",
                                        apellidos = "Álvares Valdés",
                                        rut = "12.345.678-9",
                                        email = "Vani.Valdes@empresa.cl",
                                        password = "123",
                                        cargo = "Operadora Técnica"
                                    )
                                )
                            }
                        }
                    })
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}