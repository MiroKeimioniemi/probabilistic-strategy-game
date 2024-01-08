# Probabilistic Strategy Game

## General Description

The untitled strategy game is a probabilistic battle simulator, aiming to capture the
uncertainty of traversing and engaging in combat on unknown territory against
unknown enemies. The game mode is conquest, where two players compete for
dominance over the objective tiles (dark grey dotted ones in the middle of the GUI
picture) until either one has controlled them for a total of 100 turns with various types
of units such as foot soldiers, tanks and snipers with different attributes such as
damage, weight, size, range and health. These attributes determine the probabilities of
a given unit being able to enter a given terrain and triumphing over another in battle.

To give the player some agency and a chance to show and develop their strategic
competency as the commander of their troops, at the very core of the game is the idea
of conditional actions consisting of primary and secondary actions and their respective
targets. Each action target has an associated probability calculated based on the type of
action and attributes of both the agent and the target. (These will be discussed in more
detail in the algorithms section) The player may select any of their battle units by left
clicking it with their mouse and choose a primary action for it from the dropdown menu
on the right GUI pane, that gets highlighted in grey on the grid as shown in the picture
below. This instantly prompts the player to choose a secondary action as well, which is
highlighted in a light brown shade.

Both actions are by default “Move” but may be mixed and matched in any combination.
Target selection for the primary action is highlighted in red and secondary target
selection is highlighted in yellow. Once both actions and targets (coordinates next to
selected actions) are selected, the “Set Action set” button can be clicked to save the
action set for the selected battle unit. Once action sets are set for all desired units, “Play
Turn” calculates the outcomes and updates the game state accordingly. Only if the
primary action fails is the secondary action even attempted. It is entirely possible that
both actions fail, whereas only one can ever succeed at once. Every unit can be assigned
an action set during a turn so that with the default game launch configuration at most
seven action sets will be executed at once. 

Attacking an enemy battle unit always launches a duel where, based on the unit types,
their experience and damage gradients (distance between), the loser suffers all the
damage, whereas the winner stays unscathed. Attacking a terrain tile on the other hand
degrades it so that its attributes change, making it easier or harder to traverse. If
sufficient explosive damage is dealt to, for example, a rock, it will turn into gravel. The
environment is therefore destructible. However, only tanks can degrade rocks.
Moving simply places the battle unit in the new coordinates within its range. However, if
these contain another battle unit, should the move be successful, the moving unit will
ram them and be placed on top of the now destroyed enemy unit.
Defend activates a temporary effect that reduces the damage taken by the defending
battle unit to half in case it loses a battle while defending. The effect will persist until it
moves or attacks something proactively.

The other two actions are Rest and Reload, replacing the supply chain mechanic, which
was foregone due to lack of time and potentially resulting in overcrowding of the GUI.
Rest restores a battle unit’s fuel to its maximum while staying still for one round and
Reload restores its ammo to its maximum such that there is an aspect of resource
management as well.

## User Interface

<img width="1000" alt="Probabilistic Strategy Game UI" src="https://github.com/MiroKeimioniemi/probabilistic-strategy-game/assets/65595542/e57204cf-37b1-44b4-ae0b-d187693166e4">

Selected actions are highlighted in the game map upon
unit selection. First, only the primary action in light grey and after selecting its target,
highlighted in red, the range of the secondary action is highlighted in light brown as well
and its target is highlighted in yellow upon selection.

In the battle unit pane on the right, there are five labels on the top displaying the
selected unit’s type, health, experience, ammo and fuel in that order. Ammo or fuel
running out is highlighted in red. Primary and secondary actions are selected using the
dropdowns with the second one becoming accessible only after primary action has
been set. Their target coordinates are shown next to them and “Set action set” saves the
current selection to the battle unit such that it can be overwritten any time by just
selecting new actions and pressing it again. Changing between the action currently
being selected can be done by clicking on the dropdowns with or without selecting a
new action or by clicking the battle unit twice to reset the selections.

In the bottom pane, there is a “Play Turn” button that, upon clicking, processes all the
set action sets and updates the GUI and game state. Next to it is a turn counter followed
by both player’s current scores and their visualizations in the forms of bars filling up
with the same color as their battle units. Below them are radio buttons whose states
signal to the “AI” Player whether it is expected to move that player’s units. This means
that the player may choose which units they want to control or if they just want to
observe the AI Player play against itself. This also makes intervening possible at any
point by simply unchecking one of them and continuing the game as normal.

The GUI is dynamically scalable when the map is sufficiently wider than it is tall. This is
due to a very simplistic and quick implementation to mostly just scale up to larger
monitors without considering scaling down. However, one visual bug that I did not have
time to fix remains, where the health bar margins only get updated after pressing “Play
Turn” instead of immediately upon rescaling. This is not much of an issue scaling up
because they are just a bit off-center and will get fixed rather quickly but majorly scaling
down, the too large lingering margins can cause the neighboring tiles to be pushed
away due to overflow. This is also fixed within one turn but is significantly more
disturbing than the other way around.

## Configuring The Game

The game can be configured via a custom human readable launch configuration text file in 
"src/main/resources/launch-configuration/", which is structured such that the instructions
for its safe modification are written on top and the actual space-separated configuration is
parsed such that the first non-space character after the dashed line will be interpreted as
the first character of the first map tile. This is then read one row at a time into a map 
with width equal to the number of words on the first line and height equal to the number
of lines.

<img width="1000" alt="Probabilistic Strategy Game configuration file" src="https://github.com/MiroKeimioniemi/probabilistic-strategy-game/assets/65595542/590bd23d-1c94-44c1-bf12-3387e2653094">


The launch configuration represents the map and initial placements of units in the
game with units being specified on top their starting tiles, separated from them only by
a comma (no spaces). It may be modified so that each continuous word representing a
TerrainTile and a possible BattleUnit (_ or _,_) is separated by at least one space
character. The number of words in the first row gives the map width and the number of
rows gives its height. Any incomplete rows will be padded with the top left type of tile.
Units on the left belong to Player 1 and units on the right belong to Player 2. For
standard heap memory size, maximum map size is approximately 18 * 11 with a total of
24 battleUnits, meaning that for safety, launch configuration should not contain much
more than 220 elements. There must be more rows than columns and Each row must
have the same length with its words each being one of the following:
- Grass
- Forest
- Rock
- Sand
- Dirt
- VegeDirt
- Gravel
- Objective (conquering of which will gain points towards winning)

A Battle unit can be placed on top of a Terrain tile by writing a comma after the latter
and then writing one of the following:
- Soldiers
- Sniper
- Tank

The file is read out of a directory ("src/main/resources/launch-configuration/" by
default) that is assumed to only ever contain a single file, so that the file’s name does
not matter. The project is shipped with two launch-configuration files that are meant to
be swapped in and out of the default folder for the game to be initialized with its
contents. The configurations folder is meant for storage of custom launch
configurations and launch-configuration is meant to host and use the currently active
one. If the launch-configuration folder is empty or contains an illegally formatted file,
the game defaults to the launch configuration defined in ConfigurationConstants.

## More Info

Run the GUI class to run the game.

Read more about the implementation details in [Strategy Game Final Report](https://github.com/MiroKeimioniemi/probabilistic-strategy-game/blob/master/Strategy%20Game%20Final%20Report.pdf).

<img width="1000" alt="Probabilistic Strategy Game UML Diagram" src="https://github.com/MiroKeimioniemi/probabilistic-strategy-game/assets/65595542/a6b38dcd-e589-42d8-a9ce-ea3a51519df4">

