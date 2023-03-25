import o1.grid.CompassDir.*
import o1.grid.GridPos
import scalafx.scene.paint.Color

/** Contains the current configuration of the game as constants specifying class attributes that are initialized by constant literals.
 *
 * Most values should generally range between 0 and 100 for the gameplay to make sense but there is no explicit limitation for it. */

/** Game window properties */
val GameWindowWidth = 1600
val GameWindowHeight = 900
val SelectionRectangleThickness = 4
val HighlightColor = Color.Red

/** Map properties */
val MapWidth = 16    // 32
val MapHeight = 9   // 18
val MapTiles =
  GameMap.symmetricTileUpdater(
    GameMap.tileGenerator(MapWidth, MapHeight),
    Vector[TerrainTile](
      RockTile(GridPos(4, 2)),
      RockTile(GridPos(5, 2)),
      RockTile(GridPos(3, 8)),
      RockTile(GridPos(7, 4)),
      RockTile(GridPos(7, 6)),
      RockTile(GridPos(8, 6)),
      SandTile(GridPos(7, 1)),
      SandTile(GridPos(8, 1)),
      SandTile(GridPos(8, 2)),
      SandTile(GridPos(8, 7)),
      SandTile(GridPos(8, 8)),
      SandTile(GridPos(8, 9)),
      SandTile(GridPos(7, 9)),
      ForestTile(GridPos(6, 4)),
      ForestTile(GridPos(4, 5)),
      ForestTile(GridPos(5, 5)),
      ForestTile(GridPos(6, 5)),
      ForestTile(GridPos(7, 5)),
      ForestTile(GridPos(5, 6)),
      ForestTile(GridPos(6, 6)),
      ForestTile(GridPos(6, 7)),
      ForestTile(GridPos(7, 7))
  ))

/** Player properties */
// Player 1
val Player1BattleUnitsFormation =
  Vector[BattleUnit](
    TankUnit(GridPos(2, 1), East),
    TankUnit(GridPos(1, 2), East)
)

/** TerrainTiles' properties' values are interpreted as percentages such that 100 -> 100% */
// Grass
val GrassFlatness =          95
val GrassSolidity =          95
val GrassVegetationDensity = 5
val GrassElevation =         0

// Forest
val ForestFlatness =          25
val ForestSolidity =          85
val ForestVegetationDensity = 95
val ForestElevation =         5

// Rock
val RockFlatness =          5
val RockSolidity =          100
val RockVegetationDensity = 0
val RockElevation =         100

// Sand
val SandFlatness =          50
val SandSolidity =          20
val SandVegetationDensity = 0
val SandElevation =         10

/** BattleUnit properties */
// Tank
val TankWeight =         60000
val TankVolume =         80
val TankRange =          2
val TankArmor =          100
val TankBaseDamage =     100
val TankDamageGradient = LazyList.iterate(TankBaseDamage * 2)( x => x / 2 ).patch(0, Iterable(TankBaseDamage / 10), 1) // TDG(0) = 10, TDG(1) = 100, TDG(n) = 100 / 2n

var TankAmmo =   2
var TankFuel =   2
var TankHealth = 100