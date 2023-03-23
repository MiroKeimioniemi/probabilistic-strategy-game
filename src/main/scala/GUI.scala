import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.GridPane
import math.min
import java.io.FileInputStream
import o1.GridPos

object GUI extends JFXApp3:

  private val game = new Game

  def start(): Unit =

    stage = new JFXApp3.PrimaryStage:
      title = "Strategy Game"
      width = 600
      height = 450

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

    // Match tile type with image and append corresponding ImageView to drawn
    for drawable <- drawables do
      drawable match
        case grassTile: GrassTile =>
          val imageStream = FileInputStream("src/main/resources/grass-tile.png")
          drawn = drawn :+ drawPic(Image(imageStream))
        case rockTile: RockTile  =>
          val imageStream = FileInputStream("src/main/resources/rock-tile.png")
          drawn = drawn :+ drawPic(Image(imageStream))
        case sandTile: SandTile  =>
          val imageStream = FileInputStream("src/main/resources/desert-tile.png")
          drawn = drawn :+ drawPic(Image(imageStream))

    displayInGrid(drawn, positions, node)

  end drawMapTiles

  /** Displays images corresponding to the battle units in the GUI */
  private def drawBattleUnits(node: GridPane, player: Player): Unit =

    val drawables: Vector[BattleUnit] = player.battleUnits
    val positions: Vector[GridPos] = drawables.map(_.position)
    var drawn: Vector[ImageView] = Vector[ImageView]()

    // Match tile type with image and append corresponding ImageView to drawn
    for drawable <- drawables do
      drawable match
        case tankUnit: TankUnit =>
          val imageStream = FileInputStream("src/main/resources/green-tank.png")
          drawn = drawn :+ drawPic(Image(imageStream))

    displayInGrid(drawn, positions, node)

end GUI
