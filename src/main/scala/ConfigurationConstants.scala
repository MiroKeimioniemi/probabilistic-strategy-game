import o1.grid.CompassDir.*
import o1.grid.GridPos
import scalafx.geometry.Insets
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, FontWeight}

import math.max

/** Contains the current configuration of the game as constants specifying class attributes that are initialized by constant literals.
 *
 * All Game object values are relative and should therefore generally range between 0 and 100. */

/** GUI properties */
// Layout properties
val GameWindowWidth  = 1280
val GameWindowHeight = 720
val RightPaneWidth   = GameWindowWidth / 4
val BottomPaneHeight = GameWindowHeight / 15

// Component properties
val SelectionRectangleThickness = 4
val BattleUnitHighlightColor    = Color.Blue
val PrimaryHighlightColor       = Color.Red
val SecondaryHighlightColor     = Color.Yellow
val HeadingFont                 = Font.font("Open Sans", FontWeight.Bold, 20)
val DefaultFont                 = Font.font("Open Sans", 20)
val DefaultSpacing              = 20
val LayoutInset                 = Insets(20)
val DefaultLeftMargin           = Insets(0, 0, 0, 10)
val DropdownWidth               = 80
val DropdownHeight              = 50
val HealthBarRounding           = 5
val HealthBarCriticalThreshold  = 0.5
val HealthBarHealthyColor       = Color.LimeGreen
val HealthBarCriticalColor      = Color.Red

/** Text */
val GameTitle                = "Strategy Game"
val SelectedUnitDefault      = "Choose unit"
val PlayTurnButton           = "Play Turn"
val SetActionSetButton       = "Set Action set"
val PrimaryAction            = "Primary Action"
val PrimaryTargetSelection   = "Select target"
val SecondaryAction          = "Secondary Action"
val SecondaryTargetSelection = "Select target"



/** TerrainTiles' properties' values are interpreted as percentages such that 100 -> 100% */
// Grass
val GrassFlatness          = 95
val GrassSolidity          = 95
val GrassVegetationDensity = 2
val GrassElevation         = 0

// Forest
val ForestFlatness          = 25
val ForestSolidity          = 85
val ForestVegetationDensity = 75
val ForestElevation         = 0

// Rock
val RockFlatness          = 5
val RockSolidity          = 100
val RockVegetationDensity = 0
val RockElevation         = 100

// Sand
val SandFlatness          = 50
val SandSolidity          = 20
val SandVegetationDensity = 0
val SandElevation         = 10

/** BattleUnit properties */
// Tank
val TankWeight         = 100
val TankVolume         = 100
val TankRange          = 1
val TankArmor          = 100
val TankBaseDamage     = 90
val TankDamageGradient: LazyList[Double] = LazyList.iterate(TankBaseDamage * 2.0)( x => max(1, (x / 2)) ).patch(0, Iterable(TankBaseDamage / 10), 1) // TDG(0) = 10, TDG(1) = 100, TDG(n) = 100 / 2n
val TankMaxHealth          = 100

var TankAmmo   = 2
var TankFuel   = 2
var TankHealth = 100



/** Map properties */
val MapWidth  = 16
val MapHeight = 9
val MapTiles  =
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
  ), "origin")

/** Player properties */
// Player 1
val Player1BattleUnitsFormation =
  Vector[BattleUnit](
    Player1TankUnit(GridPos(2, 1), East),
    Player1TankUnit(GridPos(1, 2), East),
    Player1TankUnit(GridPos(7, 8), East)
)

val Player2BattleUnitsFormation =
  Vector[BattleUnit](
    Player2TankUnit(GridPos(15, 1), West),
    Player2TankUnit(GridPos(16, 2), West),
    Player2TankUnit(GridPos(9, 8), West),
    Player2TankUnit(GridPos(7, 6), West)
)
 
/** GamePlay properties */
val DefendStrength = 2