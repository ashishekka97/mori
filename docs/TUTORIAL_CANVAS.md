# Tutorial: "The Childhood Canvas" (Papercraft Scenery)

Welcome to the Mori Artist's Guide. In this tutorial, you will build a "Hello World" dynamic papercraft scenery. You will learn how to use the Mori Biome DSL, respect the strict power budget, and bind real-world sensor data to high-performance visual pipelines.

---

## The Power Budget & Primitives
Mori is a zero-allocation engine designed for 60fps and minimal battery drain. 
*   **The Rule of 32:** You have a strict limit of 32 layers per biome.
*   **The Solution:** Build complex scenes using basic overlapping primitives (`RECT`, `CIRCLE`, `TRIANGLE`) and high-performance `PATH` resources.

## Chapter 1: The Sky & Sun (Coordinate Space & Facts)
Learn how to place elements in the 1000x1000 **Virtual Artboard** and connect them to real-world sensors.
*   **Concepts:** `x`, `y`, `fact[1]` (Sun Altitude), `mix_oklab`.
*   **Goal:** A sky background that transitions from `#1A1A2E` (Night) to `#A8DADC` (Day). We use `height: 4000` to ensure the sky covers the screen during vertical parallax.

```json
{
  "id": 1,
  "type": "RECT",
  "zOrder": -100,
  "expressions": {
    "x": "500", "y": "500", "width": "1000", "height": "4000",
    "color_primary": "mix_oklab(#1A1A2E, #A8DADC, (fact[1] + 1) / 2)"
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
    "color_primary": "if_gt(fact[16], 0.5, #1A1A2E, #E9C46A)",
    "alpha": "if_gt(fact[1], 0.1, 0.3, 1.0)"
  }
}
```

## Chapter 4: The Tree (Vitality & Animation)
Bringing life to static objects with health data and motion logic.
*   **Concepts:** `fact[9]` (Steps Progress), `oscillate`, `lerp`.
*   **Goal:** The tree foliage grows as you walk (`lerp` with `fact[9]`) and sways gently in the wind.

```json
{
  "id": 14,
  "type": "CIRCLE",
  "zOrder": 30,
  "expressions": {
    "x": "750 + oscillate(0, 10, 0.5, 0)",
    "y": "580",
    "width": "lerp(120, 300, fact[9])",
    "color_primary": "#2A9D8F"
  }
}
```

## Chapter 5: The River & Smoke (High-Performance Paths)
Harnessing the power of the GPU for complex vector geometry.
*   **Concepts:** `PATH` layer type, `resId`, `time`.
*   **Goal:** The river and chimney smoke use `PATH` resources. The smoke's vertical position and transparency are driven by `time` to create a looping animation.

```json
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
}
```

---

## Summary of the Artist's Workflow
1.  **Define Resources:** Add your raw SVG path strings to the `resources` block.
2.  **Layer the Scene:** Arrange your 32 layers from back to front using `zOrder`.
3.  **Bind the Senses:** Use `fact[n]` to make properties (color, scale, position) react to the world.
4.  **Verify:** Ensure all math is zero-allocation and stays within the 1000x1000 artboard (or uses Over-scan for backgrounds).
