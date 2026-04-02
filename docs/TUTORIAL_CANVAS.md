# Tutorial: "The Childhood Canvas"

Welcome to the Mori Artist's Guide. In this tutorial, you will build a classic "Hello World" scenery (Mountain, Sun, River, House, Tree) while learning the Mori Biome DSL and the high-performance visual pipeline.

---

## Chapter 1: The Sky & Sun (Coordinate Space & Facts)
Learn how to place elements in the 1000x1000 virtual artboard and connect them to real-world sensors.
*   **Concepts:** `x`, `y`, `fact[1]` (Sun Altitude).
*   **Goal:** A sun that rises and sets with the real time of day.

---

## Chapter 2: The Mountains (Z-Order & Primitives)
Understand depth and geometric shapes.
*   **Concepts:** `zOrder`, `TRIANGLE`, `RECT`.
*   **Goal:** Layering static mountain silhouettes behind the foreground.

---

## Chapter 3: The House (Textures & The Atlas)
Introduction to the high-fidelity visual assets.
*   **Concepts:** `res_id`, `Bitmap Texture Atlas`.
*   **Goal:** Using hand-painted brick and roof textures instead of flat colors.

---

## Chapter 4: The Tree (Logic & Oscillate)
Bringing life to static objects with motion logic.
*   **Concepts:** `oscillate`, `rotation`, `scale`.
*   **Goal:** A tree that sways gently in the virtual wind.

---

## Chapter 5: The River (AGSL Shaders)
Harnessing the power of the GPU for atmospheric effects.
*   **Concepts:** `SHADER` type, AGSL Uniforms.
*   **Goal:** A shimmering river with procedural water ripples.

---

## Chapter 6: The Atmosphere (OKLab & Color)
Creating a vibrant, reactive color palette.
*   **Concepts:** `mix_oklab`, `color_primary`.
*   **Goal:** A seamless day-to-night transition with vibrant sunsets.

---

## Reference Checklist
*   [SPEC_DSL.md](SPEC_DSL.md) - Full language reference.
*   [SPEC_ISA.md](SPEC_ISA.md) - Instruction set reference.
