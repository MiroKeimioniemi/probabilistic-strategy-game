import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{GridPane, StackPane}
import math.min
import java.io.FileInputStream
import o1.GridPos
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

object GUI extends JFXApp3:

  // Attempt to use hardware acceleration
  System.setProperty("prism.order", "sw")

  private val game = new Game

  def start(): Unit =

    stage = new JFXApp3.PrimaryStage:
      title = "Strategy Game"
      width = GameWindowWidth
      height = GameWindowHeight

    val root = GridPane()
    val scene = Scene(parent = root)
    stage.scene = scene

    drawMapTiles(root)
    drawBattleUnits(root, game.player1)


  /** Returns an ImageView object corresponding to a given image */
  private def drawPic(pic: FileInputStream): ImageView =
    val imageView = ImageView(Image(pic))
    imageView.setX(10)
    imageView.setY(10)
    imageView.setFitWidth(100)
    imageView.setFitHeight(100)
    imageView.setPreserveRatio(true)
    imageView
  end drawPic

  /** Returns a vector of StackPanes containing the original images with colored borderse drawn on top */
  private def highlightTiles(tiles: Vector[TerrainTile]): Vector[StackPane] =

    val images = tiles.map(tile => drawPic(tile.image))
    var encircledImages = Vector[StackPane]()

    for image <- images do
      val border = new Rectangle {
        width <== images(0).fitWidth - strokeWidth
        height <== images(0).fitHeight - strokeWidth
        strokeWidth = SelectionRectangleThickness
        stroke = HighlightColor
        fill = Color.Transparent
      }

      val selectable = StackPane()
      selectable.children.addAll(image, border)
      encircledImages = encircledImages :+ selectable

    encircledImages

  end highlightTiles

  /** Add StackPane objects to GridPane children to be displayed in a scene */
  private def displayInGrid(drawn: Vector[StackPane], positions: Vector[GridPos], node: GridPane) =
    for element <- drawn zip positions do
      node.add(element._1, element._2.x, element._2.y)
  end displayInGrid


  /** Displays selectable images corresponding to the map tiles in the GUI */
  private def drawMapTiles(node: GridPane): Unit =

    val tiles: Vector[TerrainTile] = game.gameMap.tiles
    val positions: Vector[GridPos] = tiles.map(_.position)
    var drawn: Vector[StackPane] = Vector[StackPane]()

    /** Returns a StackPane containing the image of the tile, an initially transparent border and
     * a mouse click event listener that toggles the highlighting (border) of the tile */
    def selectableTiles(tile: TerrainTile): StackPane =

      val image = drawPic(tile.image)
      val border = new Rectangle {
        width <== image.fitWidth - strokeWidth
        height <== image.fitHeight - strokeWidth
        strokeWidth = SelectionRectangleThickness
        stroke = Color.Transparent
        fill = Color.Transparent
      }
      val selectable = StackPane()
      selectable.children.addAll(image, border)

      var selected = false

      selectable.onMouseClicked = _ => {
        if selected then
          border.stroke = Color.Transparent
          selected = false
          game.selectedTiles = game.selectedTiles.filterNot(_ == tile)
        else
          border.stroke = HighlightColor
          selected = true
          game.selectedTiles = game.selectedTiles :+ tile
      }

      selectable

    end selectableTiles

    tiles.foreach(drawable => drawn = drawn :+ selectableTiles(drawable))
    displayInGrid(drawn, positions, node)

  end drawMapTiles

  /** Displays selectable images corresponding to the battle units in the GUI */
  private def drawBattleUnits(node: GridPane, player: Player): Unit =

    val battleUnits: Vector[BattleUnit] = player.battleUnits
    val positions: Vector[GridPos] = battleUnits.map(_.position)
    var drawn: Vector[StackPane] = Vector[StackPane]()

    /** Returns a StackPane containing the image of the battle unit, an initially transparent border
     * and a mouse click event listener that toggles the highlighting (border) of the BattleUnit and
     * the highlightTiles in the Game class associated with it */
    def selectableBattleUnit(battleUnit: BattleUnit): StackPane =

      val image = drawPic(battleUnit.image)
      val border = new Rectangle {
        width <== image.fitWidth - strokeWidth
        height <== image.fitHeight - strokeWidth
        strokeWidth = SelectionRectangleThickness
        stroke = Color.Transparent
        fill = Color.Transparent
      }
      val selectable = StackPane()
      selectable.children.addAll(image, border)

      var selected = false

      selectable.onMouseClicked = _ => {
        if selected then
          border.stroke = Color.Transparent
          selected = false
          game.selectedBattleUnits = game.selectedBattleUnits.filterNot(_ == battleUnit)
          for i <- game.fovTiles.indices do
            node.children.remove(node.children.last)
        else
          border.stroke = HighlightColor
          selected = true
          game.selectedBattleUnits = game.selectedBattleUnits :+ battleUnit
          // displays the highlighted fov tiles on top of the original tiles
          displayInGrid(highlightTiles(game.fovTiles), game.fovTiles.map(_.position), node)
      }

      selectable

    end selectableBattleUnit

    battleUnits.foreach(battleUnit => drawn = drawn :+ selectableBattleUnit(battleUnit))
    displayInGrid(drawn, positions, node)

  end drawBattleUnits

end GUI
