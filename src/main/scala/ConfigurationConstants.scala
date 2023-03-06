/** Contains the current configuration of the game as constants specifying class attributes that are initialized by constant literals.
 *
 * Most values should generally range between 0 and 100 for the gameplay to make sense but there is no explicit limitation for it. */

/** Map properties */
val MapWidth = 3    // 32
val MapHeight = 2   // 18

/** TerrainTiles' properties' values are interpreted as percentages such that 100 -> 100% */
// Grass
val GrassFlatness = 90
val GrassSolidity = 90
val GrassVegetationDensity = 5
val GrassElevation = 0

// Rock
val RockFlatness = 5
val RockSolidity = 100
val RockVegetationDensity = 0
val RockElevation = 100

// Sand
val SandFlatness = 50
val SandSolidity = 20
val SandVegetationDensity = 0
val SandElevation = 10
