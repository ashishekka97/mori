# Mori: Biome DSL Specification (v1.0)

This document provides a comprehensive guide to defining Mori Biomes. It covers the file structure, logic system, data access, and rendering properties required to create high-performance, data-driven live wallpapers.

---

## 1. Biome File Structure

A Mori Biome is defined as a single JSON file. The structure is designed for clarity and easy expansion.

### 1.1 The Root Object
The root of the JSON file contains the metadata and the list of rendering layers.

| Key | Type | Description |
| :--- | :--- | :--- |
| `id` | String | A unique identifier for the biome (e.g., `zen_forest`). |
| `name` | String | The human-readable name displayed in the UI. |
| `description`| String | (Optional) A brief explanation of the biome's behavior. |
| `layers` | Array | A collection of [Layer Objects](#12-the-layer-object). |

### 1.2 The Layer Object
Each entry in the `layers` array defines a single visual element or logic processor.

| Key | Type | Description |
| :--- | :--- | :--- |
| `id` | Int | A unique ID for this specific layer. |
| `type` | String | The geometric primitive: `RECT`, `CIRCLE`, or `TRIANGLE`. |
| `zOrder` | Int | Determines depth. Higher values are drawn in front. |
| `expressions`| Map | A collection of [DSL Expressions](#2-language-foundations). |

### 1.3 Performance Budget: The Golden Rule
To ensure maximum battery efficiency and maintain 60fps on all devices, the Mori Engine follows a strict **Power Budget**:

*   **Layer Count:** Maximum **16 layers** per biome. Any layers defined beyond this limit in the JSON will be ignored by the Engine.
*   **Z-Order Range:** Supports standard signed integers. Layers are sorted and drawn from most-negative (bottom-most) to most-positive (top-most).
*   **Duplicate IDs:** Layer IDs must be unique within a single Biome. If duplicates occur, the Engine will process them in their array order.

### 1.4 The Expressions Map
The `expressions` key is where the "Brain" of the layer lives. It maps **Visual Properties** to **Math Logic**.
*   **Key:** Must be a valid property name (e.g., `x`, `alpha`, `color_primary`).
*   **Value:** A string containing the Mori DSL math (e.g., `500 + sin(time) * 100`).

### 1.5 Geometry Behaviors by Type
Different layer types interpret the `width` and `height` properties differently:

| Type | interpretation |
| :--- | :--- |
| **`RECT`** | Standard rectangle. Uses both `width` and `height`. |
| **`CIRCLE`** | Uses `width` as the diameter. `height` is ignored to ensure perfect circularity. |
| **`TRIANGLE`** | Renders an equilateral triangle fitting within the `width` and `height` bounding box. |
| **`PATH`** | (Phase 7) Reserved for complex SVG-like paths. |

### 1.6 Resource Registry (Phase 7)
Mori is designed to be asset-rich. While currently restricted to geometric primitives, the Biome schema includes a reserved `resources` block for external assets.

*   **Bitmaps:** High-fidelity hand-painted textures.
*   **Shaders (AGSL):** Custom GPU logic for materials (Glass, Water, Light). 
    *   *Note:* Shaders are **Logic Resources**. They are defined once and can be applied to any Layer type (e.g., a `RECT` layer with a `glass_material` shader).
*   **Referencing:** Layers link to these resources using a `res_id`, allowing a single asset to be reused across multiple layers with different DSL-driven effects.

### 1.7 Special Layer Types
*   **`SHADER`**: A full-screen atmospheric pass that uses an AGSL resource but does not require geometric bounds (e.g., Color Grading, Vignette, Fog).

---

## 2. Language Foundations

### 2.1 The Rule-Engine Architecture
The Mori DSL is a stack-based expression language. Unlike standard scripting, it is designed for **deterministic, zero-allocation execution**. Every expression in your JSON is compiled into bytecode that the Engine's Virtual Machine (VM) runs at 60 frames per second.

### 2.2 Syntax: Infix Notation
We use standard infix math, which is automatically converted to postfix (Reverse Polish Notation) during compilation.
*   **Supported Operators:** `+`, `-`, `*`, `/`, `%`
*   **Grouping:** Use parentheses `()` to enforce order of operations.
*   **Example:** `(500 + sin(time) * 100) / 2`

### 2.3 Logical Operators
In addition to math, the DSL supports logical comparisons:
*   **Boolean Logic:** `&&` (AND), `||` (OR).
*   **Behavior:** Mori treats any value `> 0.5` as TRUE and `<= 0.5` as FALSE.
*   *Example:* `(fact[7] && fact[16])` (True if Charging AND DND is active).

### 2.4 Inter-Layer Signals (`signal[n]`)
Signals allow layers to communicate. A "Logic Layer" can calculate a complex value and store it in a signal slot (0-7), which other layers can then read.
*   **Keyword:** `signal[index]`
*   *Excellence Note:* This prevents redundant math. Calculate "Wind Speed" once in Layer 1, and read it in all Tree layers.

### 2.5 The Engine Lifecycle
Designers should understand the two-phase rendering cycle:
1.  **Update Phase:** The Rule Engine executes your DSL bytecode. It reads Facts, performs Math, and writes to the Property Buffer.
2.  **Draw Phase:** The Engine reads the finalized Property Buffer and translates it into pixels on the screen.

### 2.6 Coordinate Space & Scaling
Mori uses a **Virtual Reference Space** of 1000x1000 units to ensure biomes are portable across different screen resolutions.

#### 2.6.1 The "Center-Crop" Strategy
Because real phones are tall rectangles (e.g., 9:19), and our virtual space is a square (1:1), the engine treats the 1000x1000 space as an **Artboard**:

1.  **Uniform Scaling:** 1000 virtual units always equals 100% of the screen width. This ensures shapes maintain their intended aspect ratio (circles stay circles).
2.  **Vertical Centering:** The virtual artboard is anchored to the center of the physical screen.
3.  **The Safe Zone (y: 200 to 800):** Content placed within this vertical range is guaranteed to be visible on almost any modern smartphone aspect ratio.
4.  **The Over-scan Area:** Background elements (Sky, Clouds, Ground) should extend their `height` to **2000 units** to ensure they bleed off the top and bottom edges of even the tallest displays.


#### 2.6.2 Edge-to-Edge Programming
Standard 1:1 design stays in the center. If you need to anchor elements exactly to the physical top or bottom of the screen, use the **Aspect Ratio Fact (`fact[24]`)**.

*   **True Top (Y):** `500 - (fact[24] * 500)`
*   **True Bottom (Y):** `500 + (fact[24] * 500)`

*Example:* To place a 50px tall bar exactly at the top of the screen:
`"y": "500 - (fact[24] * 500) + 25"`

#### 2.6.3 Responsive Design & Orientation
Mori biomes can react to device rotation using the **Orientation Fact (`fact[25]`)**.

*   **Logic:** Use `if_gt(fact[25], 0.5, [LANDSCAPE_VALUE], [PORTRAIT_VALUE])`.
*   **Best Practice:** On tall phones, Landscape mode significantly crops the vertical "Safe Zone." Use responsive math to shift critical elements toward the center line.

### 2.7 Data Types & Precision
*   **Floating Point:** All math is performed with 32-bit float precision.
*   **Exact Hex Colors:** To prevent the precision loss inherent in floating-point math, Mori uses a specialized **Hex Ingress**.
    *   **Format:** `#RRGGBB` or `#AARRGGBB`.
    *   **Example:** `"color_primary": "#FF5252"`
    *   *Excellence Note:* Hex strings are parsed directly into 32-bit ARGB integers, ensuring your colors are mathematically exact.

---

## 3. The Brain: API Reference (Functions)

Mori provides a suite of high-value "Macro-OpCodes" for atmospheric animations. All functions are designed for zero-allocation performance.

### 3.1 Basic Math
*   **`abs(v)`**: Returns the absolute value.
    *   *Example:* `abs(sin(time))` (Ensures a value is always positive).
*   **`sqrt(v)`**: Returns the square root. Returns `0.0` for negative values.
    *   *Example:* `sqrt(fact[6])` (Creates a non-linear battery progression).
*   **`sin(rad)` / `cos(rad)`**: Standard trigonometric functions.
    *   *Example:* `500 + sin(time) * 100` (Smoothly moves an object 100 units around center).

### 3.2 Range & Constraint
*   **`clamp(value, min, max)`**: Constrains a value to a specific range.
    *   *Example:* `"alpha": "clamp(fact[18], 0.2, 0.8)"` (Visibility stays between 20% and 80%).
*   **`step(edge, v)`**: Returns `1.0` if `v >= edge`, otherwise `0.0`. Useful for simple on/off triggers.
    *   *Example:* `step(0.5, fact[6])` (Returns 1.0 only if battery is above 50%).
*   **`lerp(a, b, t)`**: Linear interpolation between `a` and `b` by factor `t` (0.0 to 1.0).
    *   *Example:* `lerp(100, 500, fact[9])` (Moves an object from 100 to 500 as step goal is reached).
*   **`remap(v, iL, iH, oL, oH)`**: Maps a value from one range to another.
    *   *Example:* `"y": "remap(fact[6], 0, 1, 1000, 0)"` (Maps 0..1 battery to 1000..0 pixels).

### 3.3 Motion & Animation
*   **`oscillate(center, amplitude, speed, phase)`**: Creates a smooth repeating pulse.
    *   *Example:* `"width": "oscillate(200, 50, 0.5, 0)"` (Pulses between 150 and 250 units every 2s).
*   **`if_gt(value, threshold, then, else)`**: Returns `then` if `value > threshold`, else `else`.
    *   *Example:* `"color_primary": "if_gt(fact[7], 0.5, #FFFF00, #333333)"` (Yellow if charging).

### 3.4 Easing Functions
All easing functions take a factor `t` (0.0 to 1.0) and return a transformed factor.
*   **`ease_in_out(t)`**: Standard smooth start and end.
*   **`ease_back(t)`**: Over-shooting "back" easing.
*   **`ease_elastic(t)`**: An oscillating "spring" easing.

### 3.5 Perceptual Atmosphere
*   **`noise(v)`**: Returns a deterministic, pseudo-random hash between `0.0` and `1.0`.
*   **`mix_oklab(colorA, colorB, t)`**: Interpolates colors in OKLab space.
    *   *Example:* `"color_primary": "mix_oklab(#0D0221, #87CEEB, (fact[1] + 1) / 2)"`

---

## 4. The Senses: Fact Ingress Registry

The `fact[n]` array is the artist's window into the real world. All values are normalized between `0.0` and `1.0` unless otherwise noted.

### 4.1 Chronos (Time & Seasons)
| Index | Name      | Range | Description                                   |
|:-----:|:----------| :---: |:----------------------------------------------|
| **0** | `time_s`  | `raw` | Raw continuous seconds.                       |
| **1** | `sun_alt` | `-1..1` | Sun position (-1.0 = Midnight, 1.0 = Noon).   |
| **2** | `time_p`  | `0..1` | Day progress (0.0 = Midnight, 1.0 = 23:59).   |
| **3** | `moon_p`  | `0..1` | Moon phase (0.0 = New, 1.0 = Full).           |
| **4** | `season`  | `0..1` | Season progress (0.0 = Spring, 1.0 = Winter). |

### 4.2 Energy (Power & Thermals)
| Index | Name | Range | Description |
| :---: | :--- | :---: | :--- |
| **6** | `battery` | `0..1` | Device charge percentage. |
| **7** | `charging`| `0/1` | 1.0 if connected to power, else 0.0. |
| **8** | `thermal` | `0..1` | 0.0 (Cool) to 1.0 (Emergency throttling). |

### 4.3 Vitality & Zen
| Index | Name | Range | Description |
| :---: | :--- | :---: | :--- |
| **9** | `steps_p` | `0..1` | Progress toward daily step goal. |
| **16**| `dnd`       | `0/1` | 1.0 if Do Not Disturb is active. |
| **18**| `light` | `0..1` | Ambient light intensity. |
| **19**| `pocket`| `0/1` | 1.0 if proximity sensor is blocked. |

### 4.4 Platform Metadata
| Index | Name | Description |
| :---: | :--- | :--- |
| **20** | `kp_index`| Geomagnetic activity index (Atmospheric effects). |
| **21** | `media`   | Current media pulse (if audio is playing). |
| **22** | `alarm`   | Time until the next alarm. |
| **23** | `notifs`  | Unread notification count. |
| **24** | `aspect`  | Physical aspect ratio (`height / width`). Used for [Edge-to-Edge math](#262-edge-to-edge-programming). |
| **25** | `is_land` | 1.0 if the device is in Landscape mode, else 0.0. |
| **26** | `f_ratio` | Field ratio (`width / height`). Useful for horizontal positioning in landscape. |
| **27-31** | `custom`| Reserved for Biome-specific local signal injection. |

---

## 5. The Body: Visual Properties

Visual properties define how a layer is transformed and colored. All properties are calculated in a **1000x1000 Virtual Space** and scaled uniformly based on the screen width.

### 5.1 Transform Properties
| Property | Default | Description |
| :--- | :---: | :--- |
| **`x`** | `0.0` | Horizontal center position (0 to 1000). |
| **`y`** | `0.0` | Vertical center position (0 to 1000). |
| **`width`** | `100.0` | Base width in virtual units. |
| **`height`** | `100.0` | Base height in virtual units. |
| **`scale_x`** | `1.0` | Multiplier for the width. |
| **`scale_y`** | `1.0` | Multiplier for the height. |
| **`rotation`**| `0.0` | Degrees of clockwise rotation around the (x,y) center. |

### 5.2 Style Properties
| Property | Default | Description |
| :--- | :---: | :--- |
| **`alpha`** | `1.0` | Global transparency (0.0 = Invisible, 1.0 = Opaque). |
| **`stroke_width`**| `0.0` | Thickness of the outline. If `> 0`, Mori renders both Fill and Stroke. |
| **`color_primary`**| `#FFFFFF`| The fill color (ARGB Hex format). |
| **`color_secondary`**| `#FFFFFF`| The stroke color (ARGB Hex format). |

### 5.3 Property Defaults & Behavior
If a property is omitted from the `expressions` map, the Engine uses the default value listed above. 
*   **Performance Note:** Omitted properties do not execute the Rule Engine, saving CPU cycles.
*   **Coordinate Note:** Position `(500, 500)` is exactly the center of the 1000x1000 artboard.

---

## 6. Optimization Standards

To maintain **Internal Excellence**, Mori biomes should be designed with the following performance rules in mind:

### 6.1 Logic Efficiency
1.  **Constant Folding:** Math between literal numbers (e.g., `100 + 200`) is solved at compile-time. Use it freely for readability.
2.  **Property Pruning:** If a property (like `rotation` or `stroke_width`) doesn't change, **omit it from the JSON**. The Engine skips the math for any property without an expression, saving precious CPU cycles.
3.  **Signal Layering:** If multiple layers depend on the same complex math (e.g., a "Wind Effect"), calculate it **once** in a background layer and store it in a `signal[n]`. Other layers should read the signal instead of recalculating the math.

### 6.2 Zero-Allocation Performance
1.  **Pre-allocated Stack:** The Engine executes all math on a fixed 32-slot primitive stack. Avoid extremely deep nesting (more than 10-15 levels) to ensure stack safety.
2.  **Ordinal Comparisons:** Using the standardized `LayerType` enum ensures that shape switching in the hot path is a fast integer comparison, not a string check.

### 6.3 Drawing Performance
1.  **Z-Order Stability:** Avoid using dynamic expressions for `zOrder`. Sorting layers is an expensive operation; keep Z-orders static for maximum performance.
2.  **The Golden Rule of 16:** Stay within the **16-layer limit** to guarantee 60fps and low battery impact across all Android devices.
3.  **Primitive Paths:** Prefer `RECT` and `CIRCLE` over `TRIANGLE` (Polygon) for simple UI elements, as they utilize the GPU's optimized hardware paths.

---
