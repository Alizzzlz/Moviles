package com.example.moviles.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviles.data.local.database.entity.SolicitudHoraExtraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudHoraExtraDao {

    @Query("SELECT * FROM solicitudes_horas_extras ORDER BY id DESC")
    fun obtenerTodasLasSolicitudes(): Flow<List<SolicitudHoraExtraEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarSolicitud(solicitud: SolicitudHoraExtraEntity)

    @Query("UPDATE solicitudes_horas_extras SET estado = :nuevoEstado WHERE cupoId = :cupoId")
    suspend fun actualizarEstado(cupoId: Int, nuevoEstado: String)

    @Query("DELETE FROM solicitudes_horas_extras WHERE cupoId = :cupoId")
    suspend fun eliminarPorCupoId(cupoId: Int)
}