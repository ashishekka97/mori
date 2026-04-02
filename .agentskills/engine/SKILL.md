---
name: engine-expert
description: Expert in the Mori Rendering Engine, focusing on zero-allocation performance, platform-agnostic graphics, and the three-phase rendering cycle (UPDATE, SYNTHESIZE, DRAW).
---

# Mori Engine Expert

## Core Principles
- **Zero-Allocation Mandate**: ABSOLUTELY NO allocations (`new`, `copy()`, `Rect()`, `Paint()`) inside the `drawFrame` loop.
- **Dumb Muscle**: The Engine should not know the semantic meaning of data; it only renders properties.
- **Platform Agnostic**: Uses `EngineCanvas` abstraction to decouple from Android's `Canvas`.

## Rendering Cycle
1. **UPDATE**: `MoriEngine` updates the `LayerManager`. Layers calculate internal logic.
2. **SYNTHESIZE**: `MoriWallpaper` aggregates `RendererPalette` contributions to determine the final UI theme.
3. **DRAW**: Renderers perform "dumb" drawing operations using consistent state results.

## Performance
- **Zero-Allocation**: No object creation during the frame cycle.
- **Cache Locality**: Renderers read directly from Property Buffers.
- **Cached Contributions**: Uses caching for `RendererPalette` to avoid allocations.

## Key Files & Sources of Truth
- `engine/src/main/java/me/ashishekka/mori/engine/MoriEngine.kt`
- `engine/src/main/java/me/ashishekka/mori/engine/canvas/EngineCanvas.kt`
- **DIRECTIVE:** Before making structural changes to the engine, you MUST consult `docs/ARCHITECTURE.md` to ensure alignment with the "Mori Machine" visual flow and the Zero-Meaning Design Principle.