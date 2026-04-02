---
name: ui-expert
description: Expert in the Pulse Design System, Jetpack Compose UI components, and stateless UI architecture for the Mori platform.
---

# Mori UI Expert

## Pulse Design System
- **Pure Compose**: Strictly limited to the `:ui` module. No ViewModels, no XML.
- **Stateless**: UI components must be pure and stateless.
- **PulseTheme**: The universal theme wrapper driven by the Engine's state.

## Design Constraints
- **Zero Knowledge**: The UI layer must not know about the Mori rendering engine internals.
- **Satoshi Font**: Use the Satoshi font family for all UI elements (Light, Regular, Medium, Bold, Black).
- **Interactive Feedback**: Ensure modern, interactive, and polished feedback through consistent spacing and typography.

## Key Files
- `ui/src/main/java/me/ashishekka/mori/ui/theme/PulseTheme.kt`
- `ui/src/main/java/me/ashishekka/mori/ui/components/`
