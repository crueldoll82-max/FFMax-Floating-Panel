package com.example.ffpanel

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY id DESC")
    fun getAll(): Flow<List<SensitivityProfile>>

    @Insert
    suspend fun insert(profile: SensitivityProfile)

    @Update
    suspend fun update(profile: SensitivityProfile)

    @Delete
    suspend fun delete(profile: SensitivityProfile)
}
