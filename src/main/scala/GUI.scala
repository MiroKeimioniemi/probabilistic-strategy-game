import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{HBox, Pane, VBox}

import java.io.FileInputStream

object GUI extends JFXApp3:

  private val game = new Game

  def start(): Unit =

    stage = new JFXApp3.PrimaryStage:
      title = "Strategy Game"
      width = 600
      height = 450

    val root = HBox()
    val scene = Scene(parent = root)
    stage.scene = scene

    for tile <- drawMapTiles() do
      root.children += tile



  private def drawPic(pic: Image): ImageView =
    val imageView = ImageView(pic)
    imageView.setX(10)
    imageView.setY(10)
    imageView.setFitWidth(100)
    imageView.setPreserveRatio(true)
    imageView



  private def drawMapTiles() =
    val drawables: Vector[TerrainTile] = game.gameMap.tiles
    var drawn: Vector[ImageView] = Vector[ImageView]()

    for drawable <- drawables do
      drawable match
        case grassTile: GrassTile =>
          val imageStream = FileInputStream("src/main/resources/grass-tile.png")
          drawn = drawn :+ drawPic(Image(imageStream))
        case rockTile: RockTile  =>
          val imageStream = FileInputStream("src/main/resources/dirt-tile.png")
          drawn = drawn :+ drawPic(Image(imageStream))
        case sandTile: SandTile  =>
          val imageStream = FileInputStream("src/main/resources/pixel-desert-tile (1).png")
          drawn = drawn :+ drawPic(Image(imageStream))

    drawn

end GUI
