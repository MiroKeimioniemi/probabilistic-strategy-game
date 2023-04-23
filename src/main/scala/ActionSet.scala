import o1.grid.GridPos

class ActionSet(
  val primaryAction: Action,
  val primaryTarget: GridPos,
  val secondaryAction: Action,
  val secondaryTarget: GridPos
):

  var primaryActionSuccess = false
  
end ActionSet

