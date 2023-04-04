import Action.*
import o1.grid.CompassDir.*
import o1.grid.GridPos

class Game:

  val gameMap = GameMap(MapWidth, MapHeight)
  val player1 = Player("Player 1")
  val player2 = Player("Player 2")

  var turnCount = 0
  var gameOver = false

  var selectedAction: Action = Move
  var selectedBattleUnits: Vector[BattleUnit] = Vector[BattleUnit]()
  var selectedTiles: Vector[TerrainTile] = Vector[TerrainTile]()



  /** Returns the tiles in the field of view of a given BattleUnit */
  def fovTiles(battleUnit: BattleUnit, range: Int): Vector[TerrainTile] =
    val coordinatesOfCardinalDirections =
      battleUnit.position.pathTowards(East).take(range) ++
      battleUnit.position.pathTowards(South).take(range) ++
      battleUnit.position.pathTowards(West).take(range) ++
      battleUnit.position.pathTowards(North).take(range)
    gameMap.tiles.filter(tile => coordinatesOfCardinalDirections.contains(tile.position))
  end fovTiles

  def tilesInRange(battleUnit: BattleUnit): Vector[TerrainTile] =
    selectedAction match
      case Move => fovTiles(battleUnit, battleUnit.range + 1)
      case Attack => fovTiles(battleUnit, MapWidth)
      case Stay => fovTiles(battleUnit, 0)
      case Defend => fovTiles(battleUnit, 0)

  /** Updates the game state by executing all pending actions and clearing all selections */
  def playTurn(): Unit =
    selectedBattleUnits = Vector()
    selectedTiles = Vector()
    turnCount += 1
  end playTurn



end Game
