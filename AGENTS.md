# Nine Men's Morris Library Guide

## Overview
This Kotlin Multiplatform library implements Nine Men's Morris game logic with AI. Key classes: `Position` (board state), `Movement` (actions), `GameState` (phases). Supports serialization for persistence.

## Quick Start
Import the library:
```kotlin
import com.kroune.nineMensMorrisLib.*
```

Start a game:
```kotlin
var position = gameStartPosition
val bestMove = position.findBestMove(10u)
if (bestMove != null) {
    position = bestMove.producePosition(position)
}
```

## Key Concepts
### Board Representation
24 positions indexed 0-23. Use constants:
- `GREEN` (true): First player
- `BLUE_` (false): Second player
- `EMPTY` (null): Empty spot

Board layout for reference:
```
0-----------------1-----------------2
|                 |                 |
|     3-----------4-----------5     |
|     |           |           |     |
|     |     6-----7-----8     |     |
|     |     |           |     |     |
9-----10----11          12----13----14
|     |     |           |     |     |
|     |     15----16----17    |     |
|     |           |           |     |
|     18----------19----------20    |
|                 |                 |
21----------------22----------------23
```

### Game Phases (`GameState`)
- `Placement`: Placing pieces
- `Normal`: Moving adjacent
- `Flying`: Moving anywhere (when <3 pieces)
- `Removing`: Removing opponent's pieces after mill
- `End`: Game over

### Movements
`Movement(startIndex: Int?, endIndex: Int?)`:
- Placement: `startIndex = null`, `endIndex = target`
- Move: `startIndex = from`, `endIndex = to`
- Removal: `startIndex = piece`, `endIndex = null`

## Creating Positions
Default start:
```kotlin
val position = gameStartPosition
```

Custom (use `@formatter:off/on` for readability):
```kotlin
val position = Position(
    // @formatter:off
    positions = arrayOf(
        GREEN,                  EMPTY,                  EMPTY,
                BLUE_,          EMPTY,          EMPTY,
                        EMPTY,  BLUE_,  EMPTY,
        EMPTY,  EMPTY,  EMPTY,          EMPTY,  EMPTY,  EMPTY,
                        EMPTY,  EMPTY,  EMPTY,
                EMPTY,          EMPTY,          EMPTY,
        EMPTY,                  EMPTY,                  EMPTY
    ),
    // @formatter:on
    freeGreenPieces = 7u,
    freeBluePieces = 8u,
    pieceToMove = true,
    removalCount = 0u
)
```

Alternative constructor uses `Int` without `u` suffix.

## Core API
- `position.gameState()`: Returns current `GameState`
- `position.generateMoves()`: List of legal `Movement`s
- `movement.producePosition(position)`: New `Position` after move (immutable)
- `position.evaluate()`: Score (positive: green advantage)
- `position.findBestMove(depth: UByte)`: Best `Movement` using minimax (depth 5-6 recommended)
- `Cache.wipeCache()`: Clear cache between games

## AI Integration
For AI player:
```kotlin
val bestMove = position.findBestMove(5u)
position = bestMove?.producePosition(position) ?: position  // Handle no move
```

AI uses alpha-beta pruning and transposition tables.

## Example Game Loop
```kotlin
var position = gameStartPosition
while (position.gameState() != GameState.End) {
    val moves = position.generateMoves()
    if (moves.isEmpty()) break
    val move = if (position.pieceToMove) {
        // User or AI move for green
        position.findBestMove(5u)
    } else {
        position.findBestMove(5u)
    }
    position = move?.producePosition(position) ?: break
}
```

## Win Conditions
Game ends if player has <3 pieces or no moves. Check `position.gameState() == GameState.End`.

## Notes
- Immutable: Always use returned `Position`.
- Depths: 5-7 for strong play.
- Compatible: JVM, Android, iOS, JS, Native.
- Serialization: All types support kotlinx.serialization.