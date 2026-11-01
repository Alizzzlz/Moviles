package com.example.moviles.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviles.data.local.database.entity.CupoHoraExtraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HoraExtraDao {

    @Query("SELECT * FROM cupos_horas_extras ORDER BY id DESC")
    fun obtenerTodosLosCupos(): Flow<List<CupoHoraExtraEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCupo(cupo: CupoHoraExtraEntity)

    @Query("DELETE FROM cupos_horas_extras WHERE id = :id")
    suspend fun eliminarCupo(id: Int)
}