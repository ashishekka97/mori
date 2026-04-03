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
*   **Goal:** A sky background that transitions seamlessly from `#0D0221` (Night) to `#87CEEB` (Day) using `mix_oklab`. We use the **Over-scan Area** (setting `height: 4000`) for the sky to bleed off the edges of tall displays safely.
*   **The Sun:** A `CIRCLE` whose `y` position remaps based on the Sun Altitude (`fact[1]`).

## Chapter 2: The Ground & Mountains (Z-Order & Aspect Ratio)
Understand depth, geometric shapes, and responsive anchoring.
*   **Concepts:** `zOrder`, `TRIANGLE`, `fact[24]` (Aspect Ratio).
*   **Goal:** Layering static mountain silhouettes behind the foreground (`zOrder: -15`). We anchor the green ground `RECT` to the absolute bottom of the physical screen by using the **Aspect Ratio Fact** (`fact[24]`) to calculate the exact `y` coordinate.

## Chapter 3: The House (Logical Toggles)
Introduction to boolean logic and conditional rendering.
*   **Concepts:** `if_gt`, `fact[16]` (Do Not Disturb).
*   **Goal:** Render the house base as a `RECT`. Add a yellow window `CIRCLE` that only "turns on" (switches color or alpha) when the user's phone is in Do Not Disturb mode (`fact[16] > 0.5`).

## Chapter 4: The Tree (Vitality & Animation)
Bringing life to static objects with health data and motion logic.
*   **Concepts:** `fact[9]` (Steps Progress), `oscillate`, `lerp`.
*   **Goal:** The tree's leafy `CIRCLE` grows from a radius of 100 to 300 as the user reaches their daily step goal (`lerp` with `fact[9]`). The tree continuously sways in the wind using the `oscillate` function on its `rotation`.

## Chapter 5: The River (High-Performance Paths)
Harnessing the power of the GPU for complex vector geometry.
*   **Concepts:** `PATH` layer type, `res_id`.
*   **Goal:** The river uses a complex vector path defined in the **Resource Registry**. By setting `type: "PATH"` and `res_id: 1`, the engine draws the pre-defined river curve without the overhead of full SVG parsing.

## Chapter 6: The Chimney Smoke (Paths & Energy)
Rendering dynamic effects gated by device state.
*   **Concepts:** `PATH` layer type, `fact[7]` (Charging state).
*   **Goal:** A vector smoke wisp (`res_id: 2`) that emerges from the house chimney. Using a conditional expression, the smoke's `alpha` becomes `1.0` only when the device is plugged into power (`fact[7] > 0.5`), otherwise it remains invisible.

---

## Summary of the Artist's Workflow
1.  **Define Resources:** Add your raw SVG path strings to the `resources` block.
2.  **Layer the Scene:** Arrange your 32 layers from back to front using `zOrder`.
3.  **Bind the Senses:** Use `fact[n]` to make properties (color, scale, position) react to the world.
4.  **Verify:** Ensure all math is zero-allocation and stays within the 1000x1000 artboard (or uses Over-scan for backgrounds).
