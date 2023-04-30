enum Action(val targetless: Boolean):
  case Move extends Action(targetless = false)
  case Attack extends Action(targetless = false)
  case Defend extends Action(targetless = true)
  case Reload extends Action(targetless = true)
  case Rest extends Action(targetless = true)
end Action
