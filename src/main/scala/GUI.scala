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

  private def drawRectangleAround(image: ImageView): StackPane =

    val border = new Rectangle {
      width <== image.fitWidth - strokeWidth
      height <== image.fitHeight - strokeWidth
      strokeWidth = SelectionRectangleThickness
      stroke = Color.Transparent
      fill = Color.Transparent
    }

    var selected = false

    val encircled = StackPane()
    encircled.children.addAll(image, border)

    encircled.onMouseClicked = _ => {
      if selected then
        border.stroke = Color.Transparent
        selected = false
      else
        border.stroke = Color.Red
        selected = true
    }

    encircled


  /** Add ImageView objects to GridPane children to be displayed in a scene */
  private def displayInGrid(drawn: Vector[StackPane], positions: Vector[GridPos], node: GridPane) =
    for element <- drawn zip positions do
      node.add(element._1, element._2.x, element._2.y)
  end displayInGrid


  /** Displays images corresponding to the map tiles in the GUI */
  private def drawMapTiles(node: GridPane): Unit =

    val drawables: Vector[TerrainTile] = game.gameMap.tiles
    val positions: Vector[GridPos] = drawables.map(_.position)
    var drawn: Vector[StackPane] = Vector[StackPane]()

    drawables.foreach(drawable => drawn = drawn :+ drawRectangleAround(drawPic(drawable.image)))

    displayInGrid(drawn, positions, node)

  end drawMapTiles

  /** Displays images corresponding to the battle units in the GUI */
  private def drawBattleUnits(node: GridPane, player: Player): Unit =

    val drawables: Vector[BattleUnit] = player.battleUnits
    val positions: Vector[GridPos] = drawables.map(_.position)
    var drawn: Vector[StackPane] = Vector[StackPane]()

    drawables.foreach(drawable => drawn = drawn :+ drawRectangleAround(drawPic(drawable.image)))

    displayInGrid(drawn, positions, node)

end GUI
