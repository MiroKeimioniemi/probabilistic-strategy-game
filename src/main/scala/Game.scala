import o1.grid.CompassDir.*
import o1.grid.GridPos

class Game:

  val gameMap = GameMap(MapWidth, MapHeight)
  val player1 = Player("Player 1")
  val player2 = Player("Player 2")

  var turnCount = 0
  var gameOver = false

  var selectedBattleUnits: Vector[BattleUnit] = Vector[BattleUnit]()
  var fovTiles: Vector[TerrainTile] = gameMap.tiles.takeRight(4)
  var selectedTiles: Vector[TerrainTile] = Vector[TerrainTile]()

end Game
