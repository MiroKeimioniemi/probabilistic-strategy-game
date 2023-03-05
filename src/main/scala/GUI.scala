import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.paint.Color._
import scalafx.scene.shape.Rectangle
import scalafx.scene.layout.Pane

object GUI extends JFXApp3:
  
  val Game = new Game

  def start(): Unit =

    stage = new JFXApp3.PrimaryStage:
      title = "Strategy Game"
      width = 600
      height = 450

    val root = Pane()
    val scene = Scene(parent = root)
    stage.scene = scene

end GUI
