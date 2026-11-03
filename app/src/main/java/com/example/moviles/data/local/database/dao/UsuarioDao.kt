package com.example.moviles.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.moviles.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    fun obtenerUsuarioPorId(id: Int): Flow<UsuarioEntity?>

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun obtenerUsuarioPorEmail(email: String): UsuarioEntity?

    @Update
    suspend fun actualizarUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios ORDER BY id DESC")
    fun obtenerTodosLosUsuarios(): Flow<List<UsuarioEntity>>

    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun eliminarUsuarioPorId(id: Int)
}