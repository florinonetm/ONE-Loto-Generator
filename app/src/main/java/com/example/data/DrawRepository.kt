package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.random.Random

class DrawRepository(private val drawDao: DrawDao) {

    fun getRecentDraws(gameId: String, limit: Int = 50): Flow<List<Draw>> {
        return drawDao.getRecentDraws(gameId, limit)
    }

    fun getAllDrawsForGame(gameId: String): Flow<List<Draw>> {
        return drawDao.getAllDrawsForGame(gameId)
    }

    suspend fun insertDraw(draw: Draw): Long = withContext(Dispatchers.IO) {
        drawDao.insertDraw(draw)
    }

    suspend fun deleteDraw(id: Long) = withContext(Dispatchers.IO) {
        drawDao.deleteDrawById(id)
    }

    suspend fun clearHistory(gameId: String) = withContext(Dispatchers.IO) {
        drawDao.clearHistoryForGame(gameId)
    }

    /**
     * Seeds initial historical draws if the game has fewer than 10 draws,
     * ensuring Lucky AI and statistics have rich historical data right out of the box.
     */
    suspend fun seedInitialHistoryIfEmpty(
        gameId: String,
        totalNumbers: Int,
        drawCount: Int,
        extraCount: Int = 0,
        extraTotal: Int = 0
    ) = withContext(Dispatchers.IO) {
        val existingCount = drawDao.getDrawCountForGame(gameId)
        if (existingCount < 10) {
            val sampleDraws = mutableListOf<Draw>()
            val now = System.currentTimeMillis()
            val dayMillis = 24 * 60 * 60 * 1000L

            for (i in 35 downTo 1) {
                val availablePool = (1..totalNumbers).toMutableList()
                availablePool.shuffle()
                val picked = availablePool.take(drawCount).sorted()
                val numbersStr = picked.joinToString(",")

                val extraStr = if (extraCount > 0 && extraTotal > 0) {
                    val extraPool = (1..extraTotal).toMutableList()
                    extraPool.shuffle()
                    extraPool.take(extraCount).sorted().joinToString(",")
                } else ""

                sampleDraws.add(
                    Draw(
                        gameId = gameId,
                        numbers = numbersStr,
                        extraNumbers = extraStr,
                        isLuckyAi = i % 3 == 0,
                        timestamp = now - (i * dayMillis * 2) - Random.nextLong(1000, 50000)
                    )
                )
            }
            drawDao.insertAll(sampleDraws)
        }
    }
}
