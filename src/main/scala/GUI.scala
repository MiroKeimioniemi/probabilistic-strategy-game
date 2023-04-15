import Action.Move
import scalafx.Includes.*
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.{Node, Scene, layout, text}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, GridPane, HBox, StackPane, VBox}
import math.min
import java.io.FileInputStream
import o1.{East, GridPos, South, West}
import o1.grid.CompassDir.North
import scalafx.beans.property.{BooleanProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.concurrent.Task
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, ChoiceBox, Label}
import scalafx.scene.input.{MouseButton, MouseEvent, PickResult}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Font, FontWeight}

object GUI extends JFXApp3:

  // Attempt to use hardware acceleration
  System.setProperty("prism.order", "sw")

  // Initialize game
  private val game = new Game

  // Initialize bound properties
  private val selectedUnitType = StringProperty(SelectedUnitDefault)
  private val turnCount        = StringProperty(game.turnCount.toString)
  private val primaryTarget    = StringProperty(PrimaryTargetSelection)
  private val secondaryTarget  = StringProperty(SecondaryTargetSelection)

  private def selectedBattleUnitPane(grid: GridPane): StackPane =
    grid.children.filter(e => GridPane.getRowIndex(e) == game.selectedBattleUnit.getOrElse(game.player1.battleUnits.head).position.y && GridPane.getColumnIndex(e) == game.selectedBattleUnit.getOrElse(game.player1.battleUnits.head).position.x)(1).asInstanceOf[javafx.scene.layout.StackPane]

  private def selectedTilePane(grid: GridPane, tile: TerrainTile): StackPane =
    grid.children.find(e => GridPane.getRowIndex(e) == tile.position.y && GridPane.getColumnIndex(e) == tile.position.x).getOrElse(grid.children.head).asInstanceOf[javafx.scene.layout.StackPane]

  private def syntheticMouseClick(target: Node): MouseEvent =
    MouseEvent(MouseEvent.MouseClicked, 0, 0, 0, 0, MouseButton.Primary, 0, false, false, false, false, false, false, false, false, false, false, new PickResult(target.delegate, target.localToScene(0, 0, 0), 0.0))

  private def actionDropdownItems: ObservableBuffer[String] =
    var showItems = ObservableBuffer[String]()
    Action.values.foreach(action => showItems = showItems :+ action.toString)
    showItems

  private def clearHighlights(grid: GridPane): Unit =
    if game.selectedBattleUnit.isDefined then
      selectedBattleUnitPane(grid).fireEvent(syntheticMouseClick(selectedBattleUnitPane(grid)))
    if game.selectedPrimaryTile.isDefined then
      selectedTilePane(grid, game.selectedPrimaryTile.get).children(1).asInstanceOf[javafx.scene.shape.Rectangle].stroke = Color.Transparent
    if game.selectedSecondaryTile.isDefined then
      selectedTilePane(grid, game.selectedSecondaryTile.get).children(1).asInstanceOf[javafx.scene.shape.Rectangle].stroke = Color.Transparent

    game.selectedBattleUnit = None
    game.selectedPrimaryTile = None
    game.selectedSecondaryTile = None
  end clearHighlights



  /** Defines and draws the layout of the Game GUI */
  def start(): Unit =

    /** Layout components */
    stage = new JFXApp3.PrimaryStage:
      title = GameTitle

    val grid = new GridPane()

    val rightPane = new VBox():
      minWidth = GameWindowWidth
      padding = LayoutInset
      spacing = DefaultSpacing

    val bottomPane = new HBox():
      minHeight = GameWindowHeight
      padding = LayoutInset
      spacing = DefaultSpacing

    val rootPane = new BorderPane():
      left = grid
      right = rightPane
      bottom = bottomPane

    val scene = new Scene(GameWindowWidth, GameWindowHeight):
      content = rootPane

    stage.scene = scene


    /** Display and control components */
    var unitLabel = new Label():
      font = HeadingFont
      text <== selectedUnitType
      margin = Insets(0, 0, 50, 0)

    var primaryActionLabel = new Label():
      font = DefaultFont
      text = PrimaryAction

    var primaryActionSelector = new HBox()

    var primaryActionDropdown = new ChoiceBox[String]():
      maxWidth = DropdownWidth
      maxHeight = DropdownHeight
      items = actionDropdownItems
      selectionModel().selectFirst()
      // Updates the currently selected action and associated GUI highlights
      onAction = () => {
          if game.selectedBattleUnit.isDefined then
            val targetPane = selectedBattleUnitPane(grid)
            targetPane.fireEvent(syntheticMouseClick(targetPane))
            game.selectedAction = Action.values.find(_.toString == value.value).getOrElse(Move)
            targetPane.fireEvent(syntheticMouseClick(targetPane))
          else
            game.selectedAction = Action.values.find(_.toString == value.value).getOrElse(Move)
      }

    var primaryActionTarget = new Label():
      font = HeadingFont
      text <== primaryTarget
      margin = DefaultLeftMargin

    var secondaryActionLabel = new Label():
      font = DefaultFont
      text = SecondaryAction

    var secondaryActionSelector = new HBox()

    var secondaryActionDropdown = new ChoiceBox[String]():
      maxWidth = DropdownWidth
      maxHeight = DropdownHeight
      items = actionDropdownItems
      selectionModel().selectFirst()
      // Updates the currently selected action and associated GUI highlights
      onAction = () => {
//          val targetPane = selectedBattleUnitPane(grid)
//          targetPane.fireEvent(syntheticMouseClick(targetPane))
//          game.selectedAction = Action.values.find(_.toString == value.value).getOrElse(Move)
//          targetPane.fireEvent(syntheticMouseClick(targetPane))
      }

    var secondaryActionTarget = new Label():
      font = HeadingFont
      text <== secondaryTarget
      margin = DefaultLeftMargin

    var setActionSetButton = new Button():
      font = HeadingFont
      text = SetActionSetButton
      margin = Insets(50, 0, 0, 0)

    // Sets the ActionSet of the currently selected BattleUnit according to other current selections and indicates the
    // success of this by adding a done symbol to the upper right corner of the BattleUnit whose AcionSet was set
    setActionSetButton.onMouseClicked = (event: MouseEvent) => {
      if game.selectedBattleUnit.isDefined then
        game.selectedBattleUnit.get.setActionSet(game.selectedAction, game.selectedPrimaryTile.getOrElse(game.gameMap.tiles.filter(_.position == game.selectedBattleUnit.get.position)).asInstanceOf[TerrainTile].position)
        game.pendingActions = game.pendingActions :+ game.selectedBattleUnit.get
        grid.add(drawPic("src/main/resources/done-symbol.png", scene), game.selectedBattleUnit.get.position.x, game.selectedBattleUnit.get.position.y)
        clearHighlights(grid)
    }

    var turnCounter = new Label():
          font = HeadingFont
          text <== turnCount

    var playTurnButton = new Button():
      font = HeadingFont
      text = PlayTurnButton

    // Resets all selections, invokes the PlayTurn method of Game and refreshes the GUI
    playTurnButton.onMouseClicked = (event: MouseEvent) => {
      clearHighlights(grid)

      game.playTurn()

      game.pendingActions = Vector()
      turnCount.value = game.turnCount.toString

      grid.children.removeRange(MapWidth * MapHeight, grid.children.length)
      drawBattleUnits(scene, game.player1)
    }

    // Builds the GUI layout from the components specified above
    primaryActionSelector.children.addAll(primaryActionDropdown, primaryActionTarget)
    secondaryActionSelector.children.addAll(secondaryActionDropdown, secondaryActionTarget)
    rightPane.children.addAll(unitLabel, primaryActionLabel, primaryActionSelector, secondaryActionLabel, secondaryActionSelector, setActionSetButton)
    bottomPane.children.addAll(playTurnButton, turnCounter)

    // Initializes the game grid
    drawMapTiles(scene)
    drawBattleUnits(scene, game.player1)

    stage.show()

  end start



  /** Returns an ImageView object corresponding to a given image path, scaled with respect to the game scene */
  private def drawPic(pic: String, gameScene: Scene): ImageView =
    val imageView = new ImageView(new Image(FileInputStream(pic))) {
      fitWidth <== ((gameScene.widthProperty()) / MapWidth) - (RightPaneWidth / MapWidth)
      fitHeight <== ((gameScene.widthProperty()) / MapWidth) - (RightPaneWidth / MapWidth)
      maxHeight((gameScene.widthProperty() / MapWidth).toDouble)
      preserveRatio = true
    }
    imageView
  end drawPic

  /** Adds StackPane objects to a grid at specified positions */
  private def displayInGrid(drawn: Vector[StackPane], positions: Vector[GridPos], grid: GridPane) =
    for element <- drawn zip positions do
      grid.add(element._1, element._2.x, element._2.y)
  end displayInGrid


  /** Displays selectable images corresponding to the map tiles in the GUI */
  private def drawMapTiles(scene: Scene): Unit =

    val tiles: Vector[TerrainTile] = game.gameMap.tiles
    val positions: Vector[GridPos] = tiles.map(_.position)
    var drawn: Vector[StackPane] = Vector[StackPane]()

    val grid = scene.content(0).asInstanceOf[javafx.scene.layout.BorderPane].children(0).asInstanceOf[javafx.scene.layout.GridPane]

    /** Returns a StackPane containing the image of the tile, an initially transparent border and
     * a mouse click event listener that toggles the highlighting (border) of the tile */
    def selectableTiles(tile: TerrainTile): StackPane =

      val image = drawPic(tile.image, scene)
      val border = new Rectangle {
        width <== image.fitWidth - strokeWidth
        height <== image.fitHeight - strokeWidth
        strokeWidth = SelectionRectangleThickness
        stroke = Color.Transparent
        fill = Color.Transparent
      }

      val selectable = StackPane()
      selectable.children.addAll(image, border)

      // Selects and highlights the clicked tile if it is in the range of the selected battle unit's selected action
      selectable.onMouseClicked = (event: MouseEvent) => {

        def highlightTile() =
          border.stroke = HighlightColor
          game.selectedPrimaryTile = Some(tile)
          primaryTarget.value = tile.position.toString

        game.selectedPrimaryTile match
          case Some(sPT) =>
            selectedTilePane(grid, sPT).children(3).asInstanceOf[javafx.scene.shape.Rectangle].stroke = Color.Transparent
            game.selectedPrimaryTile = None
            primaryTarget.value = PrimaryTargetSelection
            if sPT != tile && game.tilesInRange(game.selectedBattleUnit.get).contains(tile) then
              highlightTile()
          case None =>
            if game.selectedBattleUnit.isDefined && game.tilesInRange(game.selectedBattleUnit.get).contains(tile) then
              highlightTile()

        event.consume()
      }
      selectable

    end selectableTiles

    // Builds selectable tiles and displays them in grid
    tiles.foreach(drawable => drawn = drawn :+ selectableTiles(drawable))
    displayInGrid(drawn, positions, grid)

  end drawMapTiles

  /** Displays selectable images corresponding to the battle units in the GUI */
  private def drawBattleUnits(scene: Scene, player: Player): Unit =

    val battleUnits: Vector[BattleUnit] = player.battleUnits
    val positions: Vector[GridPos] = battleUnits.map(_.position)
    var drawn: Vector[StackPane] = Vector[StackPane]()

    val grid = scene.content(0).asInstanceOf[javafx.scene.layout.BorderPane].children(0).asInstanceOf[javafx.scene.layout.GridPane]

    /** Returns a StackPane containing the image of the battle unit, an initially transparent border
     * and a mouse click event listener that toggles the highlighting (border) of the BattleUnit and
     * the highlightTiles in the Game class associated with it */
    def selectableBattleUnit(battleUnit: BattleUnit): StackPane =

      val image = drawPic(battleUnit.image, scene)
      val border = new Rectangle {
        width <== image.fitWidth - strokeWidth
        height <== image.fitHeight - strokeWidth
        strokeWidth = SelectionRectangleThickness
        stroke = Color.Transparent
        fill = Color.Transparent
      }
      val selectable = new StackPane()
      selectable.children.addAll(image, border)

      // Attaches a mouse click event listener to each BattleUnit that toggles the highlighting of that
      // unit and the tiles in its field of view and adds them to the selectedBattleUnits and
      // selectedTiles vectors in the Game object such that selectedBattleUnits can contain only a
      // single BattleUnit at a time
      selectable.onMouseClicked = (event: MouseEvent) => {

        def highlightBattleUnit() =

          // Selects a BattleUnit
          game.selectedBattleUnit = Some(battleUnit)
          border.stroke = BattleUnitHighlightColor

          // Adds colored rectangles and probability labels between the image and the transparent
          // highlight rectangle to each tile within the field of view of the selected BattleUnit
          for tile <- game.tilesInRange(battleUnit) do
            val highlight = new Rectangle {
              width <== image.fitWidth - strokeWidth
              height <== image.fitHeight - strokeWidth
              strokeWidth = SelectionRectangleThickness
              stroke = BattleUnitHighlightColor
              fill = Color.Transparent
            }
            val moveProbability = new Label() {
              font = HeadingFont
              textFill = BattleUnitHighlightColor
              text = game.calculateSuccessProbability(battleUnit, tile).toString
            }

            selectedTilePane(grid, tile).children.add(1, highlight)
            selectedTilePane(grid, tile).children.add(2, moveProbability)

            // Updates unit selection label with the type of the selected unit
            selectedUnitType.value = battleUnit.unitType

        end highlightBattleUnit

        game.selectedBattleUnit match
          case Some(sBU) =>
            if sBU == battleUnit then

              // Deselects the BattleUnit and selected tiles
              border.stroke = Color.Transparent
              if game.selectedPrimaryTile.isDefined then
                selectedTilePane(grid, game.selectedPrimaryTile.get).fireEvent(syntheticMouseClick(selectedTilePane(grid, game.selectedPrimaryTile.get)))
              game.selectedBattleUnit = None

              // Removes the colored rectangles and probability indicators between the image and the
              // transparent highlight rectangle from each tile associated with the BattleUnit
              for tile <- game.tilesInRange(battleUnit) do
                selectedTilePane(grid, tile).children.remove(2)
                selectedTilePane(grid, tile).children.remove(1)

              selectedUnitType.value = SelectedUnitDefault

            if sBU != battleUnit then

              if game.selectedPrimaryTile.isDefined then
                  selectedTilePane(grid, game.selectedPrimaryTile.get).fireEvent(syntheticMouseClick(selectedTilePane(grid, game.selectedPrimaryTile.get)))

              // Returns the previously selected BattleUnit and it's associated tiles to their unselected states
              if game.selectedBattleUnit.isDefined then
                for tile <- game.tilesInRange(sBU) do
                  selectedTilePane(grid, tile).children.remove(2)
                  selectedTilePane(grid, tile).children.remove(1)

                selectedBattleUnitPane(grid).children(1).asInstanceOf[javafx.scene.shape.Rectangle].stroke = Color.Transparent

                game.selectedBattleUnit = None

                highlightBattleUnit()

          case None =>

            highlightBattleUnit()

        event.consume()
      }

      selectable

    end selectableBattleUnit

    // Displays BattleUnits on the grid with correct orientations
    battleUnits.foreach(battleUnit => drawn = drawn :+ selectableBattleUnit(battleUnit))
    battleUnits.map(_.orientation).zipWithIndex.foreach(o => drawn(o._2).rotate = o._1 match
      case North => -90
      case East  => 0
      case South => 90
      case West  => 180
      case _     => 0)
    displayInGrid(drawn, positions, grid)

  end drawBattleUnits

end GUI
