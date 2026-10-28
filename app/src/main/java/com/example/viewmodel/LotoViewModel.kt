package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Draw
import com.example.data.DrawRepository
import com.example.util.LotoConfig
import com.example.util.NumberGenerator
import com.example.util.StatisticsCalculator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LotoViewModel(
    application: Application,
    private val repository: DrawRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LotoUiState())
    val uiState: StateFlow<LotoUiState> = _uiState.asStateFlow()

    private var historyCollectionJob: Job? = null

    init {
        // Initialize with default game
        selectGame(LotoConfig.DEFAULT_GAME)
    }

    fun selectGame(gameConfig: LotoConfig) {
        historyCollectionJob?.cancel()

        _uiState.update { current ->
            current.copy(
                selectedGame = gameConfig,
                lockedNumbers = emptySet(),
                excludedNumbers = emptySet(),
                currentDraw = null,
                userNotification = null
            )
        }

        // Seed initial historical data if this game doesn't have enough draws yet
        viewModelScope.launch {
            repository.seedInitialHistoryIfEmpty(
                gameId = gameConfig.id,
                totalNumbers = gameConfig.totalNumbers,
                drawCount = gameConfig.drawCount,
                extraCount = gameConfig.extraNumbersCount,
                extraTotal = gameConfig.extraNumbersTotal
            )
        }

        // Collect recent draws for the selected game
        historyCollectionJob = viewModelScope.launch {
            repository.getRecentDraws(gameConfig.id, limit = 50).collect { draws ->
                val stats = StatisticsCalculator.calculate(
                    draws = draws,
                    totalNumbers = gameConfig.totalNumbers,
                    extraTotal = gameConfig.extraNumbersTotal
                )
                _uiState.update { current ->
                    current.copy(
                        recentDraws = draws.take(3),
                        allHistory = draws,
                        statistics = stats
                    )
                }
            }
        }
    }

    /**
     * Toggles lock on a number (Click)
     * Locked numbers are forced into the next generation.
     */
    fun toggleLock(number: Int) {
        val state = _uiState.value
        val currentLocked = state.lockedNumbers.toMutableSet()
        val currentExcluded = state.excludedNumbers.toMutableSet()

        if (currentLocked.contains(number)) {
            currentLocked.remove(number)
            _uiState.update { it.copy(lockedNumbers = currentLocked, userNotification = "Număr deblocat: #$number") }
        } else {
            if (currentLocked.size >= state.maxLockable) {
                _uiState.update { it.copy(userNotification = "Maxim ${state.maxLockable} numere pot fi blocate!") }
                return
            }
            currentExcluded.remove(number)
            currentLocked.add(number)
            _uiState.update {
                it.copy(
                    lockedNumbers = currentLocked,
                    excludedNumbers = currentExcluded,
                    userNotification = "Număr blocat (forțat): #$number"
                )
            }
        }
    }

    /**
     * Toggles exclusion on a number (Long-press)
     * Excluded numbers will never appear in generation.
     */
    fun toggleExclude(number: Int) {
        val state = _uiState.value
        val currentLocked = state.lockedNumbers.toMutableSet()
        val currentExcluded = state.excludedNumbers.toMutableSet()

        if (currentExcluded.contains(number)) {
            currentExcluded.remove(number)
            _uiState.update { it.copy(excludedNumbers = currentExcluded, userNotification = "Excludere anulată: #$number") }
        } else {
            val total = state.selectedGame.totalNumbers
            val drawCount = state.selectedGame.drawCount
            // Ensure we don't exclude so many that there aren't enough numbers left to draw
            if (total - (currentExcluded.size + 1) < drawCount) {
                _uiState.update { it.copy(userNotification = "Nu poți exclude mai multe numere!") }
                return
            }
            currentLocked.remove(number)
            currentExcluded.add(number)
            _uiState.update {
                it.copy(
                    lockedNumbers = currentLocked,
                    excludedNumbers = currentExcluded,
                    userNotification = "Număr exclus: #$number"
                )
            }
        }
    }

    fun generateRandom() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }
            delay(150) // Micro animation feeling

            val result = NumberGenerator.generateRandom(
                config = state.selectedGame,
                lockedNumbers = state.lockedNumbers,
                excludedNumbers = state.excludedNumbers
            )

            // Save to database
            val draw = Draw(
                gameId = state.selectedGame.id,
                numbers = result.numbers.joinToString(","),
                extraNumbers = result.extraNumbers.joinToString(","),
                isLuckyAi = false,
                timestamp = System.currentTimeMillis()
            )
            repository.insertDraw(draw)

            _uiState.update {
                it.copy(
                    currentDraw = result,
                    isGenerating = false,
                    userNotification = "Extragere aleatorie generată și salvată!"
                )
            }
        }
    }

    fun generateLuckyAi() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }
            delay(200) // Micro animation feeling for AI processing

            val result = NumberGenerator.generateLuckyAi(
                config = state.selectedGame,
                lockedNumbers = state.lockedNumbers,
                excludedNumbers = state.excludedNumbers,
                statistics = state.statistics
            )

            // Save to database
            val draw = Draw(
                gameId = state.selectedGame.id,
                numbers = result.numbers.joinToString(","),
                extraNumbers = result.extraNumbers.joinToString(","),
                isLuckyAi = true,
                timestamp = System.currentTimeMillis()
            )
            repository.insertDraw(draw)

            _uiState.update {
                it.copy(
                    currentDraw = result,
                    isGenerating = false,
                    userNotification = "Extragere Lucky AI generată cu succes!"
                )
            }
        }
    }

    fun resetGridAndDraw() {
        _uiState.update {
            it.copy(
                lockedNumbers = emptySet(),
                excludedNumbers = emptySet(),
                currentDraw = null,
                userNotification = "Grilă și selecții resetate."
            )
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteDraw(id)
        }
    }

    fun clearAllHistoryForCurrentGame() {
        val gameId = _uiState.value.selectedGame.id
        viewModelScope.launch {
            repository.clearHistory(gameId)
            _uiState.update { it.copy(userNotification = "Istoricul a fost șters.") }
        }
    }

    fun setShowStatsDialog(show: Boolean) {
        _uiState.update { it.copy(showStatsDialog = show) }
    }

    fun setShowHistoryDialog(show: Boolean) {
        _uiState.update { it.copy(showHistoryDialog = show) }
    }

    fun dismissNotification() {
        _uiState.update { it.copy(userNotification = null) }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val database = AppDatabase.getInstance(application)
                    val repository = DrawRepository(database.drawDao())
                    return LotoViewModel(application, repository) as T
                }
            }
        }
    }
}
