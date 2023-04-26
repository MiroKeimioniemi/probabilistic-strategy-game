import scala.math.max
import scala.math.min
import scala.util.Random
import o1.grid.GridPos
import scalafx.scene.image.Image
import java.io.FileInputStream

sealed trait TerrainTile(gridPos: GridPos):

  var image: String
  val position: GridPos = gridPos

  var flatness:          Int
  var solidity:          Int
  var vegetationDensity: Int
  var elevation:         Int

  def degrade(damage: Int): Unit
  def copySelf(newPosition: GridPos): TerrainTile

end TerrainTile

case class GrassTile(gridPos: GridPos) extends TerrainTile(gridPos):

  var image = "src/main/resources/grass-tile.png"

  var flatness =          GrassFlatness
  var solidity =          GrassSolidity
  var vegetationDensity = GrassVegetationDensity
  var elevation =         GrassElevation

  // TODO: Refactor the method to not contain any magic numbers ------------- !
  def degrade(damage: Int) =
    flatness =          max(75, flatness - damage)
    solidity =          max(75, solidity - damage)
    vegetationDensity = 0
    elevation =         0
    if solidity < 80 then
      image = "src/main/resources/dirt-tile.png"
  end degrade

  def copySelf(newPosition: GridPos): GrassTile =
    GrassTile(newPosition)

end GrassTile

case class ForestTile(gridPos: GridPos) extends TerrainTile(gridPos):

  var image = "src/main/resources/forest-tile.png"

  var flatness =          ForestFlatness
  var solidity =          ForestSolidity
  var vegetationDensity = ForestVegetationDensity
  var elevation =         ForestElevation

  // TODO: Refactor the method to not contain any magic numbers ------------- !
  def degrade(damage: Int) =
    flatness =          max(25, flatness + damage)
    solidity =          max(50, solidity - damage)
    vegetationDensity = max(0, vegetationDensity - damage)
    elevation =         0
    if vegetationDensity < 25 then
      image = "src/main/resources/degraded-grass-tile.png"
  end degrade

  def copySelf(newPosition: GridPos): ForestTile =
    ForestTile(newPosition)

end ForestTile

case class RockTile(gridPos: GridPos) extends TerrainTile(gridPos):

  var image = "src/main/resources/rock-tile.png"

  var flatness =          RockFlatness
  var solidity =          RockSolidity
  var vegetationDensity = RockVegetationDensity
  var elevation =         RockElevation
  
  // TODO: Refactor the method to not contain any magic numbers ------------- !
  def degrade(damage: Int) =
    flatness =          min(100, flatness + damage)
    solidity =          max(0, solidity - (damage / 10))
    vegetationDensity = 0
    elevation =         max(0, elevation - damage)
    if elevation < 50 then
      image = "src/main/resources/gravel-tile.png"
  end degrade

  def copySelf(newPosition: GridPos): RockTile =
    RockTile(newPosition)

end RockTile

case class SandTile(gridPos: GridPos) extends TerrainTile(gridPos):

  var image = "src/main/resources/desert-tile.png"

  var flatness =          SandFlatness
  var solidity =          SandSolidity
  var vegetationDensity = SandVegetationDensity
  var elevation =         SandElevation

  // TODO: Refactor the method to not contain any magic numbers ------------- !
  def degrade(damage: Int) =
    flatness =          min(100, flatness * Random().nextInt(11) / (Random().nextInt(10) + 1))
    solidity =          solidity
    vegetationDensity = 0
    elevation =         min(100, elevation * Random().nextInt(11) / (Random().nextInt(10) + 1))
  end degrade

  def copySelf(newPosition: GridPos): SandTile =
    SandTile(newPosition)

end SandTile