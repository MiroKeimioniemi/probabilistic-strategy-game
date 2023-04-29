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
val SelectionRectangleThickness = 6
val BattleUnitHighlightColor    = Color.Blue
val PrimaryHighlightColor       = Color.Red
val SecondaryHighlightColor     = Color.Yellow
val HeadingFont                 = Font.font("Open Sans", FontWeight.Bold, 20)
val DefaultFont                 = Font.font("Open Sans", 20)
val DefaultSpacing              = 20
val LayoutInset                 = Insets(25)
val DefaultLeftMargin           = Insets(0, 0, 0, 20)
val DropdownWidth               = 80
val DropdownHeight              = 50
val HealthBarCriticalThreshold  = 0.5
val HealthBarHealthyColor       = Color.LimeGreen
val HealthBarCriticalColor      = Color.Red
val BarBackgroundColor          = Color.DarkGrey
val BarRounding                 = 5
val ProgressBarWidth            = 200
val ProgressBarHeight           = 5
val TextColor                   = Color.White
val RightPaneBackgroundStyle    = "-fx-background-color: #252825"
val BottomPaneBacgroundStyle    = "-fx-background-color: #222522"
val CounterWidth                = 250
val PopUpHeadingFont            = Font.font("Open Sans", FontWeight.Bold, 40)
val PopUpButtonFont             = Font.font("Open Sans", 14)

/** Text */
val GameTitle                = "Strategy Game"
val SelectedUnitDefault      = "Choose unit"
val PlayTurnButton           = "Play Turn"
val SetActionSetButton       = "Set Action set"
val PrimaryAction            = "Primary Action"
val PrimaryTargetSelection   = "Select target"
val SecondaryAction          = "Secondary Action"
val SecondaryTargetSelection = "Select target"
val Health                   = "HP: "
val Experience               = "XP: "
val Ammo                     = "Ammo: "
val Fuel                     = "Fuel: "
val Player1Score             = "Player 1 score: "
val Player2Score             = "Player 2 score: "
val PopUpRestartButton       = "Restart"
val PopUpQuitButton          = "Quit"



/** TerrainTiles' properties' values are interpreted as percentages such that 100 -> 100% */
// Grass
val GrassImage             = "src/main/resources/grass-tile.png"
val GrassFlatness          = 95
val GrassSolidity          = 95
val GrassVegetationDensity = 2
val GrassElevation         = 0

// Dirt
val DirtImage             = "src/main/resources/dirt-tile.png"
val DirtFlatness          = 75
val DirtSolidity          = 75
val DirtVegetationDensity = 0
val DirtElevation         = 0

// VegetativeDirt
val VegetativeDirtImage             = "src/main/resources/vegetative-dirt-tile.png"
val VegetativeDirtFlatness          = 70
val VegetativeDirtSolidity          = 70
val VegetativeDirtVegetationDensity = 5
val VegetativeDirtElevation         = 0

// Forest
val ForestImage             = "src/main/resources/forest-tile.png"
val ForestFlatness          = 25
val ForestSolidity          = 85
val ForestVegetationDensity = 75
val ForestElevation         = 0

// Rock
val RockImage             = "src/main/resources/rock-tile.png"
val RockFlatness          = 5
val RockSolidity          = 100
val RockVegetationDensity = 0
val RockElevation         = 100

// Gravel
val GravelImage             = "src/main/resources/gravel-tile.png"
val GravelFlatness          = 80
val GravelSolidity          = 90
val GravelVegetationDensity = 0
val GravelElevation         = 10

// Sand
val SandImage             = "src/main/resources/desert-tile.png"
val SandFlatness          = 50
val SandSolidity          = 20
val SandVegetationDensity = 0
val SandElevation         = 10

val NeutralConquestTileImage      = "src/main/resources/neutral-conquest-tile.png"
val BlueConquestTileImage         = "src/main/resources/captured-blue-conquest-tile.png"
val RedConquestTileImage          = "src/main/resources/captured-red-conquest-tile.png"
val ConquestTileFlatness          = 100
val ConquestTileSolidity          = 100
val ConquestTileVegetationDensity = 0
val ConquestTileElevation         = 0



/** BattleUnit properties */
// Tank
val Player1TankImage          = "src/main/resources/blue-tank.png"
val Player1DestroyedTankImage = "src/main/resources/destroyed-blue-tank.png"
val Player2TankImage          = "src/main/resources/red-tank.png"
val Player2DestroyedTankImage = "src/main/resources/destroyed-red-tank.png"

val TankUnitType        = "Tank"
val TankWeight          = 100
val TankVolume          = 100
val TankRange           = 1
val TankArmor           = 100
val TankBaseDamage      = 90
val TankExplosiveDamage = true
val TankMaxHealth       = 100
val TankDamageGradient: LazyList[Double] = LazyList.iterate(TankBaseDamage * 2.0)( x => max(1, (x / 2)) ).patch(0, Iterable(TankBaseDamage / 10), 1)

var TankAmmo   = 3
var TankFuel   = 3
var TankHealth = TankMaxHealth

// Soldiers
val Player1SoldiersImage          = "src/main/resources/blue-soldiers.png"
val Player1DestroyedSoldiersImage = "src/main/resources/dead-blue-soldiers.png"
val Player2SoldiersImage          = "src/main/resources/red-soldiers.png"
val Player2DestroyedSoldiersImage = "src/main/resources/dead-red-soldiers.png"

val SoldiersUnitType        = "Soldiers"
val SoldiersWeight          = 50
val SoldiersVolume          = 50
val SoldiersRange           = 1
val SoldiersArmor           = 50
val SoldiersBaseDamage      = 80
val SoldiersExplosiveDamage = false
val SoldiersMaxHealth       = 50
val SoldiersDamageGradient: LazyList[Double] = LazyList.iterate(SoldiersBaseDamage * 1.0)( x => max(1, (x / 2)) )

var SoldiersAmmo   = 10
var SoldiersFuel   = 10
var SoldiersHealth = SoldiersMaxHealth

// Sniper
val Player1SniperImage          = "src/main/resources/blue-sniper.png"
val Player1DestroyedSniperImage = "src/main/resources/dead-blue-sniper.png"
val Player2SniperImage          = "src/main/resources/red-sniper.png"
val Player2DestroyedSniperImage = "src/main/resources/dead-red-sniper.png"

val SniperUnitType        = "Sniper"
val SniperWeight          = 25
val SniperVolume          = 25
val SniperRange           = 2
val SniperArmor           = 25
val SniperBaseDamage      = 120
val SniperExplosiveDamage = false
val SniperMaxHealth       = 25
val SniperDamageGradient: LazyList[Double] = LazyList.iterate(SniperBaseDamage * 4.0)( x => max(1, (x / 1.5)) ).patch(0, Iterable(SniperBaseDamage / 6, SniperBaseDamage / 4.5, SniperBaseDamage / 3, SniperBaseDamage / 1.5), 4)

var SniperAmmo   = 3
var SniperFuel   = 3
var SniperHealth = SniperMaxHealth



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
      ForestTile(GridPos(7, 7)),
      ConquestTile(GridPos(8, 5))
  ), "origin")



/** Player properties */
// Player 1
val Player1BattleUnitsFormation =
  Vector[BattleUnit](
    TankUnit(GridPos(2, 1), East, true),
    TankUnit(GridPos(1, 2), East, true),
    TankUnit(GridPos(7, 8), East, true),
    SniperUnit(GridPos(11, 1), East, true),
    SoldiersUnit(GridPos(8, 5), East, true)
)
val Player1Color = Color.web("4d6ef3")

// Player 2
val Player2BattleUnitsFormation =
  Vector[BattleUnit](
    TankUnit(GridPos(15, 1), West, false),
    TankUnit(GridPos(16, 2), West, false),
    TankUnit(GridPos(9, 8), West, false),
    TankUnit(GridPos(7, 6), West, false),
    SoldiersUnit(GridPos(4, 1), West, false),
    SniperUnit(GridPos(2, 3), North, false)
)
val Player2Color = Color.web("ed1c23")



/** GamePlay properties */
val ConquestTarget = 100
val DefendStrength = 2