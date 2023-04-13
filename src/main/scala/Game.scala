import Action.*
import o1.grid.CompassDir.*
import o1.grid.GridPos
import javax.print.attribute.standard.Destination

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

  /** Returns the probability of a given BattleUnit successfully moving to destination */
  def calculateMoveProbability(battleUnit: BattleUnit, destination: TerrainTile): Int =
    destination.flatness

  /** changes the position of a BattleUnit from the current one to the one given */
  // TODO: Make sure that two battleUnits cannot move to the same tile
  def move(battleUnit: BattleUnit, destination: GridPos): Unit =

    if battleUnit.position.x - destination.x > 0 then
      battleUnit.orientation = West
    else if battleUnit.position.x - destination.x < 0 then
      battleUnit.orientation = East
    else if battleUnit.position.y - destination.y > 0 then
      battleUnit.orientation = North
    else
      battleUnit.orientation = South

    battleUnit.position = destination

  end move

  /** Updates the game state by executing all pending actions and clearing all selections */
  def playTurn(): Unit =
    if pendingActions.nonEmpty && pendingTargets.nonEmpty then
      for battleUnit <- pendingActions do
        move(battleUnit, pendingTargets(pendingActions.indexOf(battleUnit)).position)
    turnCount += 1
    currentlyPlaying = if currentlyPlaying == player1 then player2 else player1
  end playTurn



end Game
