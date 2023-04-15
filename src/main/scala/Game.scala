import Action.*
import o1.grid.CompassDir.*
import o1.grid.GridPos
import javax.print.attribute.standard.Destination
import math.{max, min}

class Game:

  // Initializes the Game
  val gameMap = GameMap(MapWidth, MapHeight)
  val player1 = Player("Player 1")
  val player2 = Player("Player 2")

  // Game state variables
  var turnCount = 0
  var gameOver = false

  // Turn state variables
  var currentlyPlaying = player1
  var selectedAction: Action = Move
  var selectedBattleUnit: Option[BattleUnit] = None
  var selectedPrimaryTile: Option[TerrainTile] = None
  var selectedSecondaryTile: Option[TerrainTile] = None
  var selectingSecondaryTarget: Boolean = false
  var pendingActions: Vector[BattleUnit] = Vector[BattleUnit]()



  /** Returns the tiles in the field of view of a given BattleUnit, that is,
   *  all tiles to the cardinal directions with respect to the BattleUnit
   *  within its range
   *  @param battleUnit BattleUnit considered
   *  @param range number of tiles expected in each cardinal direction */
  def fovTiles(battleUnit: BattleUnit, range: Int): Vector[TerrainTile] =
    val coordinatesOfCardinalDirections =
      battleUnit.position.pathTowards(East).take(range) ++
      battleUnit.position.pathTowards(South).take(range) ++
      battleUnit.position.pathTowards(West).take(range) ++
      battleUnit.position.pathTowards(North).take(range)
    gameMap.tiles.filter(tile => coordinatesOfCardinalDirections.contains(tile.position))
  end fovTiles

  /** Returns the tiles in the field of view of a given BattleUnit depending on the currently selected action
   *  @param battleUnit BattleUnit considered */
  def tilesInRange(battleUnit: BattleUnit): Vector[TerrainTile] =
    selectedAction match
      case Move => fovTiles(battleUnit, battleUnit.range)
      case Attack => fovTiles(battleUnit, MapWidth)
      case Stay => fovTiles(battleUnit, 0)
      case Defend => fovTiles(battleUnit, 0)

  /** Returns the probability of a given BattleUnit successfully moving to destination
   *  @param battleUnit BattleUnit considered to be moved
   *  @param destination Potential destination TerrainTile */
  def calculateMoveProbability(battleUnit: BattleUnit, destination: TerrainTile): Int =
    val bw = battleUnit.weight
    val bv = battleUnit.volume

    val df = destination.flatness
    val ds = destination.solidity
    val dv = destination.vegetationDensity
    val de = destination.elevation

    MoveSuccessProbability(bw, bv, df, ds, dv, de)

  def calculateSuccessProbability(battleUnit: BattleUnit, destination: TerrainTile): Int =
    selectedAction match
      case Move => calculateMoveProbability(battleUnit, destination)
      case _ => 100

  /** changes the position of a BattleUnit from the current one to the one given
   *  @param battleUnit BattleUnit to be moved
   *  @param destination GridPos position in the grid where BattleUnit will be moved */
  def move(battleUnit: BattleUnit, destination: GridPos): Unit =

    if !player1.battleUnits.exists(_.position == destination) then
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

  /** Executes the ActionSet of a given BattleUnit by mathcing the actions to correct functions
   *  and keeping track of their successes and failures
   *  @param battleUnit BattleUnit whose ActionSet will be executed */
  def executeActionSet(battleUnit: BattleUnit): Unit =
    battleUnit.actionSet.primaryAction match
      case Move => move(battleUnit, battleUnit.actionSet.primaryTarget)
      case _ =>

  /** Updates the game state by executing all pending actions and clearing all selections */
  def playTurn(): Unit =
    if pendingActions.nonEmpty then
      for battleUnit <- pendingActions.reverse do
        executeActionSet(battleUnit)
    turnCount += 1
    currentlyPlaying = if currentlyPlaying == player1 then player2 else player1
  end playTurn



end Game
