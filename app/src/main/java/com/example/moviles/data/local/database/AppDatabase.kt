package com.example.moviles.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.moviles.data.local.database.dao.AsistenciaDao
import com.example.moviles.data.local.database.dao.HoraExtraDao
import com.example.moviles.data.local.database.dao.UsuarioDao
import com.example.moviles.data.local.database.entity.AsistenciaEntity
import com.example.moviles.data.local.database.entity.CupoHoraExtraEntity
import com.example.moviles.data.local.entity.UsuarioEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UsuarioEntity::class, AsistenciaEntity::class, CupoHoraExtraEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun asistenciaDao(): AsistenciaDao
    abstract fun horaExtraDao(): HoraExtraDao

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
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            // Se ejecuta cada vez que la base de datos se abre
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getDatabase(context).usuarioDao()

                                // Verificamos si existe el admin; si no existe, lo creamos
                                val adminExiste = dao.obtenerUsuarioPorEmail("admin@mark.cl") != null
                                if (!adminExiste) {
                                    dao.insertarUsuario(
                                        UsuarioEntity(
                                            id = 2,
                                            nombres = "Carlos",
                                            apellidos = "Mendoza",
                                            rut = "11.111.111-1",
                                            email = "admin@mark.cl",
                                            password = "admin",
                                            cargo = "Jefe de Obra",
                                            rol = "ADMIN",
                                            obra = "Costanera Edificio B"
                                        )
                                    )
                                }

                                // Verificamos si existe el operario por defecto
                                val operarioExiste = dao.obtenerUsuarioPorEmail("Vani.Valdes@empresa.cl") != null
                                if (!operarioExiste) {
                                    dao.insertarUsuario(
                                        UsuarioEntity(
                                            id = 1,
                                            nombres = "Vania Constanza",
                                            apellidos = "Álvares Valdés",
                                            rut = "12.345.678-9",
                                            email = "Vani.Valdes@empresa.cl",
                                            password = "123",
                                            cargo = "Operadora Técnica",
                                            rol = "OPERARIO",
                                            obra = "Costanera Edificio B"
                                        )
                                    )
                                }
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