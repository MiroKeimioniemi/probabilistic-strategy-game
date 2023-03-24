import o1.grid.CompassDir.*
import o1.grid.GridPos

/** Contains the current configuration of the game as constants specifying class attributes that are initialized by constant literals.
 *
 * Most values should generally range between 0 and 100 for the gameplay to make sense but there is no explicit limitation for it. */

/** Map properties */
val MapWidth = 6    // 32
val MapHeight = 4   // 18
val MapTiles =
  GameMap.tileUpdater(
    GameMap.tileGenerator(MapWidth, MapHeight),
    Vector[TerrainTile](
    SandTile(GridPos(2, 1)),
    RockTile(GridPos(3, 3))
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
val GrassFlatness =          90
val GrassSolidity =          90
val GrassVegetationDensity = 5
val GrassElevation =         0

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