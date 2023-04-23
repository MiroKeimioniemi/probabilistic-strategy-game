import o1.grid.CompassDir.*
import o1.grid.GridPos

class Player(val name: String, initialBattleUnits: Vector[BattleUnit]):

  var battleUnits: Vector[BattleUnit] = initialBattleUnits

  var winProgress = 0

end Player
