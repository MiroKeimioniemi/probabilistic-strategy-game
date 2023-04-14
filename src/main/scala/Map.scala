import o1.grid.*

object GameMap:

  /** Returns a vector full of GrassTiles filling the entire area of the map */
  def tileGenerator(width: Int, height: Int): Vector[TerrainTile] =
    var tiles: Vector[TerrainTile] = Vector[TerrainTile]()

    for
      i <- 1 to width
      j <- 1 to height
    do
      tiles = tiles :+ GrassTile(GridPos(i, j))

    tiles

  end tileGenerator

  /** Updates the map tiles by replacing them with other tiles
   *  @param tiles Vector of all map tiles
   *  @param updates Vector of replacement tiles */
  def tileUpdater(tiles: Vector[TerrainTile], updates: Vector[TerrainTile]): Vector[TerrainTile] =
    var newTiles = tiles

    for updateTile <- updates do
      var replaceTile = tiles.find(_.position == updateTile.position).getOrElse(tiles.head)
      newTiles = newTiles.updated(tiles.indexOf(replaceTile), updateTile)

    newTiles

  end tileUpdater

  /** Updates the map tiles symmetrically about the origin, y-axis or x-axis depending on the mirror method selected
   * @param tiles TerrainTiles to be updated
   * @param updates Replacing tiles
   * @param mirrorMethod the axis or point about which the tiles will be mirrored with options "origin", "y-axis", "x-axis" and "none" */
  def symmetricTileUpdater(tiles: Vector[TerrainTile], updates: Vector[TerrainTile], mirrorMethod: String): Vector[TerrainTile] =

    // returns a TerrainTile of the same type mirrored symmetrically according to the mirror method
    def mirrorPositionedTile(tile: TerrainTile): TerrainTile =
      var mirrorPosition =
        if mirrorMethod.toLowerCase.contains("g") then GridPos(MapWidth + 1 - tile.position.x, MapHeight + 1 - tile.position.y)
        else if mirrorMethod.toLowerCase.contains("y") then GridPos(MapWidth + 1 - tile.position.x, tile.position.y)
        else if mirrorMethod.toLowerCase.contains("x") then GridPos(tile.position.x, MapHeight + 1 - tile.position.y)
        else tile.position
      tile.copySelf(mirrorPosition)

    var newTiles = tiles

    for updateTile <- updates do
      var replaceTile1 = tiles.find(_.position == updateTile.position).getOrElse(tiles.head)
      var replaceTile2 = tiles.find(_.position == mirrorPositionedTile(updateTile).position).getOrElse(tiles.head)
      newTiles = newTiles.updated(tiles.indexOf(replaceTile1), updateTile)
      newTiles = newTiles.updated(tiles.indexOf(replaceTile2), mirrorPositionedTile(updateTile))

    newTiles



class GameMap(width: Int, height: Int) extends Grid[TerrainTile](width, height):

  def initialElements: Vector[TerrainTile] = MapTiles

  var tiles: Vector[TerrainTile] = initialElements

  def getTile(position: GridPos): TerrainTile =
    tiles.find(_.position == position).getOrElse(tiles.head)

end GameMap