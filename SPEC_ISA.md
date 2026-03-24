# Mori Instruction Set Architecture (ISA v1.2)

This document defines the primitive "Machine Code" of the Mori Rule Engine. These OpCodes are executed 60 times per second on a stack-based VM to drive atmospheric visual properties.

## 1. Execution Model
*   **Stack-Based**: All operations pop values from a pre-allocated stack and push results back.
*   **Zero-Allocation**: No objects are created during execution. All values are `Float`.
*   **Pre-Compiled**: JSON strings are parsed ONCE at load-time into `IntArray` bytecode.

---

## 2. Full Instruction Table

| Category | Instruction | Bytecode | Stack Action | Description |
| :--- | :--- | :--- | :--- | :--- |
| **Ingress** | `PUSH_CONST` | `0x01` | `[] -> [f]` | Pushes the next float from bytecode. |
| | `GET_TIME` | `0x02` | `[] -> [f]` | Pushes smooth normalized time (seconds). |
| | `GET_STATE` | `0x03` | `[idx] -> [f]` | Fetches a value from `MoriEngineState`. |
| | `GET_SIGNAL`| `0x04` | `[idx] -> [f]` | Fetches a pre-calculated global signal. |
| **Math** | `ADD` | `0x10` | `[a, b] -> [a+b]` | Binary addition. |
| | `SUB` | `0x11` | `[a, b] -> [a-b]` | Binary subtraction. |
| | `MUL` | `0x12` | `[a, b] -> [a*b]` | Binary multiplication. |
| | `DIV` | `0x13` | `[a, b] -> [a/b]` | Binary division. |
| | `MOD` | `0x14` | `[a, b] -> [a%b]` | Binary modulo. |
| | `SIN` | `0x20` | `[f] -> [sin(f)]` | Sine wave (-1 to 1). |
| | `COS` | `0x21` | `[f] -> [cos(f)]` | Cosine wave (-1 to 1). |
| | `ABS` | `0x22` | `[f] -> [abs(f)]` | Absolute value. |
| | `SQRT`| `0x23` | `[f] -> [sqrt(f)]`| Square root. |
| **Macros** | `REMAP` | `0x30` | `[v, iMin, iMax, oMin, oMax] -> [f]` | Normalized remapping. |
| | `CLAMP` | `0x31` | `[v, min, max] -> [f]` | Constrain value to range. |
| | `STEP`  | `0x32` | `[v, edge] -> [0 or 1]` | Binary threshold logic. |
| | `LERP`  | `0x33` | `[a, b, t] -> [f]` | Linear interpolation. |
| **Logic** | `IF_GT` | `0x40` | `[a, b, t, f] -> [t or f]` | If a > b return t, else f. |
| | `AND` | `0x41` | `[a, b] -> [0 or 1]` | Boolean AND (1.0 = true). |
| | `OR`  | `0x42` | `[a, b] -> [0 or 1]` | Boolean OR (1.0 = true). |
| **Atmosphere**| `NOISE` | `0x50` | `[seed] -> [f]` | Coherent Perlin-style noise. |
| | `MIX_OKLAB`| `0x51` | `[cA, cB, t] -> [color]` | Perceptual color blending. |

---

## 3. State Field Index Map (`GET_STATE`)

| Index | Field | Description |
| :--- | :--- | :--- |
| `0` | `timeSeconds` | Continuous frame-time. |
| `1` | `chronosSunAltitude` | -1.0 (Midnight) to 1.0 (Noon). |
| `2` | `energyBatteryLevel` | 0.0 to 1.0. |
| `3` | `energyIsCharging` | 0.0 (False) or 1.0 (True). |
| `4` | `vitalityStepsProgress` | 0.0 to 1.0 (Daily Goal). |
| `5` | `energyThermalStress` | 0.0 (Cold) to 1.0 (Shutdown). |
| `6` | `zenSocialNoise` | 0.0 (Calm) to 1.0 (Congested). |
| `7` | `atmosLightLevel` | 0.0 (Dark) to 1.0 (Bright). |
