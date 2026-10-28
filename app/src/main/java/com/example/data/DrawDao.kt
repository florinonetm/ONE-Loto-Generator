package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DrawDao {
    @Query("SELECT * FROM draws WHERE gameId = :gameId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentDraws(gameId: String, limit: Int = 50): Flow<List<Draw>>

    @Query("SELECT * FROM draws WHERE gameId = :gameId ORDER BY timestamp DESC")
    fun getAllDrawsForGame(gameId: String): Flow<List<Draw>>

    @Query("SELECT * FROM draws ORDER BY timestamp DESC LIMIT 100")
    fun getAllRecentDraws(): Flow<List<Draw>>

    @Query("SELECT COUNT(*) FROM draws WHERE gameId = :gameId")
    suspend fun getDrawCountForGame(gameId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraw(draw: Draw): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(draws: List<Draw>)

    @Query("DELETE FROM draws WHERE id = :id")
    suspend fun deleteDrawById(id: Long)

    @Query("DELETE FROM draws WHERE gameId = :gameId")
    suspend fun clearHistoryForGame(gameId: String)
}
