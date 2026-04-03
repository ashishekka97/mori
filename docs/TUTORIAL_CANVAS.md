# Tutorial: "The Childhood Canvas" (Papercraft Scenery)

Welcome to the Mori Artist's Guide. In this tutorial, you will build a "Hello World" dynamic papercraft scenery. You will learn how to use the Mori Biome DSL, respect the strict power budget, and bind real-world sensor data to high-performance visual pipelines.

---

## The Power Budget & Primitives
Mori is a zero-allocation engine designed for 60fps and minimal battery drain. 
*   **The Rule of 32:** You have a strict limit of 32 layers per biome.
*   **The Solution:** Build complex scenes using basic overlapping primitives (`RECT`, `TRIANGLE`, `CIRCLE`).

## Chapter 1: The Sky & Sun (Coordinate Space & Facts)
Learn how to place elements in the 1000x1000 virtual artboard and connect them to real-world sensors.
*   **Concepts:** `x`, `y`, `fact[1]` (Sun Altitude), `mix_oklab`.
*   **Goal:** A sky background that transitions seamlessly from `#0D0221` (Night) to `#87CEEB` (Day) using `mix_oklab`, and a Sun/Moon circle whose position remaps based on `fact[1]`. We use the height `2000` trick for the sky to over-scan tall displays safely.

## Chapter 2: The Ground & Mountains (Z-Order & Aspect Ratio)
Understand depth, geometric shapes, and responsive anchoring.
*   **Concepts:** `zOrder`, `TRIANGLE`, `fact[24]` (Aspect Ratio).
*   **Goal:** Layering static mountain silhouettes behind the foreground (`zOrder: -15`), and anchoring the green ground `RECT` to the absolute bottom of the physical screen using `fact[24]`.

## Chapter 3: The House (Logical Toggles)
Introduction to boolean logic.
*   **Concepts:** `if_gt`, `fact[16]` (Do Not Disturb).
*   **Goal:** Render the house base. Add a yellow window `CIRCLE` that only turns on when the user's phone is in Do Not Disturb mode (`fact[16] > 0.5`).

## Chapter 4: The Tree (Vitality & Animation)
Bringing life to static objects with health data and motion logic.
*   **Concepts:** `fact[9]` (Steps Progress), `oscillate`, `lerp`.
*   **Goal:** The tree's leafy `CIRCLE` grows from a radius of 100 to 300 as the user reaches their daily step goal (`lerp` with `fact[9]`). The tree continuously sways in the wind using the `oscillate` function on its rotation.

## Chapter 5: The River (Paths)
Harnessing the power of the GPU for atmospheric effects.
*   **Concepts:** `PATH` resource type.
*   **Goal:** The river uses a predefined path string (`res_id: 1`).

## Chapter 6: The Chimney Smoke (Paths & Energy)
Rendering high-performance vector paths dynamically.
*   **Concepts:** `PATH` resource type, `fact[7]` (Charging state).
*   **Goal:** A vector smoke wisp (`res_id: 2`) that emerges from the house chimney. It only becomes visible (`alpha: 1.0`) when the device is plugged into power (`fact[7]`).
