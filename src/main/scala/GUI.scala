import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.GridPane
import math.min

import java.io.FileInputStream

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


  /** Returns an ImageView object corresponding to a given image */
  private def drawPic(pic: Image): ImageView =
    val imageView = ImageView(pic)
    imageView.setX(10)
    imageView.setY(10)
    imageView.setFitWidth(100)
    imageView.setPreserveRatio(true)
    imageView
  end drawPic


  /** Returns a vector of ImageView objects corresponding to the map tiles, which
   * can be used to display images corresponding to the tiles in the GUI */
  private def drawMapTiles(root: GridPane): Unit =

    val drawables: Vector[TerrainTile] = game.gameMap.tiles
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

    // Add ImageView objects to GridPane children to be displayed in a scene
    def displayMapTiles(drawn: Vector[ImageView]) =

      var columnIndex = 0
      var rowIndex = 0
      var tileIndex = 0

      while rowIndex < MapHeight do
        while columnIndex < MapWidth do
          root.add(drawn(tileIndex), columnIndex, rowIndex)
          columnIndex += 1
          tileIndex += 1
        rowIndex += 1
        columnIndex = 0

    end displayMapTiles

    displayMapTiles(drawn)

  end drawMapTiles


end GUI
