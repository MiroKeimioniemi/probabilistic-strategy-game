import o1.{East, GridPos, West}
import scala.io.Source
import java.io.*
import scala.collection.mutable.{Buffer, Map}
import scala.util.{Failure, Success, Try}

/** Loads the game launch configuration from a custom human-readable file by reading the (single) file in the given directory and
 *  returns a GameMap object and Player objects corresponding to both players wrapped in a tuple if reading is successful. If file
 *  reading is unsuccessful or the file is formatted wrong, throws a corresponfing exception.
 *  @param dir Directory from which the single launch configuration file will be read, regardless of how it is named */
def loadConfig(dir: String): (GameMap, Player, Player) =
  val launchConfigDirectory = Option(new File(dir).list)

  // Makes sure that given directory only contains a single file and that it is read successfully
  launchConfigDirectory match
    case Some(dir) =>
      if dir.length != 1 then
        throw new IOException("Could not identify unique launch configuration file")
    case None =>
      throw new IOException("Directory not found")

  // Storage for parsed data
  var terrainTiles       = Vector[TerrainTile]()
  var player1BattleUnits = Vector[BattleUnit]()
  var player2BattleUnits = Vector[BattleUnit]()

  // State variables for locating the configuration
  var widthSet = false
  var mapFound = false
  var mapWidth = MapWidth

  // Grid position components
  var x = 0
  var y = 1

  // Opens the launch configuration file
  val fileReader = Source.fromFile(dir + launchConfigDirectory.getOrElse(throw new IOException("Could not identify unique launch configuration file")).head)
  // Processes the file one line at a time
  for line <- fileReader.getLines() do

    if line.trim.nonEmpty then
      var tiles = line.split("\\s+").toVector.filter(_ != "")
      if widthSet then
        tiles = tiles.take(mapWidth)
        y += 1
        x = 0

      if mapFound && tiles.nonEmpty then

        for tile <- tiles do
          x += 1
          // Mathces tile types and creates corresponding objects at corresponding locations
          tile match
            case tile =>
              var terrainTile = tile.takeWhile(_ != ',') match
                case "Grass"     => GrassTile(GridPos(x, y))
                case "Forest"    => ForestTile(GridPos(x, y))
                case "Rock"      => RockTile(GridPos(x, y))
                case "Sand"      => SandTile(GridPos(x,y))
                case "Dirt"      => DirtTile(GridPos(x,y))
                case "VegeDirt"  => VegetativeDirtTile(GridPos(x,y))
                case "Gravel"    => GravelTile(GridPos(x,y))
                case "Objective" => ConquestTile(GridPos(x,y))
                case _           => throw new Exception("invalid tile type")
              terrainTiles = terrainTiles :+ terrainTile

              // BattleUnits at and to the left of center belong to Player 1 whereas those on the right belong to Player 2
              val player1Unit = x <= (mapWidth / 2)
              val orientation = if player1Unit then East else West

              // Adds potential BattleUnits to their respective players
              if tile.contains(',') then
                var battleUnit = tile.dropWhile(_ != ',').tail match
                  case "Soldiers" => SoldiersUnit(GridPos(x, y), orientation, player1Unit)
                  case "Sniper"   => SniperUnit(GridPos(x, y), orientation, player1Unit)
                  case "Tank"     => TankUnit(GridPos(x, y), orientation, player1Unit)
                  case _           => throw new Exception("invalid BattleUnit type")
                if player1Unit then
                  player1BattleUnits = player1BattleUnits :+ battleUnit
                else
                  player2BattleUnits = player2BattleUnits :+ battleUnit


      if mapFound && !widthSet then
        mapWidth = tiles.length
        widthSet = true

      if tiles.contains(MapConfigStartFlag) then
        mapFound = true

      // Pads tile rows with the TerrainTile type found at (1, 1) in the map grid
      if widthSet && tiles.length < mapWidth then
        for i <- 1 to mapWidth - tiles.length do
          x += 1
          terrainTiles = terrainTiles :+ terrainTiles.head.getClass.getDeclaredConstructor(classOf[GridPos]).newInstance(GridPos(x, y))

  fileReader.close()
  (GameMap(mapWidth, y, terrainTiles), Player(Player1Name, player1BattleUnits), Player(Player2Name, player2BattleUnits))