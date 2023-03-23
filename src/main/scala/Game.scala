import o1.grid.CompassDir.*
import o1.grid.GridPos

class Game:

  val gameMap = GameMap(MapWidth, MapHeight)
  val player1 = Player("Player 1")
  val player2 = Player("Player 2")

  var turnCount = 0
  var gameOver = false

end Game
