import o1.grid.CompassDir.*
import o1.grid.GridPos

class Player(val name: String):

  var battleUnits: Vector[BattleUnit] = Player1BattleUnitsFormation

  var winProgress = 0

end Player
