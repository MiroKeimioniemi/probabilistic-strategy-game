import o1.grid.CompassDir.*
import o1.grid.{CompassDir, GridPos}
import scalafx.scene.image.Image
import java.io.FileInputStream

sealed trait BattleUnit(initialGridPos: GridPos, initialFacing: CompassDir):
  
  var image:          String
  val deadImage:      String
  val unitType:       String

  val weight:         Int
  val volume:         Int
  val range:          Int
  val armor:          Int
  val baseDamage:     Int
  val damageGradient: LazyList[Double]
  val maxHealth:      Int
  val maxAmmo:        Int
  val maxFuel:        Int

  var alive:          Boolean             = true
  var defending:      Boolean             = false
  var position:       GridPos             = initialGridPos
  var orientation:    CompassDir          = initialFacing
  var experience:     Int                 = 0
  var ammo:           Int
  var fuel:           Int
  var health:         Int
  var supplyChain:    Option[SupplyChain]

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

case class Player1TankUnit(initialGridPos: GridPos, initialFacing: CompassDir) extends BattleUnit(initialGridPos, initialFacing: CompassDir):
  
  var image          = "src/main/resources/blue-tank.png"
  val deadImage      = "src/main/resources/destroyed-blue-tank.png"
  val unitType       = "Tank"

  val weight         = TankWeight
  val volume         = TankVolume
  val range          = TankRange
  val armor          = TankArmor
  val baseDamage     = TankBaseDamage
  val damageGradient = TankDamageGradient
  val maxHealth      = TankHealth
  val maxAmmo        = TankAmmo
  val maxFuel        = TankFuel

  var ammo           = maxAmmo
  var fuel           = maxFuel
  var health         = maxHealth
  var supplyChain    = None

end Player1TankUnit

case class Player2TankUnit(initialGridPos: GridPos, initialFacing: CompassDir) extends BattleUnit(initialGridPos, initialFacing: CompassDir):

  var image          = "src/main/resources/red-tank.png"
  val deadImage      = "src/main/resources/destroyed-red-tank.png"
  val unitType       = "Tank"

  val weight         = TankWeight
  val volume         = TankVolume
  val range          = TankRange
  val armor          = TankArmor
  val baseDamage     = TankBaseDamage
  val damageGradient = TankDamageGradient
  val maxHealth      = TankHealth
  val maxAmmo        = TankAmmo
  val maxFuel        = TankFuel

  var ammo           = maxAmmo
  var fuel           = maxFuel
  var health         = maxHealth
  var supplyChain    = None

end Player2TankUnit
