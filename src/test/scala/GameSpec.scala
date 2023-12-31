import o1.{GridPos, South, West}
import o1.grid.CompassDir.{East, North}
import o1.grid.GridPos
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.*

class GameSpec extends AnyFlatSpec with Matchers with BeforeAndAfter:

  // Initializes the game with specific map and battleUnit configurations
  val game = new Game
  game.gameMap.tiles = GameMap.symmetricTileUpdater(
    GameMap.tileGenerator(16, 9),
    Vector[TerrainTile](
      RockTile(GridPos(4, 2)),
      RockTile(GridPos(5, 2)),
      RockTile(GridPos(3, 8)),
      RockTile(GridPos(7, 4)),
      RockTile(GridPos(7, 6)),
      RockTile(GridPos(8, 6)),
      SandTile(GridPos(7, 1)),
      SandTile(GridPos(8, 1)),
      SandTile(GridPos(8, 2)),
      SandTile(GridPos(8, 7)),
      SandTile(GridPos(8, 8)),
      SandTile(GridPos(8, 9)),
      SandTile(GridPos(7, 9)),
      ForestTile(GridPos(6, 4)),
      ForestTile(GridPos(4, 5)),
      ForestTile(GridPos(5, 5)),
      ForestTile(GridPos(6, 5)),
      ForestTile(GridPos(7, 5)),
      ForestTile(GridPos(5, 6)),
      ForestTile(GridPos(6, 6)),
      ForestTile(GridPos(6, 7)),
      ForestTile(GridPos(7, 7)),
      ConquestTile(GridPos(8, 5))
  ), "origin")
  game.player1.battleUnits = Vector[BattleUnit](TankUnit(GridPos(7, 8), East, true))
  game.player2.battleUnits = Vector[BattleUnit](TankUnit(GridPos(9, 8), West, false))

  // BattleUnit used in tests
  val testBattleUnit = game.player1.battleUnits(0)
  val testEnemyUnit  = game.player2.battleUnits(0)

  // Restores game state before each test
  before {
    testBattleUnit.orientation = East
    testBattleUnit.position    = GridPos(7, 8)
    testBattleUnit.actionSet   = ActionSet(Action.Rest, testBattleUnit.position, Action.Rest, testBattleUnit.position)
    testBattleUnit.health      = testBattleUnit.maxHealth
    testBattleUnit.alive       = true
    testEnemyUnit.health       = testEnemyUnit.maxHealth
    testBattleUnit.fuel        = testBattleUnit.maxFuel
    testBattleUnit.ammo        = testBattleUnit.maxAmmo
    game.randomNumberGenerator.setSeed(1)
  }

  after {
    game.refuel(testBattleUnit)
  }

  "Game" should "be initialized correctly" in {

    withClue(s"Game should contain a map with ${16 * 9} TerrainTiles") {
      game.gameMap.tiles.length should equal (16 * 9)
    }

    withClue(s"Game should have player1 with ${1} BattleUnits") {
      game.player1.battleUnits.length should equal (1)
    }

    withClue("Player 1's BattleUnit vector should contain BattleUnits") {
      game.player1.battleUnits.last shouldBe a [BattleUnit]
    }

    withClue("Player 2's BattleUnit vector should contain BattleUnits") {
      game.player2.battleUnits.last shouldBe a [BattleUnit]
    }
  }

  "probabilities" should "work according to specification" in {

    withClue("tilesInRange probabilities should range between 1 and 100") {
      for tile <- game.tilesInRange(testBattleUnit, Action.Move) do
        game.calculateMoveProbability(testBattleUnit, tile.position) should (be >= 1 and be <= 100)
    }

    withClue("tilesInRange probabilities should range between 1 and 100") {
      for tile <- game.tilesInRange(testBattleUnit, Action.Move) do
        game.calculateAttackProbability(testBattleUnit, tile.position) should (be >= 1 and be <= 100)
    }

    withClue("randomNumberGenerator should produce the correct number sequence with numbers ranging from 0 to 100 when initialized with seed 1") {
      (1 to 100).map(_ => game.randomNumberGenerator.nextInt(101)) should equal (Vector(97, 5, 21, 41, 77, 60, 33, 30, 45, 50, 4, 64, 19, 81, 43, 30, 31, 38, 55, 33, 71, 58, 7, 88, 71, 48, 35, 56, 20, 48, 6, 60, 20, 50, 78, 65, 77, 83, 11, 57, 83, 20, 52, 62, 31, 95, 8, 69, 43, 85, 35, 88, 66, 44, 96, 17, 29, 87, 89, 69, 5, 31, 4, 73, 24, 9, 71, 59, 25, 80, 21, 52, 48, 33, 10, 5, 4, 19, 99, 39, 66, 64, 33, 91, 55, 67, 65, 34, 65, 91, 76, 90, 31, 10, 54, 87, 19, 84, 39, 62))
    }
  }

  "move" should "work according to specification" in {

    withClue("move should change BattleUnit position to match destination coordinates and orientation to match direction moved") {
      game.move(testBattleUnit, GridPos(8, 8))
      testBattleUnit.position should equal (GridPos(8, 8))
      testBattleUnit.orientation should equal (East)
    }

    withClue("move should change BattleUnit position to match destination coordinates and orientation to match direction moved") {
      game.move(testBattleUnit, GridPos(8, 9))
      testBattleUnit.position should equal (GridPos(8, 9))
      testBattleUnit.orientation should equal (South)
    }

    withClue("move should change BattleUnit position to match destination coordinates and orientation to match direction moved") {
      game.move(testBattleUnit, GridPos(7, 9))
      testBattleUnit.position should equal (GridPos(7, 9))
      testBattleUnit.orientation should equal (West)
    }

    withClue("move should change BattleUnit position to match destination coordinates and orientation to match direction moved") {
      game.refuel(testBattleUnit)
      game.move(testBattleUnit, GridPos(7, 8))
      testBattleUnit.position should equal (GridPos(7, 8))
      testBattleUnit.orientation should equal (North)
    }
  }

  "attack" should "work according to specification" in {

    val battleUnitHealthBeforeAttack = testBattleUnit.health
    val enemyHealthBeforeAttack = testEnemyUnit.health
    val targetTile = game.gameMap.tiles.find(_.position == GridPos(9, 8)).getOrElse(game.gameMap.tiles.head)

    withClue("failed attack against an enemy should leave both BattleUnit's health levels untouched") {
      game.attack(testBattleUnit, GridPos(9, 8))
      testBattleUnit.health should be (testBattleUnit.maxHealth)
      testEnemyUnit.health should be (testEnemyUnit.maxHealth)
    }

    withClue("failed attack against an enemy should leave the tile it is on as is") {
      targetTile.image should be (SandImage)
      targetTile.flatness should be (SandFlatness)
      targetTile.solidity should be (SandSolidity)
      targetTile.vegetationDensity should be (SandVegetationDensity)
      targetTile.elevation should be (SandElevation)
    }

    withClue("lost attack should decrease the attacking BattleUnit's health") {
      game.randomNumberGenerator.nextInt(101)
      game.randomNumberGenerator.nextInt(101)
      game.randomNumberGenerator.nextInt(101)
      game.attack(testBattleUnit, GridPos(9, 8))
      testBattleUnit.health should be < battleUnitHealthBeforeAttack
    }

    withClue("won attack should decrease the attacked BattleUnit's health") {
      game.attack(testBattleUnit, GridPos(9, 8))
      testEnemyUnit.health should be < enemyHealthBeforeAttack
    }

    withClue("successful attack should degrade the target tile") {
      val targetTileFlatnessBeforeAttack = game.gameMap.tiles.filter(_.position == GridPos(9, 8)).head.flatness
      game.attack(testBattleUnit, GridPos(9, 8))
      game.gameMap.tiles.filter(_.position == GridPos(9, 8)).head.flatness should be !== targetTileFlatnessBeforeAttack
    }

  }

  "defend" should "work according to specification" in {

    testBattleUnit.defend()
    testEnemyUnit.defend()

    withClue("lost attack by formerly defending BattleUnit should decrease the attacking BattleUnit's health as normal") {
      (1 to 44).foreach(_ => game.randomNumberGenerator.nextInt(101))
      game.attack(testBattleUnit, GridPos(9, 8))
      testBattleUnit.health should be (testBattleUnit.maxHealth - testEnemyUnit.damageGradient(1).toInt)
    }

    withClue("won attack against a defending BattleUnit should decrease the attacked BattleUnit's health less than normal") {
      game.attack(testBattleUnit, GridPos(9, 8))
      testEnemyUnit.health should be > (testBattleUnit.maxHealth - testEnemyUnit.damageGradient(1).toInt)
    }

  }

  "executeActionSet" should "work according to specification" in {

    withClue("BattleUnit should fail to move to (8, 8) but succeed at moving to (6, 8)") {
      testBattleUnit.setActionSet(Action.Move, GridPos(8, 8), Action.Move, GridPos(6, 8))
      game.executeActionSet(testBattleUnit)
      testBattleUnit.position should equal (GridPos(6, 8))
    }

    withClue("BattleUnit should fail to move at all") {
      testBattleUnit.position = GridPos(9, 6)
      testBattleUnit.setActionSet(Action.Move, GridPos(8, 6), Action.Move, GridPos(10, 6))
      game.executeActionSet(testBattleUnit)
      testBattleUnit.position should equal (GridPos(9, 6))
    }
  }