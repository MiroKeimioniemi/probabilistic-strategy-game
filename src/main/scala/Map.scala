import o1.grid.*

class GameMap(width: Int, height: Int) extends Grid[TerrainTile](width, height):

  def initialElements: Vector[TerrainTile] = Vector(GrassTile(), SandTile(), SandTile(), GrassTile(), GrassTile(), SandTile())

  var tiles: Vector[TerrainTile] = initialElements

end GameMap