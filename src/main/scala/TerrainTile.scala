import scala.math.max
import scala.math.min

trait TerrainTile:

  var flatness: Int
  var solidity: Int
  var vegetationDensity: Int
  var elevation: Int

  def degrade(damage: Int): Unit

end TerrainTile

class GrassTile extends TerrainTile:

  var flatness = GrassFlatness
  var solidity = GrassSolidity
  var vegetationDensity = GrassVegetationDensity
  var elevation = GrassElevation

  // TODO: Refactor the method to not contain any magic numbers ------------- !
  def degrade(damage: Int) =
    flatness = max(0, flatness - damage)
    solidity = max(0, solidity - damage)
    vegetationDensity = 0
    elevation = 0
  end degrade

end GrassTile

class RockTile extends TerrainTile:

  var flatness = RockFlatness
  var solidity = RockSolidity
  var vegetationDensity = RockVegetationDensity
  var elevation = RockElevation
  
  // TODO: Refactor the method to not contain any magic numbers ------------- !
  def degrade(damage: Int) =
    flatness = min(100, flatness + damage)
    solidity = max(0, solidity - (damage / 10))
    vegetationDensity = 0
    elevation = max(0, elevation - damage)
  end degrade

end RockTile