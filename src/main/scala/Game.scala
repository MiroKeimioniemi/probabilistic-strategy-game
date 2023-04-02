import o1.grid.CompassDir.*
import o1.grid.GridPos

class Game:

  val gameMap = GameMap(MapWidth, MapHeight)
  val player1 = Player("Player 1")
  val player2 = Player("Player 2")

  var turnCount = 0
  var gameOver = false

  var selectedBattleUnits: Vector[BattleUnit] = Vector[BattleUnit]()
  var selectedTiles: Vector[TerrainTile] = Vector[TerrainTile]()

  /** Returns the tiles in the field of view of a given BattleUnit */
  def fovTiles(battleUnit: BattleUnit): Vector[TerrainTile] =
    val coordinatesOfCardinalDirections =
      battleUnit.position.pathTowards(East).take(MapWidth) ++
      battleUnit.position.pathTowards(South).take(MapWidth) ++
      battleUnit.position.pathTowards(West).take(MapWidth) ++
      battleUnit.position.pathTowards(North).take(MapWidth)

    gameMap.tiles.filter(tile => coordinatesOfCardinalDirections.contains(tile.position))

end Game
