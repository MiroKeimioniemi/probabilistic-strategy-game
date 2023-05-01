//import scala.util.{Try, Failure, Success}
//import java.io.{FileReader, BufferedReader, FileNotFoundException}
//
//import io.circe.generic.auto.*
//import io.circe.syntax.*
//import io.circe.parser.decode
//
//import o1.grid.CompassDir.*
//import o1.grid.GridPos
//import scalafx.geometry.Insets
//import scalafx.scene.paint.Color
//import scalafx.scene.text.{Font, FontWeight}
//
//import math.max
//
//case class GameConfig(
//
///** Contains the default configuration of the game as constants specifying class attributes that are initialized by constant literals.
// *
// * Most Game object values are relative and should therefore generally range between 0 and 100. */
//
///** GUI properties */
//// Layout properties
//val GameWindowWidth:  Int,
//val GameWindowHeight: Int,
//val RightPaneWidth:   Int,
//val BottomPaneHeight: Int,
//
//// Component properties
//val SelectionRectangleThickness: Int,
//
//// Colors
//val TextColor:                     Color,
//val PrimaryActionHighlightColor:   Color,
//val SecondaryActionHighlightColor: Color,
//val PrimaryHighlightColor:         Color,
//val SecondaryHighlightColor:       Color,
//val HealthBarHealthyColor:         Color,
//val HealthBarCriticalColor:        Color,
//val BarBackgroundColor:            Color,
//
//val RightPaneBackgroundStyle: String,
//val BottomPaneBacgroundStyle: String,
//
//// Fonts
//val HeadingFont:      Font,
//val DefaultFont:      Font,
//val PopUpHeadingFont: Font,
//val PopUpButtonFont:  Font,
//
//// Values
//val DefaultSpacing:             Int,
//val LayoutInset:                Int,
//val DefaultLeftMargin:          Int,
//val DropdownWidth:              Int,
//val DropdownHeight:             Int,
//val HealthBarCriticalThreshold: Int,
//val BarRounding:                Int,
//val ProgressBarWidth:           Int,
//val ProgressBarHeight:          Int,
//val CounterWidth:               Int,
//
//
///** Text */
//val GameTitle:                String,
//val SelectedUnitDefault:      String,
//val PlayTurnButton:           String,
//val SetActionSetButton:       String,
//val PrimaryAction:            String,
//val PrimaryTargetSelection:   String,
//val SecondaryAction:          String,
//val SecondaryTargetSelection: String,
//val Health:                   String,
//val Experience:               String,
//val Ammo:                     String,
//val Fuel:                     String,
//val Player1Score:             String,
//val Player2Score:             String,
//val PopUpRestartButton:       String,
//val PopUpQuitButton:          String,
//val Player1Win:               String,
//val Player2Win:               String,
//
//
//
///** TerrainTiles' properties' values are interpreted as percentages such that 100 -> 100% */
//// Grass
//val GrassImage:             String,
//val GrassFlatness:          Int,
//val GrassSolidity:          Int,
//val GrassVegetationDensity: Int,
//val GrassElevation:         Int,
//
//// Dirt
//val DirtImage:             String,
//val DirtFlatness:          Int,
//val DirtSolidity:          Int,
//val DirtVegetationDensity: Int,
//val DirtElevation:         Int,
//
//// VegetativeDirt
//val VegetativeDirtImage:             String,
//val VegetativeDirtFlatness:          Int,
//val VegetativeDirtSolidity:          Int,
//val VegetativeDirtVegetationDensity: Int,
//val VegetativeDirtElevation:         Int,
//
//// Forest
//val ForestImage:             String,
//val ForestFlatness:          Int,
//val ForestSolidity:          Int,
//val ForestVegetationDensity: Int,
//val ForestElevation:         Int,
//
//// Rock
//val RockImage:             String,
//val RockFlatness:          Int,
//val RockSolidity:          Int,
//val RockVegetationDensity: Int,
//val RockElevation:         Int,
//
//// Gravel
//val GravelImage:             String,
//val GravelFlatness:          Int,
//val GravelSolidity:          Int,
//val GravelVegetationDensity: Int,
//val GravelElevation:         Int,
//
//// Sand
//val SandImage:             String,
//val SandFlatness:          Int,
//val SandSolidity:          Int,
//val SandVegetationDensity: Int,
//val SandElevation:         Int,
//
//val NeutralConquestTileImage:      String,
//val BlueConquestTileImage:         String,
//val RedConquestTileImage:          String,
//val ConquestTileFlatness:          Int,
//val ConquestTileSolidity:          Int,
//val ConquestTileVegetationDensity: Int,
//val ConquestTileElevation:         Int,
//
//
//
/////** BattleUnit properties */
////// Tank
////val Player1TankImage:          String,
////val Player1DestroyedTankImage: String,
////val Player2TankImage:          String,
////val Player2DestroyedTankImage: String,
////
////val TankUnitType:        String,
////val TankWeight:          Int,
////val TankVolume:          Int,
////val TankRange:           Int,
////val TankArmor:           Int,
////val TankBaseDamage:      Int,
////val TankExplosiveDamage: Boolean,
////val TankMaxHealth:       Int,
////val TankDamageGradient:  List[Double],
////
////var TankAmmo:   Int,
////var TankFuel:   Int,
////var TankHealth: Int,
////
////// Soldiers
////val Player1SoldiersImage:          String,
////val Player1DestroyedSoldiersImage: String,
////val Player2SoldiersImage:          String,
////val Player2DestroyedSoldiersImage: String,
////
////val SoldiersUnitType:        String,
////val SoldiersWeight:          Int,
////val SoldiersVolume:          Int,
////val SoldiersRange:           Int,
////val SoldiersArmor:           Int,
////val SoldiersBaseDamage:      Int,
////val SoldiersExplosiveDamage: Boolean,
////val SoldiersMaxHealth:       Int,
////val SoldiersDamageGradient:  List[Double],
////
////var SoldiersAmmo:   Int,
////var SoldiersFuel:   Int,
////var SoldiersHealth: Int,
////
////
////// Sniper
////val Player1SniperImage:          String,
////val Player1DestroyedSniperImage: String,
////val Player2SniperImage:          String,
////val Player2DestroyedSniperImage: String,
////
////val SniperUnitType:        String,
////val SniperWeight:          Int,
////val SniperVolume:          Int,
////val SniperRange:           Int,
////val SniperArmor:           Int,
////val SniperBaseDamage:      Int,
////val SniperExplosiveDamage: Boolean,
////val SniperMaxHealth:       Int,
////val SniperDamageGradient:  List[Double],
////
////var SniperAmmo:   Int,
////var SniperFuel:   Int,
////var SniperHealth: Int,
//
//
//
/////** Map properties */
////val MapWidth:  Int,
////val MapHeight: Int,
////val MapTiles  =
////  GameMap.symmetricTileUpdater(
////    GameMap.tileGenerator(MapWidth, MapHeight),
////    Vector[TerrainTile](
////      RockTile(GridPos(4, 2)),
////      RockTile(GridPos(5, 2)),
////      RockTile(GridPos(3, 8)),
////      RockTile(GridPos(7, 4)),
////      RockTile(GridPos(7, 6)),
////      RockTile(GridPos(8, 6)),
////      SandTile(GridPos(7, 1)),
////      SandTile(GridPos(8, 1)),
////      SandTile(GridPos(8, 2)),
////      SandTile(GridPos(8, 7)),
////      SandTile(GridPos(8, 8)),
////      SandTile(GridPos(8, 9)),
////      SandTile(GridPos(7, 9)),
////      ForestTile(GridPos(6, 4)),
////      ForestTile(GridPos(4, 5)),
////      ForestTile(GridPos(5, 5)),
////      ForestTile(GridPos(6, 5)),
////      ForestTile(GridPos(7, 5)),
////      ForestTile(GridPos(5, 6)),
////      ForestTile(GridPos(6, 6)),
////      ForestTile(GridPos(6, 7)),
////      ForestTile(GridPos(7, 7)),
////      ConquestTile(GridPos(8, 5))
////  ), "origin"),
////
////
////
/////** Player properties */
////// Player 1
////val Player1BattleUnitsFormation =
////  Vector[BattleUnit](
////    TankUnit(GridPos(2, 1), East, true),
////    TankUnit(GridPos(1, 2), East, true),
////    TankUnit(GridPos(7, 8), East, true),
////    SniperUnit(GridPos(11, 1), East, true),
////    SoldiersUnit(GridPos(8, 5), East, true)
////),
////val Player1Color: Color,
////
////// Player 2
////val Player2BattleUnitsFormation =
////  Vector[BattleUnit](
////    TankUnit(GridPos(15, 1), West, false),
////    TankUnit(GridPos(16, 2), West, false),
////    TankUnit(GridPos(9, 8), West, false),
////    TankUnit(GridPos(7, 6), West, false),
////    TankUnit(GridPos(5, 8), West, false),
////    SoldiersUnit(GridPos(4, 1), West, false),
////    SniperUnit(GridPos(2, 3), North, false)
////),
////val Player2Color: Color,
////
////
////
/////** GamePlay properties */
////val ConquestTarget: Int,
////val DefendStrength: Int
//):
//
//  val defaultGameConfig = GameConfig(
//    GameWindowWidth,
//    GameWindowHeight,
//    RightPaneWidth,
//    BottomPaneHeight,
//
//    // Component properties
//    SelectionRectangleThickness,
//
//    // Colors
//    TextColor                    ,
//    PrimaryActionHighlightColor  ,
//    SecondaryActionHighlightColor,
//    PrimaryHighlightColor        ,
//    SecondaryHighlightColor      ,
//    HealthBarHealthyColor        ,
//    HealthBarCriticalColor       ,
//    BarBackgroundColor           ,
//
//    RightPaneBackgroundStyle     ,
//    BottomPaneBacgroundStyle     ,
//
//    // Fonts
//    HeadingFont                  ,
//    DefaultFont                  ,
//    PopUpHeadingFont             ,
//    PopUpButtonFont              ,
//
//    // Values
//    DefaultSpacing               ,
//    LayoutInset                  ,
//    DefaultLeftMargin            ,
//    DropdownWidth                ,
//    DropdownHeight               ,
//    HealthBarCriticalThreshold   ,
//    BarRounding                  ,
//    ProgressBarWidth             ,
//    ProgressBarHeight            ,
//    CounterWidth                 ,
//
//
//    /** Text */
//    GameTitle               ,
//    SelectedUnitDefault     ,
//    PlayTurnButton          ,
//    SetActionSetButton      ,
//    PrimaryAction           ,
//    PrimaryTargetSelection  ,
//    SecondaryAction         ,
//    SecondaryTargetSelection,
//    Health                  ,
//    Experience              ,
//    Ammo                    ,
//    Fuel                    ,
//    Player1Score            ,
//    Player2Score            ,
//    PopUpRestartButton      ,
//    PopUpQuitButton         ,
//    Player1Win              ,
//    Player2Win              ,
//
//
//
//    /** TerrainTiles' properties' es are interpreted as percentag */
//    // Grass
//    GrassImage            ,
//    GrassFlatness         ,
//    GrassSolidity         ,
//    GrassVegetationDensity,
//    GrassElevation        ,
//
//    // Dirt
//    DirtImage            ,
//    DirtFlatness         ,
//    DirtSolidity         ,
//    DirtVegetationDensity,
//    DirtElevation        ,
//
//    // VegetativeDirt
//    VegetativeDirtImage             ,
//    VegetativeDirtFlatness          ,
//    VegetativeDirtSolidity          ,
//    VegetativeDirtVegetationDensity ,
//    VegetativeDirtElevation         ,
//
//    // Forest
//    ForestImage             ,
//    ForestFlatness          ,
//    ForestSolidity          ,
//    ForestVegetationDensity ,
//    ForestElevation         ,
//
//    // Rock
//    RockImage             ,
//    RockFlatness          ,
//    RockSolidity          ,
//    RockVegetationDensity ,
//    RockElevation         ,
//
//    // Gravel
//    GravelImage             ,
//    GravelFlatness          ,
//    GravelSolidity          ,
//    GravelVegetationDensity ,
//    GravelElevation         ,
//
//    // Sand
//    SandImage             ,
//    SandFlatness          ,
//    SandSolidity          ,
//    SandVegetationDensity ,
//    SandElevation         ,
//
//    NeutralConquestTileImage      ,
//    BlueConquestTileImage         ,
//    RedConquestTileImage          ,
//    ConquestTileFlatness          ,
//    ConquestTileSolidity          ,
//    ConquestTileVegetationDensity ,
//    ConquestTileElevation         
//
//
//
//  //  /** BattleUnit properties */
//  //  // Tank
//  //  Player1TankImage          ,
//  //  Player1DestroyedTankImage ,
//  //  Player2TankImage          ,
//  //  Player2DestroyedTankImage ,
//  //
//  //  TankUnitType        ,
//  //  TankWeight          ,
//  //  TankVolume          ,
//  //  TankRange           ,
//  //  TankArmor           ,
//  //  TankBaseDamage      ,
//  //  TankExplosiveDamage ,
//  //  TankMaxHealth       ,
//  //  TankDamageGradient,
//  //
//  //  TankAmmo   ,
//  //  TankFuel   ,
//  //  TankHealth ,
//  //
//  //  // Soldiers
//  //  Player1SoldiersImage          ,
//  //  Player1DestroyedSoldiersImage ,
//  //  Player2SoldiersImage          ,
//  //  Player2DestroyedSoldiersImage ,
//  //
//  //  SoldiersUnitType        ,
//  //  SoldiersWeight          ,
//  //  SoldiersVolume          ,
//  //  SoldiersRange           ,
//  //  SoldiersArmor           ,
//  //  SoldiersBaseDamage      ,
//  //  SoldiersExplosiveDamage ,
//  //  SoldiersMaxHealth       ,
//  //  SoldiersDamageGradient,
//  //
//  //  SoldiersAmmo   ,
//  //  SoldiersFuel   ,
//  //  SoldiersHealth ,
//  //
//  //  // Sniper
//  //  Player1SniperImage          ,
//  //  Player1DestroyedSniperImage ,
//  //  Player2SniperImage          ,
//  //  Player2DestroyedSniperImage ,
//  //
//  //  SniperUnitType        ,
//  //  SniperWeight          ,
//  //  SniperVolume          ,
//  //  SniperRange           ,
//  //  SniperArmor           ,
//  //  SniperBaseDamage      ,
//  //  SniperExplosiveDamage ,
//  //  SniperMaxHealth       ,
//  //  SniperDamageGradient,
//  //
//  //  SniperAmmo   ,
//  //  SniperFuel   ,
//  //  SniperHealth
//  ).asJson