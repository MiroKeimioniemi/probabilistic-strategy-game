import o1.grid.GridPos

class ActionSet(
  val primaryAction: Action,
  val primaryTarget: GridPos,
  val secondaryAction: Action,
  val secondaryTarget: GridPos
):
  
  var primaryActionAttempted   = false
  var primaryActionSuccess     = false
  var secondaryActionAttempted = false
  var secondaryActionSuccess   = false
  
end ActionSet

