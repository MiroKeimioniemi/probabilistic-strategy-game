import o1.grid.CompassDir.*
import o1.grid.{CompassDir, GridPos}
import scalafx.scene.image.Image
import java.io.FileInputStream

sealed trait BattleUnit(initialGridPos: GridPos, initialFacing: CompassDir):
  
  var image:           String
  val deadImage:       String
  val unitType:        String

  val weight:          Int
  val volume:          Int
  val range:           Int
  val armor:           Int
  val baseDamage:      Int
  val explosiveDamage: Boolean
  val damageGradient:  LazyList[Double]
  val maxHealth:       Int
  val maxAmmo:         Int
  val maxFuel:         Int

  var alive:           Boolean             = true
  var defending:       Boolean             = false
  var position:        GridPos             = initialGridPos
  var orientation:     CompassDir          = initialFacing
  var experience:      Int                 = 0
  var ammo:            Int
  var fuel:            Int
  var health:          Int
  var supplyChain:     Option[SupplyChain]

  var actionSet: ActionSet = ActionSet(Action.Stay, position, Action.Stay, position)

  def setActionSet(primaryAction: Action = Action.Stay,
                   primaryTarget: GridPos = position,
                   secondaryAction: Action = Action.Stay,
                   secondaryTarget: GridPos = position
                   ): Unit =
    actionSet = new ActionSet(primaryAction, primaryTarget, secondaryAction, secondaryTarget)

  def useAmmo(q: Int): Unit =
    ammo -= q

  def useFuel(q: Int): Unit =
    fuel -= q

  def takeDamage(q: Int): Unit =
    health -= q
    if health <= 0 then
      alive = false
      image = deadImage

  def gainExperience(q: Int): Unit =
    experience += q

  def defend(): Unit =
    defending = true

end BattleUnit



case class TankUnit(initialGridPos: GridPos, initialFacing: CompassDir, player1Unit: Boolean) extends BattleUnit(initialGridPos, initialFacing: CompassDir):
  
  var image           = if player1Unit then Player1TankImage else Player2TankImage
  val deadImage       = if player1Unit then Player1DestroyedTankImage else Player2DestroyedTankImage
  val unitType        = TankUnitType

  val weight          = TankWeight
  val volume          = TankVolume
  val range           = TankRange
  val armor           = TankArmor
  val baseDamage      = TankBaseDamage
  val explosiveDamage = TankExplosiveDamage
  val damageGradient  = TankDamageGradient
  val maxHealth       = TankHealth
  val maxAmmo         = TankAmmo
  val maxFuel         = TankFuel

  var ammo            = maxAmmo
  var fuel            = maxFuel
  var health          = maxHealth
  var supplyChain     = None

end TankUnit

case class SoldiersUnit(initialGridPos: GridPos, initialFacing: CompassDir, player1Unit: Boolean) extends BattleUnit(initialGridPos, initialFacing: CompassDir):
  
  var image           = if player1Unit then Player1SoldiersImage else Player2SoldiersImage
  val deadImage       = if player1Unit then Player1DestroyedSoldiersImage else Player2DestroyedSoldiersImage
  val unitType        = SoldiersUnitType

  val weight          = SoldiersWeight
  val volume          = SoldiersVolume
  val range           = SoldiersRange
  val armor           = SoldiersArmor
  val baseDamage      = SoldiersBaseDamage
  val explosiveDamage = SoldiersExplosiveDamage
  val damageGradient  = SoldiersDamageGradient
  val maxHealth       = SoldiersHealth
  val maxAmmo         = SoldiersAmmo
  val maxFuel         = SoldiersFuel

  var ammo            = maxAmmo
  var fuel            = maxFuel
  var health          = maxHealth
  var supplyChain     = None

end SoldiersUnit

case class SniperUnit(initialGridPos: GridPos, initialFacing: CompassDir, player1Unit: Boolean) extends BattleUnit(initialGridPos, initialFacing: CompassDir):
  
  var image           = if player1Unit then Player1SniperImage else Player2SniperImage
  val deadImage       = if player1Unit then Player1DestroyedSniperImage else Player2DestroyedSniperImage
  val unitType        = SniperUnitType

  val weight          = SniperWeight
  val volume          = SniperVolume
  val range           = SniperRange
  val armor           = SniperArmor
  val baseDamage      = SniperBaseDamage
  val explosiveDamage = SniperExplosiveDamage
  val damageGradient  = SniperDamageGradient
  val maxHealth       = SniperHealth
  val maxAmmo         = SniperAmmo
  val maxFuel         = SniperFuel

  var ammo            = maxAmmo
  var fuel            = maxFuel
  var health          = maxHealth
  var supplyChain     = None

end SniperUnit