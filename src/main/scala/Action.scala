enum Action:
  case Move
  case Stay
  case Attack
  case Defend
  case Patrol
end Action

enum Condition:
  case MoveSuccess
  case MoveFailure
  case BattleWon
  case BattleLost
  case EnemyInRange
end Condition
