import o1.grid.*

class GameMap(width: Int, height: Int) extends Grid[TerrainTile](width, height):

  def initialElements: Vector[TerrainTile] = Vector(GrassTile(GridPos(1, 1)), SandTile(GridPos(2, 1)), SandTile(GridPos(3, 1)), GrassTile(GridPos(1, 2)), GrassTile(GridPos(2, 2)), RockTile(GridPos(3, 2)))

  var tiles: Vector[TerrainTile] = initialElements

end GameMap