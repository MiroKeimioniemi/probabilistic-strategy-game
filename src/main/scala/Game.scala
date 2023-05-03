import Action.*
import o1.grid.CompassDir.*
import o1.grid.GridPos

import java.io.IOException
import javax.print.attribute.standard.Destination
import math.{abs, max, min}
import scala.util.Random

class Game:

  // Initializes the Game
  val initializer =
    try {
      loadConfig(LaunchConfigDirectory)
    } catch {
      // If any type of exception is detected, defaults to default configuration from ConfigurationConstants
      case ex: Throwable =>
        println(ex.getMessage)
        (GameMap(MapWidth, MapHeight),
         Player(Player1Name, Player1BattleUnitsFormation),
         Player(Player2Name, Player2BattleUnitsFormation))
    }
  val gameMap = initializer._1
  val player1 = initializer._2
  val player2 = initializer._3

  private val randomNumberGenerator = new Random(System.nanoTime())

  // Game state variables
  var turnCount = 0
  var gameOver = false
  var winner: Option[Player] = None

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
   *  all tiles to the cardinal directions with respect to the BattleUnit,
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
      case Rest => fovTiles(battleUnit, 0)
      case Defend => fovTiles(battleUnit, 0)
      case Reload => fovTiles(battleUnit, 0)


  /** Returns the probability of a given BattleUnit successfully moving to target coordinates that may or may
   * not contain an enemy unit, in which case it returns the probability of successfully destroying them by
   * ramming and moving on top of them
   *  @param battleUnit BattleUnit considered to be moved
   *  @param target destination coordinates */
  def calculateMoveProbability(battleUnit: BattleUnit, target: GridPos): Int =

    val opponent = if currentlyPlaying == player1 then player2 else player1
    val targetBattleUnit = opponent.battleUnits.find(bU => bU.alive && bU.position == target)

    var rammingProbability = 100

    if targetBattleUnit.isDefined then
      rammingProbability = (((battleUnit.armor * battleUnit.weight * 1.0) / (battleUnit.armor * battleUnit.weight * 1.0 + (targetBattleUnit.get.armor * targetBattleUnit.get.weight * 1.0))) * 100).toInt

    val targetTile = gameMap.tiles.filter(_.position == target).head

    val xDistance = battleUnit.position.x - target.x
    val yDistance = battleUnit.position.y - target.y
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

    val bw = battleUnit.weight
    val bv = battleUnit.volume

    val df = targetTile.flatness
    val ds = targetTile.solidity
    val dv = targetTile.vegetationDensity
    val de = targetTile.elevation

    var successProbability = 100
    var blockingDegree = 100 - min(99, max(1, (100 - (0.33 * (bw / (ds + bv))) - (0.33 * (dv + bv)) - (de) + (0.33 * df))).toInt)

    // Calculates probabilites for a successful move based on TerrainTiles' characteristics on the way to target
    if xDistance < 0 || yDistance < 0 then
      for i <- targetPath.indices do
        successProbability = max(1, successProbability - blockingDegree)
        blockingDegree = blockingDegree + targetPath(i).elevation + targetPath(i).vegetationDensity
    else if xDistance > 0 || yDistance > 0 then
      for i <- targetPath.indices.reverse do
        successProbability = max(1, successProbability - blockingDegree)
        blockingDegree = blockingDegree + targetPath(i).elevation + targetPath(i).vegetationDensity

    min(99, max(1, ((successProbability * rammingProbability) / 100)))

  end calculateMoveProbability


  /** Returns the the probability of the BattleUnit successfully beating the other in a duel
   *  @param battleUnit Attacking BattleUnit
   *  @param targetBattleUnit Attacked BattleUnit
   *  @param distance Distance between the BattleUnits */
  def attackProbabilityAgainstBattleUnit(battleUnit: BattleUnit, targetBattleUnit: BattleUnit, distance: Int) =
    (((battleUnit.armor * battleUnit.damageGradient(distance)) / (battleUnit.armor * battleUnit.damageGradient(distance) + (targetBattleUnit.armor * targetBattleUnit.damageGradient(distance)))) * 100).toInt + max(0, min(10, ((2 * battleUnit.experience / battleUnit.baseDamage) - (2 * targetBattleUnit.experience / targetBattleUnit.baseDamage))))


  /** Returns a tuple containing the probability of successfully attacking a target, masked by damageGradient in the first place and the unmasked probability in the second
   *  @param battleUnit Attacking BattleUnit
   *  @param target Coordinates to be attacked*/
  def attackInitiationProbability(battleUnit: BattleUnit, target: GridPos): (Int, Int) =

    val xDistance = battleUnit.position.x - target.x
    val yDistance = battleUnit.position.y - target.y
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

    var successProbability = 100
    var blockingDegree = 0

    // Calculates probabilites for a successful attack based on TerrainTiles' characteristics on the path and the damage gradient
    if xDistance < 0 || yDistance < 0 then
      for i <- targetPath.indices do
        successProbability = max(1, ((battleUnit.damageGradient(i)) * 100.0 / battleUnit.baseDamage) - (blockingDegree * (battleUnit.baseDamage / 100.0))).toInt
        blockingDegree = blockingDegree + targetPath(i).elevation + targetPath(i).vegetationDensity
    else if xDistance > 0 || yDistance > 0 then
      for i <- targetPath.indices.reverse do
        successProbability = max(1, ((battleUnit.damageGradient(targetPath.length - 1 - i)) * 100.0 / battleUnit.baseDamage) - (blockingDegree * (battleUnit.baseDamage / 100.0))).toInt
        blockingDegree = blockingDegree + targetPath(i).elevation + targetPath(i).vegetationDensity

    (successProbability, (100 - min(100, blockingDegree)))

  end attackInitiationProbability


  /** Returns the probability of a given BattleUnit successfully attacking target
   *  @param battleUnit Attacking BattleUnit
   *  @param target Target coordinates */
  def calculateAttackProbability(battleUnit: BattleUnit, target: GridPos): Int =

    val xDistance = battleUnit.position.x - target.x
    val yDistance = battleUnit.position.y - target.y
    val absoluteDistance = if abs(xDistance) >= abs(yDistance) then abs(xDistance) else abs(yDistance)

    val opponent = if currentlyPlaying == player1 then player2 else player1
    val targetBattleUnit = opponent.battleUnits.find(bU => bU.alive && bU.position == target)

    var combatSuccessProbability = 100

    if targetBattleUnit.isDefined then
      combatSuccessProbability = attackProbabilityAgainstBattleUnit(battleUnit, targetBattleUnit.get, absoluteDistance - 1)

    min(99, max(1, ((attackInitiationProbability(battleUnit, target)._1 * combatSuccessProbability) / 100)))

  end calculateAttackProbability


  /** Returns the probability with which the given BattleUnit succeeds to execute the given action on the given target
   *  @param battleUnit BattleUnit considered
   *  @param target Coordinates of the target of the given action
   *  @param action The action for which probabilities are calculated */
  def calculateSuccessProbability(battleUnit: BattleUnit, target: GridPos, action: Action): Int =
      action match
        case Move => calculateMoveProbability(battleUnit, target)
        case Attack => calculateAttackProbability(battleUnit, target)
        case _ => 100


  /** Changes the given BattleUnit's orientation to be towards target
   *  @param battleUnit BattleUnit to be turned
   *  @param target Target coordinates towards which BattleUnit will be turned */
  def turn(battleUnit: BattleUnit, target: GridPos): Unit =
      if battleUnit.position.x - target.x > 0 then
        battleUnit.orientation = West
      else if battleUnit.position.x - target.x < 0 then
        battleUnit.orientation = East
      else if battleUnit.position.y - target.y > 0 then
        battleUnit.orientation = North
      else if battleUnit.position.y - target.y < 0 then
        battleUnit.orientation = South
      else
        battleUnit.orientation = battleUnit.orientation


  /** Changes the position of a BattleUnit from the current one to the one given
   *  @param battleUnit BattleUnit to be moved
   *  @param destination GridPos position in the grid where BattleUnit will be moved */
  def move(battleUnit: BattleUnit, destination: GridPos): Unit =
    if battleUnit.fuel > 0 then
      val opponent = if currentlyPlaying == player1 then player2 else player1
      val targetBattleUnit = opponent.battleUnits.find(bU => bU.alive && bU.position == destination)

      if targetBattleUnit.isDefined then
        targetBattleUnit.get.takeDamage(targetBattleUnit.get.maxHealth)

      if !player1.battleUnits.filter(_.alive).exists(_.position == destination) && !player2.battleUnits.filter(_.alive).exists(_.position == destination) then
        battleUnit.actionSet.primaryActionSuccess = true
        battleUnit.defending = false
        turn(battleUnit, destination)
        battleUnit.useFuel(1)
        battleUnit.position = destination
  end move


  /** Attacks the given target such that if target coordinates contain an enemy BattleUnit, a duel is launched, the loser of which
   *  takes all the damage. The tile in the loser's coordinates or the target coordinates gets degraded depending on whether a duel
   *  is launched and what its outcome is.
   *  @param battleUnit Attacking BattleUnit
   *  @param target Target coordinates of attack */
  def attack(battleUnit: BattleUnit, target: GridPos): Unit =

    battleUnit.defending = false

    turn(battleUnit, target)

    val xDistance = battleUnit.position.x - target.x
    val yDistance = battleUnit.position.y - target.y
    val absoluteDistance = if abs(xDistance) >= abs(yDistance) then abs(xDistance) else abs(yDistance)

    val opponent = if currentlyPlaying == player1 then player2 else player1
    val targetBattleUnit = opponent.battleUnits.find(bU => bU.alive && bU.position == target)
    val targetTile = gameMap.tiles.filter(_.position == target).head

    if targetBattleUnit.isDefined then {

      if randomNumberGenerator.nextInt(101) <= attackInitiationProbability(battleUnit, target)._2 then {

        turn(targetBattleUnit.get, battleUnit.position)
        if battleUnit.ammo > 0 then
          battleUnit.useAmmo(1)
        if targetBattleUnit.get.ammo > 0 then
          targetBattleUnit.get.useAmmo(1)

        if randomNumberGenerator.nextInt(101) <= attackProbabilityAgainstBattleUnit(battleUnit, targetBattleUnit.get, absoluteDistance - 1) then {
          if randomNumberGenerator.nextInt(101) <= attackInitiationProbability(battleUnit, target)._1 && battleUnit.ammo >= 0 then {

            if targetBattleUnit.get.defending then
              targetBattleUnit.get.takeDamage((battleUnit.damageGradient(absoluteDistance - 1) / (max(1, DefendStrength))).toInt)
            else
              targetBattleUnit.get.takeDamage((battleUnit.damageGradient(absoluteDistance - 1).toInt))

            if targetTile.getClass != classOf[RockTile] || battleUnit.explosiveDamage then
              targetTile.degrade(battleUnit.damageGradient(absoluteDistance).toInt)

            battleUnit.actionSet.primaryActionSuccess = true
            battleUnit.gainExperience((battleUnit.damageGradient(absoluteDistance - 1).toInt))
          }
        } else if randomNumberGenerator.nextInt(101) <= attackInitiationProbability(targetBattleUnit.get, battleUnit.position)._1 && targetBattleUnit.get.ammo >= 0 then
            battleUnit.takeDamage(targetBattleUnit.get.damageGradient(absoluteDistance - 1).toInt)
            gameMap.tiles.filter(_.position == battleUnit.position).head.degrade(targetBattleUnit.get.damageGradient(absoluteDistance - 1).toInt)
            targetBattleUnit.get.gainExperience(targetBattleUnit.get.damageGradient(absoluteDistance - 1).toInt)
      }
    } else if randomNumberGenerator.nextInt(101) <= attackInitiationProbability(battleUnit, target)._1 && (targetTile.getClass != classOf[RockTile] || battleUnit.explosiveDamage) && battleUnit.ammo > 0 then
        battleUnit.useAmmo(1)
        battleUnit.actionSet.primaryActionSuccess = true
        targetTile.degrade(battleUnit.damageGradient(max(0, absoluteDistance - 1)).toInt)

  end attack


  /** Sets BattleUnit state as "defending" until it tries to move or attack again. When defending, the BattleUnit will only receive a fraction
   *  (1 / DefendStrength) of the damage it normally would if it loses a duel
   *  @param battleUnit Defending BattleUnit */
  def defend(battleUnit: BattleUnit): Unit =
    battleUnit.actionSet.primaryActionSuccess = true
    battleUnit.defend()


  /** Restores BattleUnit ammo to maximum
   *  @param battleUnit Reloading BattleUnit */
  def reload(battleUnit: BattleUnit): Unit =
    battleUnit.actionSet.primaryActionSuccess = true
    battleUnit.reload()


  /** Restores BattleUnit ammo to maximum
   *  @param battleUnit Reloading BattleUnit */
  def refuel(battleUnit: BattleUnit): Unit =
    battleUnit.actionSet.primaryActionSuccess = true
    battleUnit.refuel()

  /** Updates conquest tile colors and win progress of the current conqueror if their units are exclusively occupying any ConquestTile */
  def updateConquest() =
    val conquestTiles = gameMap.tiles.filter(_.getClass == classOf[ConquestTile])
    if conquestTiles.map(_.position).intersect(player1.battleUnits.filter(_.alive).map(_.position)).nonEmpty && conquestTiles.map(_.position).intersect(player2.battleUnits.filter(_.alive).map(_.position)).isEmpty then
      for cTile <- conquestTiles do
        cTile.asInstanceOf[ConquestTile].updateStatus(Some(Player1Color))
      player1.winProgress += 1
    else if conquestTiles.map(_.position).intersect(player2.battleUnits.filter(_.alive).map(_.position)).nonEmpty && conquestTiles.map(_.position).intersect(player1.battleUnits.filter(_.alive).map(_.position)).isEmpty then
      for cTile <- conquestTiles do
        cTile.asInstanceOf[ConquestTile].updateStatus(Some(Player2Color))
      player2.winProgress += 1
    else
      for cTile <- conquestTiles do
        cTile.asInstanceOf[ConquestTile].updateStatus(None)


  /** Executes the ActionSet of a given BattleUnit by mathcing the actions to correct functions
   *  and keeping track of their successes and failures
   *  @param battleUnit BattleUnit whose ActionSet will be executed */
  def executeActionSet(battleUnit: BattleUnit): Unit =

    battleUnit.actionSet.primaryAction match
      case Move =>
        if randomNumberGenerator.nextInt(101) <= calculateSuccessProbability(battleUnit, battleUnit.actionSet.primaryTarget, Action.Move) then
          move(battleUnit, battleUnit.actionSet.primaryTarget)
      case Attack =>
        attack(battleUnit, battleUnit.actionSet.primaryTarget)
      case Defend =>
        defend(battleUnit)
      case Reload =>
        reload(battleUnit)
      case Rest =>
        refuel(battleUnit)

    if !battleUnit.actionSet.primaryActionSuccess && battleUnit.alive then
      battleUnit.actionSet.secondaryAction match
        case Move =>
          if randomNumberGenerator.nextInt(101) <= calculateSuccessProbability(battleUnit, battleUnit.actionSet.secondaryTarget, Action.Move) then
            move(battleUnit, battleUnit.actionSet.secondaryTarget)
        case Attack =>
          attack(battleUnit, battleUnit.actionSet.secondaryTarget)
        case Defend =>
          defend(battleUnit)
        case Reload =>
          reload(battleUnit)
        case Rest =>
          refuel(battleUnit)


  /** Updates the game state by executing all pending actions and clearing all selections */
  def playTurn(): Unit =

    if !gameOver && pendingActions.nonEmpty then
      for battleUnit <- pendingActions.reverse do
        executeActionSet(battleUnit)

    updateConquest()

    if player1.winProgress >= ConquestTarget || player2.winProgress >= ConquestTarget || player1.battleUnits.count(_.alive) == 0 || player2.battleUnits.count(_.alive) == 0 then
      gameOver = true
      winner = if player1.battleUnits.count(_.alive) > player2.battleUnits.count(_.alive) || player1.winProgress >= ConquestTarget then Some(player1) else Some(player2)

    turnCount += 1

    currentlyPlaying = if currentlyPlaying == player1 then player2 else player1
    
  end playTurn

end Game
