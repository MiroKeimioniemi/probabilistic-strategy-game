import o1.grid.*

object GameMap:

  def tileGenerator(width: Int, height: Int): Vector[TerrainTile] =
    var tiles: Vector[TerrainTile] = Vector[TerrainTile]()

    for
      i <- 1 to width
      j <- 1 to height
    do
      tiles = tiles :+ GrassTile(GridPos(i, j))

    tiles

  end tileGenerator

  def tileUpdater(tiles: Vector[TerrainTile], updates: Vector[TerrainTile]): Vector[TerrainTile] =
    var newTiles = tiles

    for updateTile <- updates do
      var replaceTile = tiles.find(_.position == updateTile.position)
      if replaceTile.isDefined then
        newTiles = newTiles.updated(tiles.indexOf(replaceTile.getOrElse(tiles.head)), updateTile)

    newTiles

  end tileUpdater



class GameMap(width: Int, height: Int) extends Grid[TerrainTile](width, height):

  def initialElements: Vector[TerrainTile] = MapTiles

  var tiles: Vector[TerrainTile] = initialElements

  def getTile(position: GridPos): TerrainTile =
    tiles.find(_.position == position).getOrElse(tiles.head)

end GameMap