# Mori Instruction Set Architecture (ISA v1.4)

This document defines the primitive "Machine Code" of the Mori Rule Engine. These OpCodes are executed 60 times per second on a stack-based VM to drive atmospheric visual properties.

## 1. Execution Model
*   **Stack-Based**: All operations pop values from a pre-allocated stack and push results back.
*   **Zero-Allocation**: No objects are created during execution. All values are `Float`.
*   **Pre-Compiled**: JSON strings are parsed ONCE at load-time into `IntArray` bytecode.

---

## 2. The Instruction Set

| Category | Instruction | Bytecode | Purpose | Example |
| :--- | :--- | :--- | :--- | :--- |
| **Data Ingress** | `PUSH_CONST` | `0x01` | Loads a hardcoded number from the JSON. | `2.5`, `360.0` |
| | `GET_TIME` | `0x02` | Fetches the smooth 60fps frame-time. | Driving constant motion. |
| | `GET_STATE` | `0x03` | Fetches real-world data (Battery, Sun, Steps). | Making layers reactive. |
| | `GET_SIGNAL`| `0x04` | Reuses a calculation from another layer. | Syncing cloud & shadow. |
| **Core Math** | `ADD`, `SUB` | `0x10-11`| Basic coordinate offsets. | `x + offset` |
| | `MUL`, `DIV` | `0x12-13`| Scaling and rate adjustment. | `time * speed` |
| | `SIN`, `COS` | `0x20-21`| Advanced geometric or orbital patterns. | Circular paths. |
| | `ABS`, `SQRT`| `0x22-23`| Non-linear math requirements. | Distance calculations. |
| **Macros** | `REMAP` | `0x30` | **The Primary Tool**: Scales a sensor to a visual. | `Battery (0..1)` -> `Alpha (1..0)` |
| | `CLAMP` | `0x31` | Keeps values within safe, visible ranges. | Prevents "exploding" scales. |
| | `STEP` | `0x32` | Binary triggers for goals or thresholds. | `if steps > 10000` |
| | `LERP` | `0x33` | Simple linear transitions. | Moving from point A to B. |
| | `OSCILLATE`| `0x34` | **Vibrant Shortcut**: For swaying and pulsing. | Trees in wind, light heartbeats. |
| **Logic** | `IF_GT` | `0x40` | Decision making ("If Day then A else B"). | Dynamic asset swapping. |
| | `AND`, `OR` | `0x41-42`| Combining multiple sensor conditions. | `isNight AND isCharging` |
| **Atmosphere**| `NOISE` | `0x50` | Natural, organic randomness. | Wind gusts, water ripples. |
| | `MIX_OKLAB`| `0x51` | **Mori Standard**: Vibrant color blending. | Mud-free sunsets. |
| **Motion** | `EASE_IN_OUT`| `0x60` | Smooth, cinematic start/stop. | Sun/Moon trajectories. |
| | `EASE_BACK` | `0x61` | Natural "overshoot" for organic objects. | Bending tree branches. |
| | `EASE_ELASTIC`| `0x62` | Snappy, bouncy physical reactions. | Notification "pops". |

---

## 3. State Field Index Map (`GET_STATE`)

| Index | Field | Description | Category |
| :--- | :--- | :--- | :--- |
| `0` | `timeSeconds` | Continuous frame-time. | Environmental |
| `1` | `sunAltitude` | -1.0 (Midnight) to 1.0 (Noon). | Environmental |
| `2` | `batteryLevel` | 0.0 to 1.0. | Environmental |
| `3` | `isCharging` | 0.0 (False) or 1.0 (True). | Environmental |
| `4` | `stepsProgress`| 0.0 to 1.0 (Daily Goal). | Human Pulse |
| `5` | `thermalStress`| 0.0 (Cold) to 1.0 (Shutdown). | Device Pulse |
| `6` | `socialNoise` | 0.0 (Calm) to 1.0 (Congested). | Digital Noise |
| `7` | `lightLevel` | 0.0 (Dark) to 1.0 (Bright). | Environmental |
| `8` | `dailyScreenTime`| 0.0 to 1.0 (Target limit). | Personal Focus |
| `9` | `restQuality` | 0.0 to 1.0 (Sleep depth). | Human Pulse |
| `10`| `kpIndex` | 0.0 to 1.0 (Aurora Strength).| Global Pulse |
| `11`| `mediaPulse` | 0.0 to 1.0 (Active Beat). | Current Vibe |
| `12`| `alarmDistance`| 1.0 to 0.0 (Time to alarm). | Future Pulse |
| `13`| `chargingSpeed`| 0.0 to 1.0 (Fast/Slow). | Device Pulse |
| `14`| `notificationCount`| 0.0 to 1.0 (Unread density).| Digital Noise |
| `15`| `temperature` | Normalized (-30C..+40C). | Environmental |
