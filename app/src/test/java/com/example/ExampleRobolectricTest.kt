package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.Draw
import com.example.util.GameStatistics
import com.example.util.LotoConfig
import com.example.util.LotoGameParameters
import com.example.util.NumberGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ONE Loto", appName)
  }

  @Test
  fun `draw entity parses numbers correctly`() {
    val draw = Draw(gameId = "649", numbers = "12, 5, 49, 1, 33, 22")
    val parsed = draw.parsedNumbers()
    assertEquals(listOf(1, 5, 12, 22, 33, 49), parsed)
  }

  @Test
  fun `loto game parameters defines all five requested games`() {
    val games = LotoGameParameters.ALL_GAMES
    assertEquals(5, games.size)

    val game649 = LotoGameParameters.GAME_6_49
    assertEquals("649", game649.id)
    assertEquals(49, game649.totalNumbers)
    assertEquals(6, game649.drawCount)
    assertFalse(game649.hasExtraNumbers)

    val euromilion = LotoGameParameters.GAME_EUROMILION
    assertEquals("euromillions", euromilion.id)
    assertEquals(50, euromilion.totalNumbers)
    assertEquals(5, euromilion.drawCount)
    assertEquals(2, euromilion.extraNumbersCount)
    assertEquals(12, euromilion.extraNumbersTotal)
    assertTrue(euromilion.hasExtraNumbers)

    val uk659 = LotoGameParameters.GAME_UK_6_59
    assertEquals("uk659", uk659.id)
    assertEquals(59, uk659.totalNumbers)
    assertEquals(6, uk659.drawCount)

    val joker = LotoGameParameters.GAME_JOKER
    assertEquals("ro649plus1", joker.id)
    assertEquals(49, joker.totalNumbers)
    assertEquals(6, joker.drawCount)
    assertEquals(1, joker.extraNumbersCount)
    assertEquals(20, joker.extraNumbersTotal)
    assertTrue(joker.hasExtraNumbers)

    val game540 = LotoGameParameters.GAME_5_40
    assertEquals("ro540", game540.id)
    assertEquals(40, game540.totalNumbers)
    assertEquals(5, game540.drawCount)
    assertFalse(game540.hasExtraNumbers)
  }

  @Test
  fun `loto game parameters lookup and validation work`() {
    assertNotNull(LotoGameParameters.getGameById("649"))
    assertNotNull(LotoGameParameters.getGameById("euromillions"))
    assertNotNull(LotoGameParameters.getGameById("joker"))
    assertNotNull(LotoGameParameters.getGameById("540"))
    assertNotNull(LotoGameParameters.getGameById("uk659"))

    val game649 = LotoGameParameters.GAME_6_49
    assertTrue(LotoGameParameters.validateMainNumbers(game649, listOf(1, 10, 20, 30, 40, 49)))
    assertFalse(LotoGameParameters.validateMainNumbers(game649, listOf(1, 10, 20, 30, 40))) // Only 5
    assertFalse(LotoGameParameters.validateMainNumbers(game649, listOf(1, 10, 20, 30, 40, 50))) // 50 out of bounds

    val euromilion = LotoGameParameters.GAME_EUROMILION
    assertTrue(LotoGameParameters.validateExtraNumbers(euromilion, listOf(3, 11)))
    assertFalse(LotoGameParameters.validateExtraNumbers(euromilion, listOf(3, 15))) // Star 15 > 12
  }

  @Test
  fun `number generator respects locked and excluded numbers`() {
    val config = LotoConfig.DEFAULT_GAME // 6 din 49
    val locked = setOf(7, 14)
    val excluded = setOf(1, 2, 3)

    val result = NumberGenerator.generateRandom(config, locked, excluded)

    assertEquals(6, result.numbers.size)
    assertTrue("Must contain locked number 7", result.numbers.contains(7))
    assertTrue("Must contain locked number 14", result.numbers.contains(14))
    assertFalse("Must not contain excluded number 1", result.numbers.contains(1))
    assertFalse("Must not contain excluded number 2", result.numbers.contains(2))
    assertFalse("Must not contain excluded number 3", result.numbers.contains(3))
  }

  @Test
  fun `lucky ai generates balanced draw`() {
    val config = LotoConfig.DEFAULT_GAME // 6 din 49
    val stats = GameStatistics(
        totalDrawsAnalyzed = 20,
        hotNumbers = listOf(7, 13, 21, 35, 42),
        coldNumbers = listOf(2, 11, 24, 31, 48)
    )

    val result = NumberGenerator.generateLuckyAi(config, emptySet(), emptySet(), stats)

    assertEquals(6, result.numbers.size)
    assertTrue("Sum is positive and reasonable", result.sum > 0)
    assertEquals(6, result.oddCount + result.evenCount)
  }
}
