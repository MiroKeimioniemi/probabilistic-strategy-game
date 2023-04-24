import Action.*
import o1.grid.CompassDir.*
import o1.grid.GridPos

import javax.print.attribute.standard.Destination
import math.{max, min, abs}
import scala.util.Random

class Game:

  // Initializes the Game
  val gameMap = GameMap(MapWidth, MapHeight)
  val player1 = Player("Player 1", Player1BattleUnitsFormation)
  val player2 = Player("Player 2", Player2BattleUnitsFormation)

  val randomNumberGenerator = new Random(System.nanoTime())

  // Game state variables
  var turnCount = 0
  var gameOver = false

  // Turn state variables
  var currentlyPlaying = player1
  var selectedPrimaryAction: Action = Move
  var selectedSecondaryAction: Action = Move
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
  def tilesInRange(battleUnit: BattleUnit, selectedAction: Action): Vector[TerrainTile] =
    selectedAction match
      case Move => fovTiles(battleUnit, battleUnit.range)
      case Attack => fovTiles(battleUnit, MapWidth)
      case Stay => fovTiles(battleUnit, 0)
      case Defend => fovTiles(battleUnit, 0)

  /** Returns the probability of a given BattleUnit successfully moving to destination
   *  @param battleUnit BattleUnit considered to be moved
   *  @param destination Potential destination TerrainTile */
  def calculateMoveProbability(battleUnit: BattleUnit, destination: GridPos): Int =
    val targetTile = gameMap.tiles.filter(_.position == destination).head

    val bw = battleUnit.weight
    val bv = battleUnit.volume

    val df = targetTile.flatness
    val ds = targetTile.solidity
    val dv = targetTile.vegetationDensity
    val de = targetTile.elevation

    MoveSuccessProbability(bw, bv, df, ds, dv, de)
  end calculateMoveProbability

  def calculateAttackProbability(battleUnit: BattleUnit, target: GridPos): Int =

    val xDistance = battleUnit.position.x - target.x
    val yDistance = battleUnit.position.y - target.y
    val absoluteDistance = if abs(xDistance) >= abs(yDistance) then abs(xDistance) else abs(yDistance)

    val opponent = if currentlyPlaying == player1 then player2 else player1
    val targetBattleUnit = opponent.battleUnits.find(_.position == target)
    val targetPath =
      if xDistance >= 0 && yDistance == 0 then
        gameMap.tiles.filter(_.position.y == target.y).takeWhile(_.position.x < battleUnit.position.x).dropWhile(_.position.x < target.x)
      else if xDistance < 0 && yDistance == 0 then
        gameMap.tiles.filter(_.position.y == target.y).dropWhile(_.position.x <= battleUnit.position.x).takeWhile(_.position.x <= target.x)
      else if yDistance >= 0 && xDistance == 0 then
        gameMap.tiles.filter(_.position.x == target.x).takeWhile(_.position.y < battleUnit.position.y).dropWhile(_.position.y < target.y)
      else if yDistance < 0 && xDistance == 0 then
        gameMap.tiles.filter(_.position.x == target.x).dropWhile(_.position.y <= battleUnit.position.y).takeWhile(_.position.y <= target.y)
      else
        Vector()

    var combatSuccessProbability = 100
    var successProbability = 100
    var blockingDegree = 0

    if targetBattleUnit.isDefined then
      combatSuccessProbability = (((battleUnit.armor * battleUnit.damageGradient(absoluteDistance)) / (battleUnit.armor * battleUnit.damageGradient(absoluteDistance) + (targetBattleUnit.get.armor * targetBattleUnit.get.damageGradient(absoluteDistance)))) * 100).toInt

    // Calculates probabilites for a successful attack based on TerrainTiles' characteristics on the path and the damage gradient
    if xDistance < 0 || yDistance < 0 then
      for i <- targetPath.indices do
        successProbability = max(1, (battleUnit.damageGradient(i)) - blockingDegree).toInt
        blockingDegree = blockingDegree + targetPath(i).elevation + targetPath(i).vegetationDensity
    else if xDistance > 0 || yDistance > 0 then
      for i <- targetPath.indices.reverse do
        successProbability = max(1, (battleUnit.damageGradient(targetPath.length - 1 - i)) - blockingDegree).toInt
        blockingDegree = blockingDegree + targetPath(i).elevation + targetPath(i).vegetationDensity

    max(1, ((successProbability * combatSuccessProbability) / 100))

  end calculateAttackProbability

  def calculateSuccessProbability(battleUnit: BattleUnit, destination: GridPos, action: Action): Int =
      action match
        case Move => calculateMoveProbability(battleUnit, destination)
        case Attack => calculateAttackProbability(battleUnit, destination)
        case _ => 100

  /** changes the position of a BattleUnit from the current one to the one given
   *  @param battleUnit BattleUnit to be moved
   *  @param destination GridPos position in the grid where BattleUnit will be moved */
  def move(battleUnit: BattleUnit, destination: GridPos): Unit =

    if !player1.battleUnits.exists(_.position == destination) && !player2.battleUnits.exists(_.position == destination) then
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

    if randomNumberGenerator.nextInt(101) <= calculateSuccessProbability(battleUnit, battleUnit.actionSet.primaryTarget, battleUnit.actionSet.primaryAction) then
      battleUnit.actionSet.primaryAction match
        case Move =>
          move(battleUnit, battleUnit.actionSet.primaryTarget)
        case _ =>
      battleUnit.actionSet.primaryActionSuccess = true

    if !battleUnit.actionSet.primaryActionSuccess && randomNumberGenerator.nextInt(101) <= calculateSuccessProbability(battleUnit, battleUnit.actionSet.secondaryTarget, battleUnit.actionSet.secondaryAction) then
      battleUnit.actionSet.secondaryAction match
        case Move =>
          move(battleUnit, battleUnit.actionSet.secondaryTarget)
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
