import Action.*
import o1.grid.CompassDir.*
import o1.grid.GridPos

class Game:

  val gameMap = GameMap(MapWidth, MapHeight)
  val player1 = Player("Player 1")
  val player2 = Player("Player 2")

  var turnCount = 0
  var gameOver = false

  var currentlyPlaying = player1
  var selectedAction: Action = Move
  var selectedBattleUnits: Vector[BattleUnit] = Vector[BattleUnit]()
  var pendingActions: Vector[BattleUnit] = Vector[BattleUnit]()
  var selectedTiles: Vector[TerrainTile] = Vector[TerrainTile]()
  var pendingTargets: Vector[TerrainTile] = Vector[TerrainTile]()



  /** Returns the tiles in the field of view of a given BattleUnit */
  def fovTiles(battleUnit: BattleUnit, range: Int): Vector[TerrainTile] =
    val coordinatesOfCardinalDirections =
      battleUnit.position.pathTowards(East).take(range) ++
      battleUnit.position.pathTowards(South).take(range) ++
      battleUnit.position.pathTowards(West).take(range) ++
      battleUnit.position.pathTowards(North).take(range)
    gameMap.tiles.filter(tile => coordinatesOfCardinalDirections.contains(tile.position))
  end fovTiles

  /** Returns the tiles in the field of view of a given BattleUnit depending on the currently selected action */
  def tilesInRange(battleUnit: BattleUnit): Vector[TerrainTile] =
    selectedAction match
      case Move => fovTiles(battleUnit, battleUnit.range + 1)
      case Attack => fovTiles(battleUnit, MapWidth)
      case Stay => fovTiles(battleUnit, 0)
      case Defend => fovTiles(battleUnit, 0)

  def move(battleUnit: BattleUnit, destination: TerrainTile): Unit =

    if battleUnit.position.x - destination.position.x > 0 then
      battleUnit.orientation = West
    else if battleUnit.position.x - destination.position.x < 0 then
      battleUnit.orientation = East
    else if battleUnit.position.y - destination.position.y > 0 then
      battleUnit.orientation = North
    else
      battleUnit.orientation = South

    battleUnit.position = destination.position

  end move

  /** Updates the game state by executing all pending actions and clearing all selections */
  def playTurn(): Unit =
    if pendingActions.nonEmpty && pendingTargets.nonEmpty then
      move(pendingActions(0), pendingTargets(0))
    turnCount += 1
    currentlyPlaying = if currentlyPlaying == player1 then player2 else player1
  end playTurn



end Game
