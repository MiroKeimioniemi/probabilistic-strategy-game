import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.GridPane
import math.min
import java.io.FileInputStream
import o1.GridPos

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
  private def drawPic(pic: Image): ImageView =
    val imageView = ImageView(pic)
    imageView.setX(10)
    imageView.setY(10)
    imageView.setFitWidth(100)
    imageView.setPreserveRatio(true)
    imageView
  end drawPic


  // Add ImageView objects to GridPane children to be displayed in a scene
  private def displayInGrid(drawn: Vector[ImageView], positions: Vector[GridPos], node: GridPane) =
    for element <- drawn zip positions do
      node.add(element._1, element._2.x, element._2.y)
  end displayInGrid


  /** Displays images corresponding to the map tiles in the GUI */
  private def drawMapTiles(node: GridPane): Unit =

    val drawables: Vector[TerrainTile] = game.gameMap.tiles
    val positions: Vector[GridPos] = drawables.map(_.position)
    var drawn: Vector[ImageView] = Vector[ImageView]()

    drawables.foreach(drawable => drawn = drawn :+ drawPic(Image(drawable.image)))

    displayInGrid(drawn, positions, node)

  end drawMapTiles

  /** Displays images corresponding to the battle units in the GUI */
  private def drawBattleUnits(node: GridPane, player: Player): Unit =

    val drawables: Vector[BattleUnit] = player.battleUnits
    val positions: Vector[GridPos] = drawables.map(_.position)
    var drawn: Vector[ImageView] = Vector[ImageView]()

    drawables.foreach(drawable => drawn = drawn :+ drawPic(Image(drawable.image)))

    displayInGrid(drawn, positions, node)

end GUI
