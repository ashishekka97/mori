---
name: biome-expert
description: Specialist in the Mori Biome Layer, Rule Engine (VM), JSON DSL, and high-performance property evaluation via stack-based bytecode execution.
---

# Mori Biome Expert

## Rule Engine (VM)
- **Stack-based VM**: Executes primitive `IntArray` bytecode using a pre-allocated `FloatArray` stack.
- **Zero-Allocation**: 100% zero-allocation status for the evaluation loop.
- **Property Buffer**: Writes results (X, Y, Scale, Alpha, etc.) into a fixed-size `FloatArray` (16 slots).
- **VRAM Model**: Uses "Flat Memory" bridge between the Rule Engine and the Renderers.

## Biome DSL
- JSON-based declarative configurations for visual behavior.
- Maps raw "Facts" (Sensor data) from Persona to visual "Properties" in the Engine.
- **Power Budget**: Maximum 64 layers per biome.
- **Coordinate Space**: 1000x1000 Virtual Reference Space.

## Key Functions (Macro-OpCodes)
- `remap(v, iL, iH, oL, oH)`: Maps a value from one range to another.
- `oscillate(center, amplitude, speed, phase)`: Creates a smooth repeating pulse.
- `mix_oklab(colorA, colorB, t)`: Interpolates colors in OKLab space.
- `noise(v)`: Deterministic pseudo-random hash.

## Key Files & Sources of Truth
- `biome/src/main/java/me/ashishekka/mori/biome/RuleEvaluator.kt`
- `biome/src/main/java/me/ashishekka/mori/biome/BiomeDecoder.kt`
- **DIRECTIVE:** You MUST use `grep_search` or `read_file` on `docs/SPEC_DSL.md` and `docs/SPEC_ISA.md` to verify exact syntax, available functions, and `fact[n]` array indices BEFORE writing or modifying Biome JSON logic. DO NOT guess the syntax.