import Action.Move
import javafx.scene.layout.{ColumnConstraints, RowConstraints}
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
  private val selectedUnitType       = StringProperty(SelectedUnitDefault)
  private val turnCount              = StringProperty(game.turnCount.toString)
  private val primaryTarget          = StringProperty(PrimaryTargetSelection)
  private val secondaryTarget        = StringProperty(SecondaryTargetSelection)
  private val selectedUnitHealth     = StringProperty(Health)
  private val selectedUnitExperience = StringProperty(Experience)
  private val selectedUnitAmmo       = StringProperty(Ammo)
  private val selectedUnitFuel       = StringProperty(Fuel)

  // Utility functions
  private def selectedBattleUnitPane(grid: GridPane): StackPane =
    grid.children.filter(e => GridPane.getRowIndex(e) == game.selectedBattleUnit.getOrElse(game.currentlyPlaying.battleUnits.head).position.y && GridPane.getColumnIndex(e) == game.selectedBattleUnit.getOrElse(game.currentlyPlaying.battleUnits.head).position.x)(1).asInstanceOf[javafx.scene.layout.StackPane]

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

  private def refreshSelection(grid: GridPane, updateAction: Action): Unit =

    val setSecondaryAction = game.selectingSecondaryTarget

    // Updates the primary or secondary action, depending on which is being selected and refreshes the highlighting
    // in the grid appropriately by toggling it off and on with the action updated in between
    def refresh() =
      val targetPane = selectedBattleUnitPane(grid)
      targetPane.fireEvent(syntheticMouseClick(targetPane))
      if !setSecondaryAction then
        game.selectedPrimaryAction = Action.values.find(_ == updateAction).getOrElse(Move)
      else
        game.selectedSecondaryAction = Action.values.find(_ == updateAction).getOrElse(Move)
      targetPane.fireEvent(syntheticMouseClick(targetPane))

    if game.selectedBattleUnit.isDefined then
      // Simple, full refresh if selecting primary target
      if !setSecondaryAction then
        refresh()
      // Partial refresh, preserving primary target selection if selecting secondary target
      else
        var primaryTargetTile: Option[StackPane] = None
        if game.selectedPrimaryTile.isDefined then
          primaryTargetTile = Some(selectedTilePane(grid, game.selectedPrimaryTile.get))

        refresh()

        if primaryTargetTile.isDefined && !(game.selectedPrimaryAction == Action.Stay || game.selectedPrimaryAction == Action.Defend) then
          game.selectingSecondaryTarget = false
          primaryTargetTile.get.fireEvent(syntheticMouseClick(primaryTargetTile.get))
    // Updates the primary or secondary action, depending on which is being selected
    else
      if !setSecondaryAction then
        game.selectedPrimaryAction = Action.values.find(_ == updateAction).getOrElse(Move)
      else
        game.selectedSecondaryAction = Action.values.find(_ == updateAction).getOrElse(Move)

  end refreshSelection



  /** Defines and draws the layout of the Game GUI */
  def start(): Unit =

    /** Layout components */
    stage = new JFXApp3.PrimaryStage:
      title = GameTitle

    val grid = new GridPane()

    val rightPane = new VBox():
      style    = RightPaneBackgroundStyle
      minWidth = GameWindowWidth
      padding  = LayoutInset
      spacing  = DefaultSpacing

    val bottomPane = new HBox():
      style     = BottomPaneBacgroundStyle
      minHeight = GameWindowHeight
      padding   = LayoutInset
      spacing   = DefaultSpacing

    val rootPane = new BorderPane():
      left   = grid
      right  = rightPane
      bottom = bottomPane

    val scene = new Scene(GameWindowWidth, GameWindowHeight):
      content = rootPane

    stage.scene = scene


    /** Display and control components */
    var unitLabel = new Label():
      font = HeadingFont
      text <== selectedUnitType
      margin = Insets(0, 0, (DefaultSpacing / 2), 0)
    unitLabel.setTextFill(TextColor)

    var infoGrid = new GridPane()
    infoGrid.columnConstraints.addAll(ColumnConstraints(150), ColumnConstraints(150))
    infoGrid.rowConstraints.addAll(RowConstraints(50), RowConstraints(50))

    var healthLabel = new Label():
      font = HeadingFont
      text <== selectedUnitHealth
    healthLabel.setTextFill(TextColor)

    var experienceLabel = new Label():
      font = HeadingFont
      text <== selectedUnitExperience
    experienceLabel.setTextFill(TextColor)

    var ammoLabel = new Label():
      font = HeadingFont
      text <== selectedUnitAmmo
    ammoLabel.setTextFill(TextColor)

    var fuelLabel = new Label():
      font = HeadingFont
      text <== selectedUnitFuel
    fuelLabel.setTextFill(TextColor)

    infoGrid.add(healthLabel, 0, 0)
    infoGrid.add(experienceLabel, 1, 0)
    infoGrid.add(ammoLabel, 0, 1)
    infoGrid.add(fuelLabel, 1, 1)

    var primaryActionLabel = new Label():
      font = DefaultFont
      text = PrimaryAction
      margin = Insets(DefaultSpacing, 0, 0, 0)
    primaryActionLabel.setTextFill(TextColor)

    var primaryActionSelector = new HBox()

    var primaryActionDropdown = new ChoiceBox[String]():
      maxWidth = DropdownWidth
      maxHeight = DropdownHeight
      items = actionDropdownItems
      selectionModel().selectFirst()
      onMouseClicked = () => {
        game.selectingSecondaryTarget = false
        refreshSelection(grid, Action.values.find(_.toString == value.value).getOrElse(Move))
      }
      // Updates the currently selected action and associated GUI highlights
      onAction = () => {
        game.selectingSecondaryTarget = false
        refreshSelection(grid, Action.values.find(_.toString == value.value).getOrElse(Move))
      }

    var primaryActionTarget = new Label():
      font = HeadingFont
      text <== primaryTarget
      margin = DefaultLeftMargin
    primaryActionTarget.setTextFill(TextColor)

    var secondaryActionLabel = new Label():
      font = DefaultFont
      text = SecondaryAction
    secondaryActionLabel.setTextFill(TextColor)

    var secondaryActionSelector = new HBox()

    var secondaryActionDropdown = new ChoiceBox[String]():
      maxWidth = DropdownWidth
      maxHeight = DropdownHeight
      items = actionDropdownItems
      disable = true
      selectionModel().selectFirst()
      // Updates the currently selected action and associated GUI highlights
      onAction = () => {
        game.selectingSecondaryTarget = true
        refreshSelection(grid, Action.values.find(_.toString == value.value).getOrElse(Move))
      }

    var secondaryActionTarget = new Label():
      font = HeadingFont
      text <== secondaryTarget
      margin = DefaultLeftMargin
    secondaryActionTarget.setTextFill(TextColor)

    var setActionSetButton = new Button():
      font = HeadingFont
      text = SetActionSetButton
      margin = Insets((DefaultSpacing * 2), 0, 0, 0)

    // Sets the ActionSet of the currently selected BattleUnit according to other current selections and indicates the
    // success of this by adding a done symbol to the upper right corner of the BattleUnit whose AcionSet was set
    setActionSetButton.onMouseClicked = (event: MouseEvent) => {
      if game.selectedBattleUnit.isDefined then
        game.selectedBattleUnit.get.setActionSet(
          game.selectedPrimaryAction,
          game.selectedPrimaryTile.getOrElse(game.gameMap.tiles.filter(_.position == game.selectedBattleUnit.get.position).head).position,
          game.selectedSecondaryAction,
          game.selectedSecondaryTile.getOrElse(game.gameMap.tiles.filter(_.position == game.selectedBattleUnit.get.position).head).position
        )
        game.pendingActions = game.pendingActions :+ game.selectedBattleUnit.get
        clearHighlights(grid)
    }

    var playTurnButton = new Button():
      font = HeadingFont
      text = PlayTurnButton

    var turnCounter = new Label():
      font = HeadingFont
      text <== turnCount
    turnCounter.setTextFill(TextColor)
    turnCounter.setPrefWidth(CounterWidth)

    var player1WinProgress = new Label():
      font = HeadingFont
      text <== StringProperty(Player1Score + game.player1.winProgress.toString)
      margin = DefaultLeftMargin
    player1WinProgress.setTextFill(TextColor)
    player1WinProgress.setPrefWidth(CounterWidth)

    var player2WinProgress = new Label():
      font = HeadingFont
      text <== StringProperty(Player2score + game.player2.winProgress.toString)
      margin = DefaultLeftMargin
    player2WinProgress.setTextFill(TextColor)
    player2WinProgress.setPrefWidth(CounterWidth)

    // Resets all selections, invokes the PlayTurn method of Game and refreshes the GUI
    playTurnButton.onMouseClicked = (event: MouseEvent) => {
      clearHighlights(grid)

      game.playTurn()

      updateMapTiles(scene)
      game.pendingActions = Vector()
      turnCount.value = game.turnCount.toString

      grid.children.removeRange(MapWidth * MapHeight, grid.children.length)
      drawBattleUnits(scene, game.player1)
      drawBattleUnits(scene, game.player2)
    }

    // Builds the GUI layout from the components specified above
    primaryActionSelector.children.addAll(primaryActionDropdown, primaryActionTarget)
    secondaryActionSelector.children.addAll(secondaryActionDropdown, secondaryActionTarget)
    rightPane.children.addAll(unitLabel, infoGrid, primaryActionLabel, primaryActionSelector, secondaryActionLabel, secondaryActionSelector, setActionSetButton)
    bottomPane.children.addAll(playTurnButton, turnCounter, player1WinProgress, player2WinProgress)

    // Initializes the game grid
    drawMapTiles(scene)
    drawBattleUnits(scene, game.player1)
    drawBattleUnits(scene, game.player2)

    stage.show()

  end start



  /** Returns an ImageView object corresponding to a given image path, scaled with respect to the game scene */
  private def drawPic(pic: String, gameScene: Scene, rotation: Int): ImageView =
    val imageView = new ImageView(new Image(FileInputStream(pic))) {
      fitWidth <== ((gameScene.widthProperty()) / MapWidth) - (RightPaneWidth / MapWidth)
      fitHeight <== ((gameScene.widthProperty()) / MapWidth) - (RightPaneWidth / MapWidth)
      maxHeight((gameScene.widthProperty() / MapWidth).toDouble)
      preserveRatio = true
    }
    imageView.rotate = rotation
    imageView
  end drawPic

  /** Adds StackPane objects to a grid at specified positions */
  private def displayInGrid(drawn: Vector[StackPane], positions: Vector[GridPos], grid: GridPane) =
    for element <- drawn zip positions do
      grid.add(element._1, element._2.x, element._2.y)
  end displayInGrid


  /** Updates attacked tiles' images based on which tiles are targeted by the Attack action in pendingActions */
  private def updateMapTiles(scene: Scene) =
    val grid = scene.content(0).asInstanceOf[javafx.scene.layout.BorderPane].children(0).asInstanceOf[javafx.scene.layout.GridPane]
    val updateableTiles = game.pendingActions.filter(_.actionSet.primaryAction == Action.Attack).map(_.actionSet.primaryTarget).map(l => game.gameMap.tiles.filter(_.position == l).head) ++ game.pendingActions.filter(_.actionSet.secondaryAction == Action.Attack).map(_.actionSet.secondaryTarget).map(l => game.gameMap.tiles.filter(_.position == l).head)
    val updateableImages = updateableTiles.map(t => drawPic(t.image, scene, 0))
    val updateablePositions = updateableTiles.map(_.position)
    for tile <- updateablePositions do
      grid.children.filter(GridPane.getRowIndex(_) == tile.y).find(GridPane.getColumnIndex(_) == tile.x).get.asInstanceOf[javafx.scene.layout.StackPane].children.remove(0)
      grid.children.filter(GridPane.getRowIndex(_) == tile.y).find(GridPane.getColumnIndex(_) == tile.x).get.asInstanceOf[javafx.scene.layout.StackPane].children.add(0, updateableImages(updateablePositions.indexOf(tile)))


  /** Displays selectable images corresponding to the map tiles in the GUI */
  private def drawMapTiles(scene: Scene): Unit =

    val tiles: Vector[TerrainTile] = game.gameMap.tiles
    val positions: Vector[GridPos] = tiles.map(_.position)
    var drawn: Vector[StackPane] = Vector[StackPane]()

    val grid = scene.content(0).asInstanceOf[javafx.scene.layout.BorderPane].children(0).asInstanceOf[javafx.scene.layout.GridPane]

    /** Returns a StackPane containing the image of the tile, an initially transparent border and
     * a mouse click event listener that toggles the highlighting (border) of the tile */
    def selectableTiles(tile: TerrainTile): StackPane =

      val image = drawPic(tile.image, scene, 0)
      val primaryBorder = new Rectangle {
        width <== image.fitWidth - strokeWidth
        height <== image.fitHeight - strokeWidth
        strokeWidth <== ((image.fitWidth) / 100) * SelectionRectangleThickness
        stroke = Color.Transparent
        fill = Color.Transparent
      }
      val secondaryBorder = new Rectangle {
        width <== image.fitWidth - strokeWidth
        height <== image.fitHeight - strokeWidth
        strokeWidth <== ((image.fitWidth) / 100) * SelectionRectangleThickness
        stroke = Color.Transparent
        fill = Color.Transparent
      }

      val selectable = StackPane()
      selectable.children.addAll(image, primaryBorder, secondaryBorder)

      // Selects and highlights the clicked tile if it is in the range of the selected battle unit's selected action
      selectable.onMouseClicked = (event: MouseEvent) => {

        val secondaryActionDropdown = scene.content(0).asInstanceOf[javafx.scene.layout.BorderPane].children(1).asInstanceOf[javafx.scene.layout.VBox].children(5).asInstanceOf[javafx.scene.layout.HBox].children(0).asInstanceOf[javafx.scene.control.ChoiceBox[String]]

        def highlightPrimaryTile() =
          primaryBorder.stroke = PrimaryHighlightColor
          game.selectedPrimaryTile = Some(tile)
          primaryTarget.value = tile.position.toString

        def highlightSecondaryTile() =
          secondaryBorder.stroke = SecondaryHighlightColor
          game.selectedSecondaryTile = Some(tile)
          secondaryTarget.value = tile.position.toString

        // Selects or deselects secondary action
        if game.selectedBattleUnit.isDefined && game.tilesInRange(game.selectedBattleUnit.get, game.selectedSecondaryAction).contains(tile) && game.selectingSecondaryTarget then
          game.selectedSecondaryTile match
            case Some(sPT) =>
              if game.selectingSecondaryTarget then
                selectedTilePane(grid, sPT).children(selectedTilePane(grid, sPT).children.length - 1).asInstanceOf[javafx.scene.shape.Rectangle].stroke = Color.Transparent
                game.selectedSecondaryTile = None
                secondaryTarget.value = PrimaryTargetSelection
                if sPT != tile && game.tilesInRange(game.selectedBattleUnit.get, game.selectedSecondaryAction).contains(tile) then
                  highlightSecondaryTile()
            case None =>
                highlightSecondaryTile()

        // Selects or deselects primary action
        else if game.selectedBattleUnit.isDefined && game.tilesInRange(game.selectedBattleUnit.get, game.selectedPrimaryAction).contains(tile) && !game.selectingSecondaryTarget then
          game.selectedPrimaryTile match
            case Some(sPT) =>
              selectedTilePane(grid, sPT).children(selectedTilePane(grid, sPT).children.length - 2).asInstanceOf[javafx.scene.shape.Rectangle].stroke = Color.Transparent
              game.selectedPrimaryTile = None
              primaryTarget.value = PrimaryTargetSelection
              secondaryActionDropdown.disable = true
              if sPT != tile && game.tilesInRange(game.selectedBattleUnit.get, game.selectedPrimaryAction).contains(tile) then
                val targetPane = selectedBattleUnitPane(grid)
                targetPane.fireEvent(syntheticMouseClick(targetPane))
                game.selectingSecondaryTarget = true
                targetPane.fireEvent(syntheticMouseClick(targetPane))
                highlightPrimaryTile()
                // Enable and bring focus to secondary action dropdown
                secondaryActionDropdown.disable = false
                secondaryActionDropdown.requestFocus()
            case None =>
              // Upon selection of primary action, automatically moves on to selecting the secondary action and refreshes grid highlights to show that
              val targetPane = selectedBattleUnitPane(grid)
              targetPane.fireEvent(syntheticMouseClick(targetPane))
              game.selectingSecondaryTarget = true
              targetPane.fireEvent(syntheticMouseClick(targetPane))
              highlightPrimaryTile()
              // Enable and bring focus to secondary action dropdown
              secondaryActionDropdown.disable = false
              secondaryActionDropdown.requestFocus()

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

      val orientation = battleUnit.orientation match
        case North => -90
        case East  => 0
        case South => 90
        case West  => 180
        case _     => 0

      val image = drawPic(battleUnit.image, scene, orientation)
      val border = new Rectangle {
        width <== image.fitWidth - strokeWidth
        height <== image.fitHeight - strokeWidth
        strokeWidth <== ((image.fitWidth) / 100) * SelectionRectangleThickness
        stroke = Color.Transparent
        fill = Color.Transparent
      }
      val secondBorder = new Rectangle {
        width <== image.fitWidth - strokeWidth
        height <== image.fitHeight - strokeWidth
        strokeWidth <== ((image.fitWidth) / 100) * SelectionRectangleThickness
        stroke = Color.Transparent
        fill = Color.Transparent
      }
      val healthBarBackground = new Rectangle {
        width <== (image.fitWidth - strokeWidth) / 1.4
        height <== (image.fitHeight - strokeWidth) / 40
        margin = Insets(0, 0, ((image.fitHeight.value - strokeWidth.value) / 10), (((image.fitWidth.value - strokeWidth.value * 1.0) - ((image.fitWidth.value - strokeWidth.value * 1.0) / 1.4)) / 2))
        arcWidth = HealthBarRounding
        arcHeight = HealthBarRounding
        strokeWidth <== ((image.fitWidth) / 100) * SelectionRectangleThickness
        stroke = Color.DarkGrey
        fill = Color.DarkGrey
        alignmentInParent = javafx.geometry.Pos.BOTTOM_LEFT
      }
      val healthBar = new Rectangle {
        width <== (((image.fitWidth - strokeWidth) / 1.4) * (battleUnit.health * 1.0 / battleUnit.maxHealth))
        height <== (image.fitHeight - strokeWidth) / 40
        margin = Insets(0, 0, ((image.fitHeight.value - strokeWidth.value) / 10), (((image.fitWidth.value - strokeWidth.value * 1.0) - ((image.fitWidth.value - strokeWidth.value * 1.0) / 1.4)) / 2))
        arcWidth = HealthBarRounding
        arcHeight = HealthBarRounding
        strokeWidth <== ((image.fitWidth) / 100) * SelectionRectangleThickness
        stroke = if (battleUnit.health * 1.0 / battleUnit.maxHealth) <= HealthBarCriticalThreshold then HealthBarCriticalColor else HealthBarHealthyColor
        fill = if (battleUnit.health * 1.0 / battleUnit.maxHealth) <= HealthBarCriticalThreshold then HealthBarCriticalColor else HealthBarHealthyColor
        alignmentInParent = javafx.geometry.Pos.BOTTOM_LEFT
      }

      val selectable = new StackPane()
      if battleUnit.alive then
        selectable.children.addAll(image, border, healthBarBackground, healthBar)
      else
        selectable.children.addAll(image, border)

      // Allows selecting only currently playing player's BattleUnits
      if !game.currentlyPlaying.battleUnits.contains(battleUnit) then
        selectable.setMouseTransparent(true)
      else
        selectable.setMouseTransparent(false)

      // Attaches a mouse click event listener to each BattleUnit that toggles the highlighting of that
      // unit and the tiles in its field of view and adds them to the selectedBattleUnits and
      // selectedTiles vectors in the Game object such that selectedBattleUnits can contain only a
      // single BattleUnit at a time
      selectable.onMouseClicked = (event: MouseEvent) => {

        // Deselects
        if game.selectedPrimaryTile.isDefined && game.selectedBattleUnit.isDefined && game.selectedPrimaryTile.get == game.gameMap.tiles.filter(_.position == game.selectedBattleUnit.get.position).head then
          primaryTarget.value = PrimaryTargetSelection
        if game.selectedSecondaryTile.isDefined && game.selectedBattleUnit.isDefined && game.selectedSecondaryTile.get == game.gameMap.tiles.filter(_.position == game.selectedBattleUnit.get.position).head then
          secondaryTarget.value = SecondaryTargetSelection

        val secondaryActionDropdown = scene.content(0).asInstanceOf[javafx.scene.layout.BorderPane].children(1).asInstanceOf[javafx.scene.layout.VBox].children(5).asInstanceOf[javafx.scene.layout.HBox].children(0).asInstanceOf[javafx.scene.control.ChoiceBox[String]]
        val primaryActionDropdown = scene.content(0).asInstanceOf[javafx.scene.layout.BorderPane].children(1).asInstanceOf[javafx.scene.layout.VBox].children(3).asInstanceOf[javafx.scene.layout.HBox].children(0).asInstanceOf[javafx.scene.control.ChoiceBox[String]]

        def highlightBattleUnit() =

          // Selects a BattleUnit
          game.selectedBattleUnit = Some(battleUnit)

          // Highlights BattleUnit with approprieate color for the Action chosen. As the tile on which the BattleUnit is does not
          // get highlighted separately, primary action selection has to be replicated here in the case of Stay and Defend actions
          if (game.selectedPrimaryAction == Action.Stay || game.selectedPrimaryAction == Action.Defend) then
            border.stroke = PrimaryHighlightColor
            primaryTarget.value = game.gameMap.tiles.filter(_.position == battleUnit.position).head.position.toString
            game.selectedPrimaryTile = Some(game.gameMap.tiles.filter(_.position == battleUnit.position).head)
            game.selectingSecondaryTarget = true
            // Enable and bring focus to secondary action dropdown
            secondaryActionDropdown.disable = false
            secondaryActionDropdown.requestFocus()
            // If both actions are Stay or Defend, highlights BattleUnit tile with secondary highlight color
            if (game.selectedSecondaryAction == Action.Stay || game.selectedSecondaryAction == Action.Defend) then
              border.stroke = SecondaryHighlightColor
              game.selectedSecondaryTile = Some(game.gameMap.tiles.filter(_.position == battleUnit.position).head)
              secondaryTarget.value = game.gameMap.tiles.filter(_.position == battleUnit.position).head.position.toString
          // If secondary action is Stay or Defend, highlights it with secondary highlight color and changes to select primary target
          else if (game.selectedSecondaryAction == Action.Stay || game.selectedSecondaryAction == Action.Defend) then
            border.stroke = SecondaryHighlightColor
            game.selectedSecondaryTile = Some(game.gameMap.tiles.filter(_.position == battleUnit.position).head)
            secondaryTarget.value = game.gameMap.tiles.filter(_.position == battleUnit.position).head.position.toString
            game.selectingSecondaryTarget = false
          else
            border.stroke = BattleUnitHighlightColor

          // Adds colored rectangles and probability labels between the image and the transparent
          // highlight rectangle to each tile within the field of view of the selected BattleUnit's
          // selected primary action
          for tile <- game.tilesInRange(battleUnit, game.selectedPrimaryAction) do
            val primaryHighlight = new Rectangle {
              width <== image.fitWidth - strokeWidth
              height <== image.fitHeight - strokeWidth
              strokeWidth <== ((image.fitWidth) / 100) * SelectionRectangleThickness
              stroke = BattleUnitHighlightColor
              fill = Color.Transparent
            }
            val actionSuccessProbability = new Label() {
              font = HeadingFont
              textFill = BattleUnitHighlightColor
              text = game.calculateSuccessProbability(battleUnit, tile.position, game.selectedPrimaryAction).toString
            }
            if !(game.selectedPrimaryAction == Action.Stay || game.selectedPrimaryAction == Action.Defend) then
              selectedTilePane(grid, tile).children.add(1, primaryHighlight)
              selectedTilePane(grid, tile).children.add(2, actionSuccessProbability)
            // If there is another BattleUnit in range, displays the probability of beating it in battle by adding a stackpane with that probability on top of it
            if game.selectedPrimaryAction == Action.Attack && (game.currentlyPlaying == game.player2 && game.player1.battleUnits.exists(_.position == tile.position) || game.currentlyPlaying == game.player1 && game.player2.battleUnits.exists(_.position == tile.position)) then
              val winProbability = new Label() {
                font = HeadingFont
                textFill = PrimaryHighlightColor
                text = game.calculateSuccessProbability(battleUnit, tile.position, game.selectedPrimaryAction).toString
              }
              val winProbabilityContainer = new StackPane() {
                children = winProbability
                mouseTransparent = true
              }
              displayInGrid(Vector(winProbabilityContainer), Vector(tile.position), grid)

          // Adds colored rectangles and probability labels between the image and the transparent
          // highlight rectangle to each tile within the field of view of the selected BattleUnit's
          // selected secondary action
          for tile <- game.tilesInRange(battleUnit, game.selectedSecondaryAction) do
            val secondaryHighlight = new Rectangle {
              width <== image.fitWidth - strokeWidth
              height <== image.fitHeight - strokeWidth
              strokeWidth <== ((image.fitWidth) / 100) * SelectionRectangleThickness
              stroke = if game.selectingSecondaryTarget then Color.Cyan else Color.Transparent
              fill = Color.Transparent
            }
            val moveProbability = new Label() {
              font = HeadingFont
              textFill = if game.selectingSecondaryTarget then Color.Cyan else Color.Transparent
              text = game.calculateSuccessProbability(battleUnit, tile.position, game.selectedSecondaryAction).toString
            }
            // If tiles in range of secondary action overlap with tiles in range of primary action,
            // the formers' highlights are replaced with the latters'. If not, they are simply
            // placed on top of the image and the transparent highlight rectangle
            if !(game.selectedPrimaryAction == Action.Stay || game.selectedPrimaryAction == Action.Defend) then
              if game.tilesInRange(battleUnit, game.selectedPrimaryAction).contains(tile) && game.selectingSecondaryTarget then
                selectedTilePane(grid, tile).children.remove(2)
                selectedTilePane(grid, tile).children.remove(1)
                selectedTilePane(grid, tile).children.add(1, secondaryHighlight)
                selectedTilePane(grid, tile).children.add(2, moveProbability)
              else
                selectedTilePane(grid, tile).children.add(1, secondaryHighlight)
                selectedTilePane(grid, tile).children.add(2, moveProbability)
            else
              selectedTilePane(grid, tile).children.add(1, secondaryHighlight)
              selectedTilePane(grid, tile).children.add(2, moveProbability)
            // If there is another BattleUnit in range, displays the probability of beating it in battle by adding a stackpane with that probability on top of it
            if (game.selectingSecondaryTarget && game.selectedSecondaryAction == Action.Attack) && (game.currentlyPlaying == game.player2 && game.player1.battleUnits.exists(_.position == tile.position) || game.currentlyPlaying == game.player1 && game.player2.battleUnits.exists(_.position == tile.position)) then
              val winProbability = new Label() {
                font = HeadingFont
                textFill = PrimaryHighlightColor
                text = game.calculateSuccessProbability(battleUnit, tile.position, game.selectedSecondaryAction).toString
              }
              val winProbabilityContainer = new StackPane() {
                children = winProbability
                mouseTransparent = true
              }
              displayInGrid(Vector(winProbabilityContainer), Vector(tile.position), grid)

          // Updates uselected unit information
          val defending = if battleUnit.defending then " (Defending)" else ""
          selectedUnitType.value = battleUnit.unitType + defending
          selectedUnitHealth.value = Health + battleUnit.health.toString + "/" + battleUnit.maxHealth
          selectedUnitExperience.value = Experience + battleUnit.experience.toString
          selectedUnitAmmo.value = Ammo + battleUnit.ammo.toString + "/" + battleUnit.maxAmmo
          selectedUnitFuel.value = Fuel + battleUnit.fuel.toString + "/" + battleUnit.maxFuel

        end highlightBattleUnit

        // Deselects target selections and updates the selection state accordingly
        def clearHighlightedTiles() =
          if game.selectedSecondaryTile.isDefined then
            game.selectingSecondaryTarget = true
            selectedTilePane(grid, game.selectedSecondaryTile.get).fireEvent(syntheticMouseClick(selectedTilePane(grid, game.selectedSecondaryTile.get)))

          if game.selectedPrimaryTile.isDefined then
            game.selectingSecondaryTarget = false
            selectedTilePane(grid, game.selectedPrimaryTile.get).fireEvent(syntheticMouseClick(selectedTilePane(grid, game.selectedPrimaryTile.get)))
        end clearHighlightedTiles

        // Clears the highlighting of all tiles in the range of the selected action of the BattleUnit
        // that has the largest range by dynamically removing the correct number of colored rectangles
        def clearTilesInRange(battleUnit: BattleUnit) =
          for tile <- game.tilesInRange(battleUnit, if game.tilesInRange(battleUnit, game.selectedPrimaryAction).length > game.tilesInRange(battleUnit, game.selectedSecondaryAction).length then game.selectedPrimaryAction else game.selectedSecondaryAction) do
            val i = selectedTilePane(grid, tile).children.length - 3
            var j = i
            for tileContent <- 1 to i do
              selectedTilePane(grid, tile).children.remove(j)
              j -= 1
          // Clear all extra StackPanes
          grid.children.removeRange(MapWidth * MapHeight + game.player1.battleUnits.length + game.player2.battleUnits.length, grid.children.length)
          // Add back checkmarks for set actions
          for readyBattleUnit <- game.pendingActions do
            grid.add(drawPic("src/main/resources/done-symbol.png", scene, 0), readyBattleUnit.position.x, readyBattleUnit.position.y)

        if battleUnit.alive then
          game.selectedBattleUnit match
            case Some(sBU) =>
              if sBU == battleUnit then

                clearTilesInRange(sBU)
                // Deselects the BattleUnit and selected tiles
                border.stroke = Color.Transparent
                clearHighlightedTiles()
                game.selectedBattleUnit = None

                // Bring focus to primary action dropdown
                if !game.selectingSecondaryTarget then
                  primaryActionDropdown.requestFocus()
                  secondaryActionDropdown.disable = true

                selectedUnitType.value = SelectedUnitDefault
                selectedUnitHealth.value = Health
                selectedUnitExperience.value = Experience
                selectedUnitAmmo.value = Ammo
                selectedUnitFuel.value = Fuel

              if sBU != battleUnit then

                clearTilesInRange(sBU)
                clearHighlightedTiles()

                // Unhighlight previously highlighted BattleUnit tile
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
    displayInGrid(drawn, positions, grid)

  end drawBattleUnits

end GUI
