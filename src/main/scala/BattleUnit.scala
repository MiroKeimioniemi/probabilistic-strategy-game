import o1.grid.GridPos

trait BattleUnit(commandingPlayer: Player, gridPos: GridPos):

  val commander: Player = commandingPlayer
  val weight: Int
  val volume: Int
  val range: Int
  val armor: Int
  val damageGradient: LazyList[Int]

  var position: GridPos = gridPos
  var numOfUnits: Int
  var ammo: Int
  var fuel: Int
  var health: Int
  var experience: Int = 0
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

  def breakUnits(q: Int): Unit =
    numOfUnits -= q

  def useAmmo(q: Int): Unit =
    ammo -= q

  def useFuel(q: Int): Unit =
    fuel -= q

  def takeDamage(q: Int): Unit =
    health -= q

  def gainExperience(q: Int): Unit =
    experience += q

end BattleUnit

case class TankUnit(commandingPlayer: Player, gridPos: GridPos) extends BattleUnit(commandingPlayer, gridPos):

  val weight = TankWeight
  val volume = TankVolume
  val range = TankRange
  val armor = TankArmor
  val damageGradient = TankDamageGradient

  var numOfUnits = TankNumOfUnits
  var ammo = TankAmmo
  var fuel = TankFuel
  var health = TankHealth
  var supplyChain = None
  // TODO: Implement var actionSet: ActionSet

end TankUnit
