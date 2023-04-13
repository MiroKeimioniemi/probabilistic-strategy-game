import o1.{GridPos, South, West}
import o1.grid.CompassDir.{East, North}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.*

class GameSpec extends AnyFlatSpec with Matchers:

  val game = new Game

  "Game" should "be initialized correctly" in{
    withClue(s"Game should contain a map with ${MapWidth * MapHeight} TerrainTiles") {
      game.gameMap.tiles.length should equal (MapWidth * MapHeight)
    }

    withClue(s"Game should have player1 with ${Player1BattleUnitsFormation.length} BattleUnits") {
      game.player1.battleUnits.length should equal (Player1BattleUnitsFormation.length)
    }

    withClue("Player 1's BattleUnit vector should contain BattleUnits") {
      game.player1.battleUnits.last shouldBe a [BattleUnit]
    }
  }

  // TODO: Add a test for tilesInRange

  "Probabilities" should "range from 1 to 100" in {

    val battleUnit = game.player1.battleUnits(0)

    withClue("tilesInRange probabilities should be between 1 and 100") {
      for tile <- game.tilesInRange(battleUnit) do
        game.calculateMoveProbability(battleUnit, tile) should (be >= 0 and be <= 100)
    }
  }

  "Move" should "work according to specification" in {

    val battleUnit = game.player1.battleUnits(0)
    battleUnit.orientation = East
    battleUnit.position = GridPos(2, 2)

    withClue("move should change BattleUnit position to match destination coordinates and orientation to match direction moved") {
      game.move(battleUnit, GridPos(3, 2))
      battleUnit.position should equal (GridPos(3, 2))
      battleUnit.orientation should equal (East)
    }

    withClue("move should change BattleUnit position to match destination coordinates and orientation to match direction moved") {
      game.move(battleUnit, GridPos(3, 3))
      battleUnit.position should equal (GridPos(3, 3))
      battleUnit.orientation should equal (South)
    }

    withClue("move should change BattleUnit position to match destination coordinates and orientation to match direction moved") {
      game.move(battleUnit, GridPos(2, 3))
      battleUnit.position should equal (GridPos(2, 3))
      battleUnit.orientation should equal (West)
    }

    withClue("move should change BattleUnit position to match destination coordinates and orientation to match direction moved") {
      game.move(battleUnit, GridPos(2, 2))
      battleUnit.position should equal (GridPos(2, 2))
      battleUnit.orientation should equal (North)
    }
  }