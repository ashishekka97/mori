# Tutorial: "The Childhood Canvas" (Papercraft Scenery)

---
**Documentation Suite:** [Architecture](ARCHITECTURE.md) | [Biome DSL](SPEC_DSL.md) | [Instruction Set (ISA)](SPEC_ISA.md) | **[Tutorial](TUTORIAL_CANVAS.md)**
---

Welcome to the Mori Artist's Guide. In this tutorial, you will build a "Hello World" dynamic papercraft scenery. You will learn how to use the Mori Biome DSL, respect the strict power budget, and bind real-world sensor data to high-performance visual pipelines.

---

## The Power Budget & Primitives
Mori is a zero-allocation engine designed for 60fps and minimal battery drain. 
The Rule of 64: You have a strict limit of 64 layers per biome.
*   **The Solution:** Build complex scenes using basic overlapping primitives (`RECT`, `CIRCLE`, `TRIANGLE`) and high-performance `PATH` resources.

## Chapter 1: The Sky & Celestial Orbit (Coordinate Space & Facts)
Learn how to place elements in the 1000x1000 **Virtual Artboard** and connect them to real-world sensors.
*   **Concepts:** `x`, `y`, `fact[2]` (Day Progress), `sin`, `cos`, `mix_oklab`.
*   **Goal:** A sky background that transitions from `#1A1A2E` (Night) to `#A8DADC` (Day). We place the **Sun** and **Moon** on a shared circular orbit using `zOrder: -95` so they pass behind the mountains but in front of the sky.

```json
{
  "id": 2,
  "type": "CIRCLE",
  "zOrder": -95,
  "expressions": {
    "x": "500 + cos((fact[2] * 6.28318) - 2.35619) * 450",
    "y": "500 - sin((fact[2] * 6.28318) - 2.35619) * 450",
    "width": "160",
    "color_primary": "#F4A261",
    "alpha": "if_gt(fact[1], 0.0, 1.0, clamp(remap(fact[1], -0.1, 0.0, 0.0, 1.0), 0.0, 1.0))"
  }
}
```

## Chapter 2: The Ground & Mountains (Z-Order & Aspect Ratio)
Understand depth, geometric shapes, and responsive anchoring.
*   **Concepts:** `zOrder`, `TRIANGLE`, `fact[24]` (Aspect Ratio).
*   **Goal:** Layering static mountain silhouettes behind the foreground. We anchor the ground `RECT` to the physical bottom using the **Aspect Ratio Fact** (`fact[24]`).

```json
{
  "id": 7,
  "type": "RECT",
  "zOrder": -50,
  "expressions": {
    "x": "500",
    "y": "fact[24] * 500 + 550",
    "width": "1000", "height": "2000",
    "color_primary": "#8AB17D"
  }
}
```

## Chapter 3: The House (Logical Toggles)
Introduction to boolean logic and conditional rendering.
*   **Concepts:** `if_gt`, `fact[16]` (Do Not Disturb).
*   **Goal:** Add windows that "light up" (change color) based on the user's Do Not Disturb mode or time of day.

```json
{
  "id": 21,
  "type": "CIRCLE",
  "zOrder": 11,
  "expressions": {
    "x": "220", "y": "670", "width": "30",
    "color_primary": "if_gt(fact[16], 0.5, #1A1A2E, #FFFF88)",
    "alpha": "if_gt(fact[1], 0.1, 0.3, 1.0)"
  }
}
```

## Chapter 4: The Tree (Vitality & Animation)
Bringing life to static objects with health data and motion logic.
*   **Concepts:** `fact[9]` (Steps Progress), `oscillate`, `lerp`.
*   **Goal:** The tree foliage grows as you walk (`lerp` with `fact[9]`) and sways gently in the wind. We use a stacked `TRIANGLE` approach for a classic papercraft pine look.

```json
{
  "id": 14,
  "type": "TRIANGLE",
  "zOrder": 30,
  "expressions": {
    "x": "750 + oscillate(0, 10, 0.5, 0)",
    "y": "600",
    "width": "lerp(150, 350, fact[9])",
    "height": "lerp(100, 200, fact[9])",
    "color_primary": "#4F772D"
  }
}
```

## Chapter 5: The River, Smoke, & Shaders
Harnessing the power of the GPU for complex vector geometry and custom materials.
*   **Concepts:** `PATH` and `SHADER` layer types, `resId`, `time`.
*   **Goal:** The river (`resId: 1`) and chimney smoke (`resId: 2`) use `PATH` resources. The smoke's vertical position and transparency are driven by `time`. Finally, we apply a custom `SHADER` material over the river to create procedural water reflections using AGSL.

```json
[
  {
    "id": 12,
    "type": "PATH",
    "resId": 2,
    "zOrder": 20,
    "expressions": {
      "x": "250 + oscillate(0, 15, 0.4, 0)",
      "y": "500 - (time % 4) * 40",
      "stroke_width": "8",
      "color_secondary": "#F1FAEE",
      "color_primary": "#00000000",
      "alpha": "(1.0 - (time % 4) / 4.0) * if_gt(fact[7], 0.5, 1.0, 0.0)"
    }
  },
  {
    "id": 64,
    "type": "SHADER",
    "resId": 3,
    "maskId": 1,
    "zOrder": -39,
    "expressions": {
      "x": "660",
      "y": "1240",
      "width": "1000",
      "height": "1500",
      "color_primary": "#457B9D",
      "alpha": "0.7"
    }
  }
]
```

---

## Chapter 6: High-Fidelity Techniques
Pushing the visual boundaries using math instead of memory.

### 6.1 Looping Motion (The Clouds)
To create elements that drift endlessly, use the modulo operator `%` on `time`.
```json
{
  "id": 50,
  "type": "CIRCLE",
  "expressions": {
    "x": "(((time * 8) % 1500) - 250)",
    "y": "300 + oscillate(-5, 5, 0.2, 0)",
    "width": "60",
    "color_primary": "#FFFFFF"
  }
}
```

### 6.2 Signal Optimization (Thermal Snow)
If multiple layers share complex logic, calculate it **once** in a background layer and read it via `signal[n]`. In our demo, we use `signal[0]` to make mountain snow melt as the device temperature (`fact[8]`) rises.
```json
// In Layer 1 (Sky):
"signal[0]": "1.0 - fact[8]"

// In Layer 25 (Snow Cap):
"alpha": "signal[0]"
```

### 6.3 Clipping Masks (The River Shimmer)
Use `maskId` to constrain an effect (like a `SHADER` or a gradient `RECT`) to a specific `PATH`. This ensures the water shimmer never bleeds onto the grass.
*   **Procedural Bloom:** Stack semi-transparent `CIRCLE`s behind light sources (like the Sun) with increasing widths and decreasing alphas.
*   **Parallax Depth:** Use `mix_oklab` to blend background `color_primary` with the sky color based on depth to create atmospheric fog.
*   **Wind Simulation:** Use `oscillate()` on the `rotation` property of foliage layers to breathe life into the scene.

```json
{
  "id": 1,
  "type": "RECT",
  "expressions": {
    "signal[0]": "1.0 - fact[8]"
  }
}
```


## Summary of the Artist's Workflow
1.  **Define Resources:** Add your raw SVG path strings to the `resources` block.
2.  **Layer the Scene:** Arrange your 64 layers from back to front using `zOrder`.
3.  **Bind the Senses:** Use `fact[n]` to make properties (color, scale, position) react to the world.
4.  **Verify:** Ensure all math is zero-allocation and stays within the 1000x1000 artboard (or uses Over-scan for backgrounds).
