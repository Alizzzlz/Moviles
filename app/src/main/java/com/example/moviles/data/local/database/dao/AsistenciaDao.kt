package com.example.moviles.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviles.data.local.database.entity.AsistenciaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AsistenciaDao {

    @Query("SELECT * FROM asistencias ORDER BY id DESC")
    fun obtenerHistorial(): Flow<List<AsistenciaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarAsistencia(asistencia: AsistenciaEntity)

    @Query("SELECT * FROM asistencias WHERE sincronizado = 0")
    suspend fun obtenerPendientesSincronizacion(): List<AsistenciaEntity>

    @Query("DELETE FROM asistencias")
    suspend fun borrarTodo()
}