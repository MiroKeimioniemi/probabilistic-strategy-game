import o1.grid.*

class Map(width: Int, height: Int) extends Grid[TerrainTile](width, height):
  def initialElements: Vector[TerrainTile] = Vector(new GrassTile, new RockTile, new GrassTile, new GrassTile)
end Map