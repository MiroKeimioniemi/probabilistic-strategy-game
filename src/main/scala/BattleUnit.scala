import o1.grid.CompassDir.*
import o1.grid.{CompassDir, GridPos}

sealed trait BattleUnit(initialGridPos: GridPos, initialFacing: CompassDir):

  val weight:         Int
  val volume:         Int
  val range:          Int
  val armor:          Int
  val baseDamage:     Int
  val damageGradient: LazyList[Int]

  var position:    GridPos = initialGridPos
  var orientation: CompassDir = initialFacing
  var ammo:        Int
  var fuel:        Int
  var health:      Int
  var experience:  Int = 0
  var supplyChain: Option[SupplyChain]
  // TODO: Implement var actionSet: ActionSet

  def setActionSet(unconditionalAction: Action,
                   firstTargets: Option[Vector[TerrainTile]],
                   Condition: Boolean,
                   conditionSuccessAction: Action,
                   conditionFailureAction: Action,
                   secondTarget: Option[TerrainTile]
                   ): ActionSet =
    ???

  def useAmmo(q: Int): Unit =
    ammo -= q

  def useFuel(q: Int): Unit =
    fuel -= q

  def takeDamage(q: Int): Unit =
    health -= q

  def gainExperience(q: Int): Unit =
    experience += q

end BattleUnit

case class TankUnit(initialGridPos: GridPos, initialFacing: CompassDir) extends BattleUnit(initialGridPos, initialFacing: CompassDir):

  val weight =         TankWeight
  val volume =         TankVolume
  val range =          TankRange
  val armor =          TankArmor
  val baseDamage =     TankBaseDamage
  val damageGradient = TankDamageGradient

  var ammo =        TankAmmo
  var fuel =        TankFuel
  var health =      TankHealth
  var supplyChain = None
  // TODO: Implement var actionSet: ActionSet

end TankUnit
