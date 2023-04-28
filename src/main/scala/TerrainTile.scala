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

  var image = GrassImage

  var flatness =          GrassFlatness
  var solidity =          GrassSolidity
  var vegetationDensity = GrassVegetationDensity
  var elevation =         GrassElevation

  def degrade(damage: Int) =
    flatness =          max(DirtFlatness, flatness - damage)
    solidity =          max(DirtSolidity, solidity - damage)
    vegetationDensity = 0
    elevation =         0
    if solidity <= DirtSolidity then
      image = DirtImage
  end degrade

  def copySelf(newPosition: GridPos): GrassTile =
    GrassTile(newPosition)

end GrassTile

case class DirtTile(gridPos: GridPos) extends TerrainTile(gridPos):

  var image = DirtImage

  var flatness =          DirtFlatness
  var solidity =          DirtSolidity
  var vegetationDensity = DirtVegetationDensity
  var elevation =         DirtElevation

  def degrade(damage: Int) =
    flatness =          DirtFlatness
    solidity =          DirtSolidity
    vegetationDensity = DirtVegetationDensity
    elevation =         DirtElevation
  end degrade

  def copySelf(newPosition: GridPos): DirtTile =
    DirtTile(newPosition)

end DirtTile

case class ForestTile(gridPos: GridPos) extends TerrainTile(gridPos):

  var image = ForestImage

  var flatness =          ForestFlatness
  var solidity =          ForestSolidity
  var vegetationDensity = ForestVegetationDensity
  var elevation =         ForestElevation

  def degrade(damage: Int) =
    flatness =          max(ForestFlatness, min(100, flatness + damage))
    solidity =          max(VegetativeDirtSolidity, solidity - damage)
    vegetationDensity = max(VegetativeDirtVegetationDensity, vegetationDensity - damage)
    elevation =         0
    if vegetationDensity < (ForestVegetationDensity / 3) then
      image = VegetativeDirtImage
  end degrade

  def copySelf(newPosition: GridPos): ForestTile =
    ForestTile(newPosition)

end ForestTile

case class VegetativeDirtTile(gridPos: GridPos) extends TerrainTile(gridPos):

  var image = VegetativeDirtImage

  var flatness =          VegetativeDirtFlatness
  var solidity =          VegetativeDirtSolidity
  var vegetationDensity = VegetativeDirtVegetationDensity
  var elevation =         VegetativeDirtElevation

  def degrade(damage: Int) =
    flatness =          max(DirtFlatness, flatness - damage)
    solidity =          max(DirtSolidity, solidity - damage)
    vegetationDensity = 0
    elevation =         0
    if solidity <= DirtSolidity then
      image = DirtImage
  end degrade

  def copySelf(newPosition: GridPos): VegetativeDirtTile =
    VegetativeDirtTile(newPosition)

end VegetativeDirtTile

case class RockTile(gridPos: GridPos) extends TerrainTile(gridPos):

  var image = RockImage

  var flatness =          RockFlatness
  var solidity =          RockSolidity
  var vegetationDensity = RockVegetationDensity
  var elevation =         RockElevation

  def degrade(damage: Int) =
    flatness =          min(GrassFlatness, flatness + damage)
    solidity =          max(GravelSolidity, solidity - (damage / 10))
    vegetationDensity = 0
    elevation =         max(GravelElevation, elevation - damage)
    if elevation < (RockElevation / 2) then
      image = GravelImage
  end degrade

  def copySelf(newPosition: GridPos): RockTile =
    RockTile(newPosition)

end RockTile

case class GravelTile(gridPos: GridPos) extends TerrainTile(gridPos):

  var image = GravelImage

  var flatness =          GravelFlatness
  var solidity =          GravelSolidity
  var vegetationDensity = GravelVegetationDensity
  var elevation =         GravelElevation

  def degrade(damage: Int) =
    flatness =          GravelFlatness
    solidity =          GravelSolidity
    vegetationDensity = GravelVegetationDensity
    elevation =         GravelElevation
  end degrade

  def copySelf(newPosition: GridPos): GravelTile =
    GravelTile(newPosition)

end GravelTile

case class SandTile(gridPos: GridPos) extends TerrainTile(gridPos):

  var image = SandImage

  var flatness =          SandFlatness
  var solidity =          SandSolidity
  var vegetationDensity = SandVegetationDensity
  var elevation =         SandElevation

  def degrade(damage: Int) =
    flatness =          min(100, flatness * Random().nextInt(11) / (Random().nextInt(10) + 1))
    solidity =          solidity
    vegetationDensity = 0
    elevation =         min(100, elevation * Random().nextInt(11) / (Random().nextInt(10) + 1))
  end degrade

  def copySelf(newPosition: GridPos): SandTile =
    SandTile(newPosition)

end SandTile

case class ConquestTile(gridPos: GridPos) extends TerrainTile(gridPos):

  var image = NeutralConquestTileImage

  var flatness =          ConquestTileFlatness
  var solidity =          ConquestTileSolidity
  var vegetationDensity = ConquestTileVegetationDensity
  var elevation =         ConquestTileElevation

  def degrade(damage: Int) =
    flatness =          ConquestTileFlatness
    solidity =          ConquestTileSolidity
    vegetationDensity = ConquestTileVegetationDensity
    elevation =         ConquestTileElevation
  end degrade

  def copySelf(newPosition: GridPos): ConquestTile =
    ConquestTile(newPosition)

end ConquestTile