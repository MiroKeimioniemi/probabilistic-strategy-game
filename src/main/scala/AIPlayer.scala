import scalafx.scene.Scene
import scalafx.scene.layout.{BorderPane, GridPane, HBox, StackPane, VBox}
import scalafx.scene.control.Button
import scalafx.scene.{Node, Scene, layout}
import scalafx.collections.ObservableBuffer
import o1.GridPos
import scalafx.concurrent.Task
import o1.CompassDir.*

import java.io.FileInputStream
import scalafx.Includes.*
import Action.Move
import o1.grid.CompassDir
import scala.math.abs

import math.min

object AIPlayer {

  // AI Player state variables
  var controlPlayer1 = false
  var controlPlayer2 = false
  var currentlyControlling: Option[Player] = None

  /** Overload variants of select method, used to select tiles in the GUI grid */
  def select(column: Int, row: Int, grid: GridPane, game: Game): Unit =
    require(column > 0 && column <= game.gameMap.width && row > 0 && row <= game.gameMap.height)
    GUI.paneAt(column, row, grid).fireEvent(GUI.syntheticMouseClick(GUI.paneAt(column, row, grid)))

  def select(coordinates: (Int, Int), grid: GridPane, game: Game): Unit =
      require(coordinates._1 > 0 && coordinates._1 <= game.gameMap.width && coordinates._2 > 0 && coordinates._2 <= game.gameMap.height)
      GUI.paneAt(coordinates._1, coordinates._2, grid).fireEvent(GUI.syntheticMouseClick(GUI.paneAt(coordinates._1, coordinates._2, grid)))

  def select(gridPos: GridPos, grid: GridPane, game: Game): Unit =
    require(gridPos.x > 0 && gridPos.x <= game.gameMap.width && gridPos.y > 0 && gridPos.y <= game.gameMap.height)
    GUI.paneAt(gridPos.x, gridPos.y, grid).fireEvent(GUI.syntheticMouseClick(GUI.paneAt(gridPos.x, gridPos.y, grid)))


  /**  */
  def AIPlayTurn(game: Game, scene: Scene, player: Player): Unit =

    // Current game state relevant to the AI Player
    val conquestTiles = game.gameMap.tiles.filter(_.getClass == classOf[ConquestTile])
    val conqueredPositions = conquestTiles.map(_.position).intersect(player.battleUnits.map(_.position))
    val enemyPositions =
      if player == game.player1 then
        game.player2.battleUnits.filter(_.alive).map(_.position)
      else
        game.player1.battleUnits.filter(_.alive).map(_.position)

    // GUI components to be interacted with
    val grid = scene.content(0).asInstanceOf[javafx.scene.layout.BorderPane].children(0).asInstanceOf[javafx.scene.layout.GridPane]
    val rightPane = scene.content(0).asInstanceOf[javafx.scene.layout.BorderPane].children(1).asInstanceOf[javafx.scene.layout.VBox]
    val bottomPane = scene.content(0).asInstanceOf[javafx.scene.layout.BorderPane].children(2).asInstanceOf[javafx.scene.layout.HBox]

    // Prompts GUI to artificially click "Set Action Set" button
    def setActionSet(): Unit =
      rightPane.children(rightPane.children.length - 1).asInstanceOf[javafx.scene.control.Button].fireEvent(GUI.syntheticMouseClick(rightPane.children(rightPane.children.length - 1).asInstanceOf[javafx.scene.control.Button]))

    // Prompts GUI to artificially click "Play Turn" button
    def playTurn(): Unit =
      bottomPane.children(0).asInstanceOf[javafx.scene.control.Button].fireEvent(GUI.syntheticMouseClick(bottomPane.children(0).asInstanceOf[javafx.scene.control.Button]))

    /** Returns the preferred direction of advancement for the given BattleUnit, that is the direction on the axis on which the BattleUnit is the furthest away from the nearest objective */
    def objectiveDirection(battleUnit: BattleUnit): CompassDir =

      var objectiveDirection = battleUnit.orientation

      if conqueredPositions.length == conquestTiles.length then
        val closestEnemyPosition = enemyPositions.minBy(_.distance(battleUnit.position))
        val xDistance = battleUnit.position.xDiff(closestEnemyPosition)
        val yDistance = battleUnit.position.yDiff(closestEnemyPosition)
        objectiveDirection =
          if abs(yDistance) < abs(xDistance) then
            battleUnit.position.yDirectionOf(closestEnemyPosition).getOrElse(battleUnit.position.xDirectionOf(closestEnemyPosition).getOrElse(battleUnit.orientation))
          else
            battleUnit.position.xDirectionOf(closestEnemyPosition).getOrElse(battleUnit.position.yDirectionOf(closestEnemyPosition).getOrElse(battleUnit.orientation))

      else if conquestTiles.nonEmpty then
        val yetToBeConquered = conquestTiles.filter(cT => !conqueredPositions.contains(cT.position))
        val closestToBeConquered = yetToBeConquered.minBy(_.position.distance(battleUnit.position))
        objectiveDirection = battleUnit.position.xDirectionOf(closestToBeConquered.position).getOrElse(battleUnit.position.yDirectionOf(closestToBeConquered.position).getOrElse(battleUnit.orientation))

      objectiveDirection

    end objectiveDirection

    for battleUnit <- player.battleUnits.filter(_.alive) do
      // State variables for each BattleUnit's immediate surroundings
      val enemyLocationsInRange = game.tilesInRange(battleUnit, Action.Attack).map(_.position).intersect(enemyPositions)
      val enemyAttackProbabilities = game.tilesInRange(battleUnit, Action.Attack).filter(t => enemyLocationsInRange.contains(t.position)).map(t => game.calculateAttackProbability(battleUnit, t.position))
      val moveDestinations = game.tilesInRange(battleUnit, Action.Move)
      val moveProbabilities = game.tilesInRange(battleUnit, Action.Move).map(t => game.calculateMoveProbability(battleUnit, t.position))

      game.selectedPrimaryAction = Action.Move
      game.selectedSecondaryAction = Action.Move
      select(battleUnit.position, grid, game)

      if battleUnit.fuel == 0 then
        select(battleUnit.position, grid, game)
        game.selectedPrimaryAction = Action.Rest
        game.selectedSecondaryAction = Action.Rest
        select(battleUnit.position, grid, game)

      else if battleUnit.ammo == 0 then
        select(battleUnit.position, grid, game)
        game.selectedPrimaryAction = Action.Reload
        game.selectedSecondaryAction = Action.Reload
        select(battleUnit.position, grid, game)

      // If there is a sufficient chance of winning, the BattleUnit will attempt to attack that enemy in its range that it is most likely to beat
      else if enemyAttackProbabilities.exists(_ >= AIAttackProbabilityThreshold) then
        val attackTarget = enemyLocationsInRange.zip(enemyAttackProbabilities).maxBy(_._2)._1
        select(battleUnit.position, grid, game)
        game.selectedPrimaryAction = Action.Attack
        select(battleUnit.position, grid, game)
        select(attackTarget, grid, game)
        game.selectedPrimaryTile = game.gameMap.tiles.find(_.position == attackTarget)
        game.selectedSecondaryAction = Action.Attack
        select(attackTarget, grid, game)
        game.selectedSecondaryTile = game.gameMap.tiles.find(_.position == attackTarget)

      else if moveDestinations.intersect(conquestTiles).nonEmpty then
        select(battleUnit.position, grid, game)
        game.selectedPrimaryAction = Action.Move
        select(battleUnit.position, grid, game)
        select(moveDestinations.intersect(conquestTiles).head.position, grid, game)
        game.selectedSecondaryAction = Action.Move
        select(moveDestinations.intersect(conquestTiles).head.position, grid, game)

      else if moveDestinations.map(_.position).intersect(enemyPositions).nonEmpty then
        select(battleUnit.position, grid, game)
        game.selectedPrimaryAction = Action.Move
        select(battleUnit.position, grid, game)
        select(moveDestinations.map(_.position).intersect(enemyPositions).head, grid, game)
        game.selectedSecondaryAction = Action.Move
        select(moveDestinations.map(_.position).intersect(enemyPositions).head, grid, game)
      else
      // If there is a sufficiently high probability of passing, attempts to move directly towards objective
        val preferredDirection = objectiveDirection(battleUnit)
        val movePath = preferredDirection match
          case East  => moveDestinations.zip(moveProbabilities).grouped(battleUnit.range).filter(_.forall(_._1.position.x > battleUnit.position.x)).toVector.flatten
          case South => moveDestinations.zip(moveProbabilities).grouped(battleUnit.range).filter(_.forall(_._1.position.y > battleUnit.position.y)).toVector.flatten
          case West  => moveDestinations.zip(moveProbabilities).grouped(battleUnit.range).filter(_.forall(_._1.position.x < battleUnit.position.x)).toVector.flatten
          case North => moveDestinations.zip(moveProbabilities).grouped(battleUnit.range).filter(_.forall(_._1.position.y < battleUnit.position.y)).toVector.flatten
        val moveTarget = movePath.reverse.filter(_._2 >= AIMoveProbabilityThreshold).minByOption(t => abs(battleUnit.position.xDiff((t._1.position))))
        println((battleUnit.position, moveTarget.getOrElse("too bad"), movePath))
        if moveTarget.isDefined then
          select(battleUnit.position, grid, game)
          game.selectedPrimaryAction = Action.Move
          select(battleUnit.position, grid, game)
          select(moveTarget.get._1.position, grid, game)
          game.selectedSecondaryAction = Action.Move
          select(moveTarget.get._1.position, grid, game)

        else if moveTarget.isEmpty || (moveTarget.isDefined && player.battleUnits.filter(_.alive).exists(_.position == moveTarget.get._1.position)) then
          // Attempts to go right or left of the preferred direction to preserve the overall course while going around an obstacle
          val alternateDirections = preferredDirection match
            case East  => moveDestinations.zip(moveProbabilities).grouped(battleUnit.range).filter(_.forall(_._1.position.x == battleUnit.position.x)).toVector.flatten
            case South => moveDestinations.zip(moveProbabilities).grouped(battleUnit.range).filter(_.forall(_._1.position.y == battleUnit.position.y)).toVector.flatten
            case West  => moveDestinations.zip(moveProbabilities).grouped(battleUnit.range).filter(_.forall(_._1.position.x == battleUnit.position.x)).toVector.flatten
            case North => moveDestinations.zip(moveProbabilities).grouped(battleUnit.range).filter(_.forall(_._1.position.y == battleUnit.position.y)).toVector.flatten
          val sideMoveTarget = alternateDirections.filter(t => !player.battleUnits.filter(_.alive).forall(_.position == t._1.position)).sortBy(t => abs(battleUnit.position.distance(t._1.position))).take(2).map(_._1).map(_.position).padTo(2, moveDestinations(2).position)
          if sideMoveTarget.nonEmpty then
            select(battleUnit.position, grid, game)
            game.selectedPrimaryAction = Action.Move
            select(battleUnit.position, grid, game)
            select(sideMoveTarget(0), grid, game)
            game.selectedSecondaryAction = Action.Move
            select(sideMoveTarget(1), grid, game)
          else
            game.selectedPrimaryAction = Action.Defend
        else
          // Simply tries to go where the probability of success is highest
          val explorationPath = moveDestinations.zip(moveProbabilities).maxBy(_._2)._1.position
          select(battleUnit.position, grid, game)
          game.selectedPrimaryAction = Action.Move
          select(battleUnit.position, grid, game)
          select(explorationPath, grid, game)
          game.selectedSecondaryAction = Action.Move
          select(explorationPath, grid, game)

      setActionSet()
      game.selectedPrimaryAction = Action.Move
      game.selectedSecondaryAction = Action.Move
      println((battleUnit.actionSet.primaryAction, battleUnit.actionSet.primaryTarget, battleUnit.actionSet.secondaryAction, battleUnit.actionSet.secondaryTarget))
    playTurn()
}
