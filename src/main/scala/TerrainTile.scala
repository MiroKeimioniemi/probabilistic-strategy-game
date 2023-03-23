import scala.math.max
import scala.math.min
import scala.util.Random
import o1.grid.GridPos

sealed trait TerrainTile(gridPos: GridPos):

  val position: GridPos = gridPos
  
  var flatness: Int
  var solidity: Int
  var vegetationDensity: Int
  var elevation: Int

  def degrade(damage: Int): Unit

end TerrainTile

case class GrassTile(gridPos: GridPos) extends TerrainTile(gridPos):

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

case class RockTile(gridPos: GridPos) extends TerrainTile(gridPos):

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

case class SandTile(gridPos: GridPos) extends TerrainTile(gridPos):

  var flatness = SandFlatness
  var solidity = SandSolidity
  var vegetationDensity = SandVegetationDensity
  var elevation = SandElevation

  // TODO: Refactor the method to not contain any magic numbers ------------- !
  def degrade(damage: Int) =
    flatness = min(100, flatness * Random().nextInt(10) / Random().nextInt(10))
    solidity = solidity
    vegetationDensity = 0
    elevation = min(100, elevation * Random().nextInt(10) / Random().nextInt(10))
  end degrade

end SandTile